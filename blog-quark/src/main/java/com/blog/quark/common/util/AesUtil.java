package com.blog.quark.common.util;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
/*
 * How do you get AES key?
 * Setup:
 * Generate a random 128-bit key (k1), a random 128-bit IV, and a random salt (64 bits is probably sufficient).
 * Use PBKDF2 to generate a 256-bit key from your password and the salt, then split that into two 128-bit keys (k2, k3).
 * Use k2 to AES encrypt k1 using the random IV.
 */
public final class AesUtil {
    
    private final static String ALGORITHM_NAME = "AES";
    
    /*
     * HMAC: Hash-based Message Authentication Code
     * See: http://javadoc.iaik.tugraz.at/iaik_jce/current/iaik/pkcs/pkcs5/PBKDF2.PBKDF2WithHmacSHA1.html
     * PBKDF2.PBKDF2WithHmacSHA1: will produce a hash length of 160 bits.
     * PBKDF2.PBKDF2WithHmacSHA224:
     * PBKDF2.PBKDF2WithHmacSHA256:
     * PBKDF2.PBKDF2WithHmacSHA384:
     * PBKDF2.PBKDF2WithHmacSHA512: will produce a hash length of 512 bits.
     * 
     * 
     * NOTE: 
     *          PBEKeySpec(char[] password, byte[] salt, int iterationCount, int keyLength)
     *      The parameter keyLength is used to indicate the preference on key length for variable-key-size ciphers.
     *      The actual key size depends on each provider's implementation. 
     *      For example:
     *          PBEKeySpec(password, salt, 10000, 512) doesn't mean you will be using SHA1 to generate a keyLength of 512. It simply means that
     *          SHA1 supports up to a maximum of 160 bits. You cannot exceed that.
     *      SHA-256 are appropriate for protecting classified information up to the SECRET level. 
     *      SHA-384 are necessary for the protection of TOP SECRET information.
     * See: https://www.it1352.com/1868437.html
     */
    // private final static String PBKDF2_ALGORITHM_NAME = "PBKDF2WithHmacSHA256";
    
    /* 
     * 推荐使用AES-GCM来替换AES-CBC。 See: https://blog.csdn.net/weixin_39680609/article/details/111159600
     * AES/CBC/PKCS5Padding
     * AES/GCM/NoPadding
     */
    private final static String AES_CIPHER_PADDING = "AES/GCM/NoPadding";
    //private final static String AES_CIPHER_PADDING = "AES/CBC/PKCS5Padding";
    
    
    // AES CBC mode 加密迭代次数
    //private final static int AES_CBC_ITERATION_COUNT = 65536;
    
    // GCM uses a key size of 128, 192 or 256 bits according to AES, and the block size of 128 bits. 
    // The initialization vector (iv) is restricted to lengths less than or equal to 264-1 in multiples of 8
    // IV of any size For GCM a 12 byte IV is strongly suggested as other IV lengths will require additional calculations.
    // AES/GCM 中推荐的IV设置为12bytes(96bit), See more: 
    //        https://crypto.stackexchange.com/questions/41601/aes-gcm-recommended-iv-size-why-12-bytes
    //        https://www.jiamisoft.com/blog/5223-ghashhanshuwnagluojiamisuanfa.html
    // 初始向量长度12bytes(96bits)
    public final static int AES_GCM_IV_LEN = 12;
    // must be one of {128bit, 120bit, 112bit, 104bit, 96bit}
    public final static int AES_GCM_TAG_LEN = 16;
    // AES key的长度: 32bytes(256 bits)
    private final static int AES_KEY_LEN = 32;
    
    
    private AesUtil() {}
    
    public static SecretKey generateKey(final String base64String) {
        return generateKey(Base64.decodeBase64(base64String));
    }
    
    private static SecretKey generateKey(final byte[] utf8BytesKey) {
        // 密钥长度不够，则使用以下数据进行补位
        int plus = AES_KEY_LEN - utf8BytesKey.length;
        byte[] raw = new byte[AES_KEY_LEN];
        byte[] plusBytes = null;
        switch(AES_KEY_LEN){
            // 128bit
            case 16: 
                plusBytes = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f};
                break;
            // 192bit
            case 24:
                plusBytes = new byte[]{0x00, 0x02, 0x04, 0x08, 0x20, 0x40, 0x7f, 0x36, 0x6c, 0x5d, 0x4d, 0x5a, 0x2f, 0x6b, 0x63, 0x6c, 0x35, 0x6a, 0x74, 0x7d, 0x7a, 0x6f, 0x19, 0x39};
                break;
            // 256bit
            default:
                plusBytes = new byte[]{0x08, 0x08, 0x04, 0x0b, 0x02, 0x0f, 0x0b, 0x0c,0x01, 0x03, 0x09, 0x07, 0x0c, 0x03, 0x07, 0x0a, 0x04, 0x0f,0x06, 0x0f, 0x0e, 0x09, 0x05, 0x01, 0x0a, 0x0a, 0x01, 0x09,0x06, 0x07, 0x09, 0x0d};
        }
        for(int i= 0; i < AES_KEY_LEN; i++) {
            if(utf8BytesKey.length > i) {
                raw[i] = utf8BytesKey[i];
            }else {
                raw[i] = plusBytes[plus];
            }
        }
        
        return new SecretKeySpec(raw, ALGORITHM_NAME);
    }


    private static Cipher createGCMCipher(int mode, byte[] iv, final byte[] utf8BytesKey) throws GeneralSecurityException, DecoderException {
        Cipher gcmCipher = Cipher.getInstance(AES_CIPHER_PADDING);
        gcmCipher.getIV();
        gcmCipher.init(mode, generateKey(utf8BytesKey), new GCMParameterSpec(AES_GCM_TAG_LEN * 8, iv));
        return gcmCipher;
    }
    
    /**
     * @Param passphrase AES密钥
     * @Param cipherText 密文
     * @return 明文
     */
    public static String decrypt(String passphrase, String cipherText) throws Exception {
        if (null == passphrase || null == cipherText || passphrase.strip().isEmpty() || cipherText.strip().isEmpty()) {
            return null;
        }
        return decrypt(passphrase, cipherText.getBytes(StandardCharsets.UTF_8));
    }
    

    /**
     * @Param passphrase AES密钥
     * @Param utf8CipherText 密文字节数组（utf8编码）
     * @return 明文
     */
    private static String decrypt(String passphrase, final byte[] utf8CipherText) throws GeneralSecurityException, DecoderException {
        final byte[] data = Base64.decodeBase64(utf8CipherText);
        byte[] iv = new byte[AES_GCM_IV_LEN];
        System.arraycopy(data, 0, iv, 0, AES_GCM_IV_LEN);
        final Cipher gcmCipher = createGCMCipher(Cipher.DECRYPT_MODE, iv, passphrase.getBytes(StandardCharsets.UTF_8));
        byte[] encrypted = new byte[data.length - AES_GCM_IV_LEN];
        System.arraycopy(data, AES_GCM_IV_LEN, encrypted, 0, encrypted.length);
        
        return new String(gcmCipher.doFinal(encrypted), StandardCharsets.UTF_8);
    }
    

    /**
     * 
     * @param passphrase AES密钥
     * @param plainText 明文
     * @return 密文
     * @throws GeneralSecurityException
     * @throws DecoderException
     */
    public static String encrypt(String passphrase, String plainText) throws Exception {
        if (null == passphrase || null == plainText || passphrase.strip().isEmpty() || plainText.strip().isEmpty()) {
            return null;
        }
        return encrypt(passphrase, plainText.strip().getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * 
     * @param passphrase AES密钥
     * @param utf8BytesPlainText 明文字节数组（utf8编码）
     * @return  密文
     * @throws GeneralSecurityException
     * @throws DecoderException
     */
    private static String encrypt(String passphrase, final byte[] utf8BytesPlainText) throws GeneralSecurityException, DecoderException {
        byte[] iv = new byte[AES_GCM_IV_LEN];
        // 获取随机序列，用于生成初始向量
        new SecureRandom().nextBytes(iv);
        final Cipher gcmCipher = createGCMCipher(Cipher.ENCRYPT_MODE, iv, passphrase.getBytes(StandardCharsets.UTF_8));
        final byte[] encrypted = gcmCipher.doFinal(utf8BytesPlainText);
        byte[] data = new byte[AES_GCM_IV_LEN + encrypted.length];
        System.arraycopy(iv, 0, data, 0, AES_GCM_IV_LEN);
        System.arraycopy(encrypted, 0, data, AES_GCM_IV_LEN, encrypted.length);
        
        return new String(Base64.encodeBase64(data, false, true), StandardCharsets.UTF_8);
    }

}
