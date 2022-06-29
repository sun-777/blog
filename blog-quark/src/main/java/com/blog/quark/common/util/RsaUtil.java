package com.blog.quark.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

/**
 * RSA加密、解密算法
 * 
 * 使用RSA密钥、私钥生成工具GenerateRSAKeyUtil获取PublicKey和PrivateKey
 * 
 * @author Sun Xiaodong
 *
 */
public final class RsaUtil {
    
    // See: https://docs.oracle.com/javase/8/docs/technotes/guides/security/SunProviders.html#SunJCEProvider
    //      https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#KeyPairGenerator
    //      https://docs.oracle.com/javase/10/docs/api/javax/crypto/Cipher.html
    private final static String ALGORITHM_NAME = "RSA";
    
    // SunJCE provider. SUN default support "RSA/ECB/*****" Padding
    // RSA/ECB/PKCS1Padding (1024, 2048): "RSA/ECB/PKCS1Padding" has been known to be insecure.
    private final static String RSA_CIPHER_PADDING = "RSA/ECB/OAEPWithSHA-512/256AndMGF1Padding";
    
    //RSA_CIPHER_PADDING支持1024和2048bit，这里使用2048bit。
    public final static int RSA_PRIVATE_KEY_BITS = 2048;
    
    // dynamic block size, decided by RSA_CIPHER_PADDING & RSA_PRIVATE_KEY_BITS
    // JDK will throw an error, tell you the block size like this: [ javax.crypto.IllegalBlockSizeException: Data must not be longer than 190 bytes ]
    private static final int MAX_ENCRYPT_BLOCK_BYTES = 190;
    // decrypt bytes length 256 ( RSA_PRIVATE_KEY_BITS / 8 )
    private static final int MAX_DECRYPT_BLOCK_BYTES = RSA_PRIVATE_KEY_BITS / 8;

    
    private RsaUtil() {}
    
    
    // 取得公钥
    public static PublicKey getPublicKey(String base64PublicKey) {
        try {
            final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(base64PublicKey.getBytes(StandardCharsets.UTF_8)));
            final KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_NAME);
            return keyFactory.generatePublic(keySpec);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // 取得私钥
    public static PrivateKey getPrivateKey(String base64PrivateKey) {
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(base64PrivateKey.getBytes(StandardCharsets.UTF_8)));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_NAME);
            return keyFactory.generatePrivate(keySpec);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    /**
     * 明文加密
     * @param base64PublicKey Base64编码格式的RSA公钥（可使用GenerateRSAKeyUtil工具生成PublicKey，此PublicKey即是Base64编码之后的公钥）
     * @param plainText 明文
     * @return 密文
     * @throws GeneralSecurityException
     */
    public static String encrypt(String base64PublicKey, String plainText) throws Exception {
        if (null == base64PublicKey || null == plainText || base64PublicKey.strip().isEmpty() || plainText.strip().isEmpty()) {
            return null;
        }
        return encrypt(base64PublicKey, plainText.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * 明文加密
     * @param base64PublicKey Base64编码格式的RSA公钥（可使用GenerateRSAKeyUtil工具生成PublicKey，此PublicKey即是Base64编码之后的公钥）
     * @param utf8BytesPlainText 明文字节数组（utf8编码）
     * @return 密文
     * @throws GeneralSecurityException
     * @throws IOException 
     */
    private static String encrypt(String base64PublicKey, byte[] utf8BytesPlainText) throws GeneralSecurityException, IOException {
        Cipher cipher = Cipher.getInstance(RSA_CIPHER_PADDING/* , "SunJCE" */);
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(base64PublicKey));
        
        //分段加密
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final int plainTextBytesLength = utf8BytesPlainText.length;
        final int loops = (int) (plainTextBytesLength / MAX_ENCRYPT_BLOCK_BYTES) + (0 == plainTextBytesLength % MAX_ENCRYPT_BLOCK_BYTES ? 0 : 1);
        for (int i = 0; i < loops; ) {
            baos.write(cipher.doFinal(utf8BytesPlainText, i * MAX_ENCRYPT_BLOCK_BYTES, MAX_ENCRYPT_BLOCK_BYTES));
            // 是最后一次循环
            if (++i + 1 == loops) {
                // 最后需要处理的字节数
                final int remain = 0 == plainTextBytesLength % MAX_ENCRYPT_BLOCK_BYTES ? MAX_ENCRYPT_BLOCK_BYTES : plainTextBytesLength - i * MAX_ENCRYPT_BLOCK_BYTES;
                baos.write(cipher.doFinal(utf8BytesPlainText, i * MAX_ENCRYPT_BLOCK_BYTES, remain));
                break;
            }
        }
        
        return new String(Base64.encodeBase64(baos.toByteArray(), false, true), StandardCharsets.UTF_8);
    }
    
    /**
     * 密文解密
     * @param base64PrivateKey Base64编码格式的RSA私钥（可使用GenerateRSAKeyUtil工具生成PrivateKey，此PrivateKey即是Base64编码之后的私钥）
     * @param cipherText 密文
     * @return 明文
     * @throws GeneralSecurityException
     */
    public static String decrypt(String base64PrivateKey, String cipherText) throws Exception {
        if (null == base64PrivateKey || null == cipherText || base64PrivateKey.strip().isEmpty() || cipherText.strip().isEmpty()) {
            return null;
        }
        return decrypt(base64PrivateKey, cipherText.strip().getBytes(StandardCharsets.UTF_8));
    }
    
    
    /**
     * 密文字节数组解密
     * @param base64PrivateKey Base64编码格式的RSA私钥（可使用GenerateRSAKeyUtil工具生成PrivateKey，此PrivateKey即是Base64编码之后的私钥）
     * @param utf8CipherText 密文字节数组（utf8编码）
     * @return 明文
     * @throws GeneralSecurityException
     * @throws IOException 
     */
    private static String decrypt(String base64PrivateKey, final byte[] utf8CipherText) throws GeneralSecurityException, IOException {
        final byte[] dataBytes = Base64.decodeBase64(utf8CipherText);
        Cipher cipher = Cipher.getInstance(RSA_CIPHER_PADDING/* , "SunJCE" */);
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(base64PrivateKey));
        
        //分段解密
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final int dataBytesLen = dataBytes.length;
        final int loops = (int) (dataBytesLen / MAX_DECRYPT_BLOCK_BYTES) + (0 == dataBytesLen % MAX_DECRYPT_BLOCK_BYTES ? 0 : 1);
        for (int i = 0; i < loops; ) {
            baos.write(cipher.doFinal(dataBytes, i * MAX_DECRYPT_BLOCK_BYTES, MAX_DECRYPT_BLOCK_BYTES));
            
            // 是最后一次循环
            if (++i + 1 == loops) {
                // 最后需要处理的字节数
                final int remain = 0 == dataBytesLen % MAX_DECRYPT_BLOCK_BYTES ? MAX_DECRYPT_BLOCK_BYTES : dataBytesLen - i * MAX_DECRYPT_BLOCK_BYTES;
                baos.write(cipher.doFinal(dataBytes, i * MAX_DECRYPT_BLOCK_BYTES, remain));
                break;
            }
        }

        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }
    
}
