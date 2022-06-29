package com.blog.quark.configure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.blog.quark.common.Password;
import com.blog.quark.common.util.StringUtil;

import static com.blog.quark.common.util.Constant.WINDOWS;
import static com.blog.quark.common.util.Constant.LINUX;
import static com.blog.quark.common.util.Constant.UNIX;
import static com.blog.quark.id.generator.SnowflakeId.WORKER_ID_MAX_VALUE;


@ConfigurationProperties(prefix = "spring.quark")
public class QuarkProperties {
    
    private FileServer fileServer;
    private KeyGenerator keyGenerator;
    private Crypto crypto;

    public FileServer getFileServer() {
        return fileServer;
    }


    public QuarkProperties setFileServer(FileServer fileServer) {
        this.fileServer = fileServer;
        return this;
    }


    public KeyGenerator getKeyGenerator() {
        return keyGenerator;
    }


    public QuarkProperties setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
        return this;
    }

    public Crypto getCrypto() {
        return this.crypto;
    }


    public QuarkProperties setCrypto(Crypto crypto) {
        this.crypto = crypto;
        return this;
    }





    public static class FileServer {
        private String os;
        private String hostname;
        private int port;
        private String username;
        private Password password;
        private String ftpProxyBaseUrl;
        
        public String getOs() {
            if (null == os) {
                this.os = LINUX;
            }
            return os;
        }
        public FileServer setOs(String os) {
            this.os = adjustmentOs(os);
            return this;
        }
        public String getHostname() {
            return hostname;
        }
        public FileServer setHostname(String hostname) {
            this.hostname = hostname;
            return this;
        }
        public int getPort() {
            return port;
        }
        public FileServer setPort(int port) {
            verificationRangeIn(port, 22, 65535);
            this.port = port;
            return this;
        }
        public String getUsername() {
            return username;
        }
        public FileServer setUsername(String username) {
            this.username = username;
            return this;
        }
        public Password getPassword() {
            return password;
        }
        public FileServer setPassword(Password password) {
            this.password = password;
            return this;
        }
        public String getFtpProxyBaseUrl() {
            return this.ftpProxyBaseUrl;
        }
        public FileServer setFtpProxyBaseUrl(String ftpProxyBaseUrl) {
            this.ftpProxyBaseUrl = ftpProxyBaseUrl;
            return this;
        }
        
        
        private String adjustmentOs(String os) {
            if (StringUtil.isEmptyOrWhitespaceOnly(os)) {
                os = LINUX;
            } else {
                switch (os) {
                case WINDOWS:
                    os = WINDOWS;
                    break;
                case UNIX:
                case LINUX:
                default:
                    os = LINUX;
                    break;
                }
            }
            return os;
        }
        
        
        public boolean isWindows() {
            return WINDOWS.equals(getOs());
        }
    }


    public static class KeyGenerator{
        private Snowflake snowflake;

        public Snowflake getSnowflake() {
            return snowflake;
        }

        public KeyGenerator setSnowflake(Snowflake snowflake) {
            this.snowflake = snowflake;
            return this;
        }
        
        
    }
    
    
    public static class Crypto{
        private Aes aes;
        private Rsa rsa;
        public Aes getAes() {
            return this.aes;
        }
        public Crypto setAes(Aes aes) {
            this.aes = aes;
            return this;
        }
        public Rsa getRsa() {
            return this.rsa;
        }
        public Crypto setRsa(Rsa rsa) {
            this.rsa = rsa;
            return this;
        }
        
    }
    
    
    public static class Aes {
        private String type;
        private String key;

        public String getKey() {
            return this.key;
        }

        public Aes setKey(String key) {
            this.key = key;
            return this;
        }

        public String getType() {
            return this.type;
        }

        public Aes setType(String type) {
            this.type = type;
            return this;
        }
        
    }
    
    
    public static class Rsa {
        private String type;
        private String publicKey;
        private String privateKey;
        public String getPublicKey() {
            return this.publicKey;
        }
        public Rsa setPublicKey(String publicKey) {
            this.publicKey = publicKey;
            return this;
        }
        public String getPrivateKey() {
            return this.privateKey;
        }
        public Rsa setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
            return this;
        }
        public String getType() {
            return this.type;
        }
        public Rsa setType(String type) {
            this.type = type;
            return this;
        }
        
    }
    
    public static class Snowflake {
        private int workId;

        public int getWorkId() {
            return workId;
        }

        public Snowflake setWorkId(int workId) {
            verificationRangeIn(workId, 0, (int) WORKER_ID_MAX_VALUE);
            this.workId = workId;
            return this;
        }

    }
    
    
    // validate range in: [min, max]
    private static void verificationRangeIn(final int current, int min, int max) {
        if (Math.max(min, current) != Math.min(current, max)) {
            throw new IllegalArgumentException(String.format("The given number %d can't be greater than %d or less than %d", current, max, min));
        }
    }
    
}
