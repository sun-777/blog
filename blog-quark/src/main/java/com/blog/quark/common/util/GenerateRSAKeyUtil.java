package com.blog.quark.common.util;

import static com.blog.quark.common.util.RsaUtil.RSA_PRIVATE_KEY_BITS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.regex.Matcher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * RSA密钥对生成工具（生成的私钥、公钥均为Base64编码格式）
 * 
 * 使用示例：
 *      String path = "e:/key/key.txt";  //公钥、私钥生成目录路径或文件；
 *      GenerateRSAKeyUtil.initKeyPair(path);
 *        
 * @author Sun Xiaodong
 *
 */
public final class GenerateRSAKeyUtil {
    private static final Logger LOG = LoggerFactory.getLogger(GenerateRSAKeyUtil.class);
    
    private GenerateRSAKeyUtil(){}
    
    public static void initKeyPair() throws Exception {
        initKeyPair(null);
    }
    
    
    public static void initKeyPair(String filePath) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(RSA_PRIVATE_KEY_BITS);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        
        String path = validateAndAdjustmentPath(filePath);
        String publicKeyPath = path, privateKeyPath = path;
        final char lastChar = path.charAt(path.length() - 1);
        boolean isFile = false;
        //根据最后一个字符是否为路径分隔符'\\'或'/'，确定路径是文件还是文件夹。
        if (File.separatorChar != lastChar) {
            isFile = true;
        } else {
            publicKeyPath += "publicKey.txt";
            privateKeyPath += "privateKey.txt";
        }
        
        LOG.info("Public Key file path: {}", publicKeyPath);
        LOG.info("Private Key file path: {}", privateKeyPath);
        
        //把公钥和私钥的编码格式转换为Base64文本，并保存
        try (OutputStream publicKeyOutStream = new FileOutputStream(publicKeyPath, isFile); 
                OutputStream privateKeyOutputStream = new FileOutputStream(privateKeyPath, isFile)) {
            final byte[] utf8PublicKeyBytes = Base64.encodeBase64(publicKey.getEncoded());
            final byte[] utf8PrivateKeyBytes = Base64.encodeBase64(privateKey.getEncoded());
            if (null == utf8PublicKeyBytes || null == utf8PrivateKeyBytes || 0 == utf8PublicKeyBytes.length || 0 == utf8PrivateKeyBytes.length) {
                throw new InvalidKeyException("Cannot get an encoding of the key to be wrapped.");
            }
            
            if (isFile) {
                IOUtils.write(new StringBuilder(System.lineSeparator()).append("public key:").append(System.lineSeparator()).toString(), publicKeyOutStream, StandardCharsets.UTF_8);
            }
            // 写入公钥
            IOUtils.write(new String(utf8PublicKeyBytes, StandardCharsets.UTF_8), publicKeyOutStream, StandardCharsets.UTF_8);
            if (isFile) {
                IOUtils.write(new StringBuilder(System.lineSeparator()).append("private key:").append(System.lineSeparator()).toString(), publicKeyOutStream, StandardCharsets.UTF_8);
            }
            //写入私钥
            IOUtils.write(new String(utf8PrivateKeyBytes, StandardCharsets.UTF_8), privateKeyOutputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new Exception(e.getMessage(), e);
        }
    }
    

    /**
     * 检查指定的文件路径是否有效，无效则调整路径为默认的工程目录。
     * 如果返回的是目录，那么目录末尾默认有路径分隔符"/"或"\\"
     * 
     * @param filePath  指定的文件路径
     * @return  返回文件夹或文件路径
     * @throws IOException
     */
    private static String validateAndAdjustmentPath(String filePath) throws IOException {
        final StringBuilder pathBuilder = new StringBuilder(StringUtil.isEmptyOrWhitespaceOnly(filePath) ? "" : filePath.strip());
        if (pathBuilder.length() > 0) {
            //索引OS默认的路径分隔符的位置
            int lastSeparatorIndex = pathBuilder.lastIndexOf(File.separator);
            //将用户设置路径filePath的路径分隔符（"/"或"\\"）替换为OS默认的路径分隔符
            String beReplacedSlash = -1 == lastSeparatorIndex ? ("/".equals(File.separator) ? "\\" : "/") : File.separator;
            String replaced = pathBuilder.toString().replaceAll(Matcher.quoteReplacement(beReplacedSlash), Matcher.quoteReplacement(File.separator));
            pathBuilder.delete(0, pathBuilder.length()).append(replaced);
            //再次索引OS默认的路径分隔符的位置（后续会用到）
            lastSeparatorIndex = pathBuilder.lastIndexOf(File.separator);
            
            File file = new File(pathBuilder.toString());
            Path path = file.toPath();
            boolean pathExists = Files.exists(path);
            //不存在指定的路径，则创建文件 或 创建文件夹
            if (!pathExists) {
                try {
                    //根据 文件扩展名的分隔符"." 是否存在，判断是不是文件
                    final int fileExtensionIndex = pathBuilder.toString().lastIndexOf(".");
                    if (-1 != fileExtensionIndex) {
                        if (-1 != lastSeparatorIndex && lastSeparatorIndex < fileExtensionIndex) {
                            //创建文件之前，先判断文件所处目录是否存在，不存在则创建目录
                            File fileParent = file.getParentFile();
                            if(!fileParent.exists()){
                                fileParent.mkdirs();
                            }
                            pathExists = file.createNewFile();
                        }
                    } else {
                        //创建目录路径
                        pathExists = file.mkdirs();
                    }
                } catch (IOException e) {
                    //创建失败（说明指定的路径不正确）
                    pathExists = false;
                }
            }
            
            //指定路径已经存在
            if (pathExists) {
                if (!Files.isDirectory(path)) { //指定的filePath为不是文件夹
                    if (!Files.isRegularFile(path)) {
                        //如果filePath不是文件夹，也不是文件，则清空pathBuilder
                        pathBuilder.delete(0, pathBuilder.length());
                    }
                } else { //指定的filePath是文件夹
                    final char lastChar = pathBuilder.charAt(pathBuilder.length() - 1);
                    //文件夹路径最后末尾是否有路径分隔符"\\"或"/"，没有则追加
                    if (File.separatorChar != lastChar) {
                        pathBuilder.append(File.separatorChar);
                    }
                }
            } else {
                // 清空
                pathBuilder.delete(0, pathBuilder.length());
            }
        }
        
        //未指定路径 或 指定的路径非法，则使用默认的工程目录
        if (0 == pathBuilder.length()) {
            String path = new File("").getCanonicalPath(); //参数必须为空
            pathBuilder.append(path).append(File.separatorChar);
        }
        
        return pathBuilder.toString();
    }
    

}
