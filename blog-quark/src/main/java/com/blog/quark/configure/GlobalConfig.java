package com.blog.quark.configure;

import static com.blog.quark.common.util.Constant.FILE_SEPARATOR;
import static com.blog.quark.common.util.Constant.MINUS;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.blog.quark.common.enumerate.PreferenceEnum;
import com.blog.quark.common.util.DateAndLocalDateUtil;
import com.blog.quark.configure.properties.QuarkProperties.Crypto;
import com.blog.quark.configure.properties.QuarkProperties.FileServer;
import com.blog.quark.entity.Entity;
import com.blog.quark.entity.Preference;

public final class GlobalConfig {
    
    public static void addMappingTableName(final Class<? extends Entity> clazz, final String mappingTableName) {
        MappingTableHolder.getMappingTableMap().putIfAbsent(clazz, mappingTableName);
    }
    
    
    public static String getMappingTableName(final Class<? extends Entity> clazz) {
        return MappingTableHolder.getMappingTableMap().get(clazz);
    }
    
    public static FileServer getFileServer() {
        return PreferenceSetting.getFileServer();
    }
    
    public static void setFileServer(final FileServer fileServer) {
        PreferenceSetting.initializeFileServer(fileServer);
    }
    
    public static Crypto getCrypto() {
        return PreferenceSetting.getCrypto();
    }
    
    public static void setCrypto(final Crypto crypto) {
        PreferenceSetting.initializeCrypto(crypto);
    }
    
    
    // 默认生成的上传至文件服务器的文件名格式: yyyyMM-uuid.jpg [.png | .gif |.* ]
    public static String getDefaultUploadFileName(final String fileName) {
        final String fileExtName = fileName.substring(fileName.lastIndexOf("."));
        String yyyyMMString = DateAndLocalDateUtil.localDateToString(LocalDate.now(), DateTimeFormatter.ofPattern("yyyyMM"));
        String uuid = UUID.randomUUID().toString().replaceAll(MINUS, "");
        return yyyyMMString.concat(MINUS).concat(uuid).concat(fileExtName);
    }
    
    
    
    
    // 拼接上传图片的完整路径
    public static String getUploadImageFullPath(final String imageFileName) {
        return getUploadImageFullPath(imageFileName, true);
    }
    
    /**
     * 拼接上传图片的完整路径
     * 
     * @param imageFileName  yyyyMM-uuid.jpg | yyyyMM-uuid.png | yyyyMM-uuid.*， yyyyMM为年月
     * @param isConvertPath  是否转换Path（即有路径包含一层日期目录），默认为true。
     *               *** 仅当富文本编辑器中的图片上传时，必须设置为false。***
     *               因为wangeditor富文本编辑器图片插入机制是先上传成功后，再获取文件名（或完整路径）写入编辑器中。
     *               当上传图片后，因各种原因，没有成功提交富文本编辑器中的内容时，已经上传的图片是无效的，对于这些无效的图片，需要定期清除。
     *               所以故意设置富文本编辑器上传的图片目录中无日期层级，方便手动清理文件服务器上的无效图片文件。
     *               当富文本编辑器内容提交后，会有一个额外动作：移动已上传的富文本编辑中的图片至有日期层级的目录中。
     * @return
     */
    public static String getUploadImageFullPath(final String imageFileName, boolean isConvertPath) {
        final boolean isWindows = PreferenceSetting.getFileServer().isWindows();
        Map<PreferenceEnum,Preference> preferenceMap = PreferenceSetting.getPreferenceMap();
        String rootPath = isWindows ? preferenceMap.get(PreferenceEnum.UPLOAD_ROOT_PATH_WIN).getPreferenceValue() 
                                    : preferenceMap.get(PreferenceEnum.UPLOAD_ROOT_PATH_LINUX).getPreferenceValue();
        String imageRelativePath = preferenceMap.get(PreferenceEnum.IMAGE_UPLOAD_PATH).getPreferenceValue();
        String yyyyMMString = imageFileName.substring(0, imageFileName.indexOf(MINUS));
        //拼接文件在文件服务器上的绝对路径
        return new StringBuilder(256)
                .append(rootPath)
                .append(imageRelativePath)
                .append(FILE_SEPARATOR)
                .append(isConvertPath ? yyyyMMString.concat(FILE_SEPARATOR).concat(imageFileName) : imageFileName)
                .toString();
    }
    
    
    /**
     * Nginx反向代理访问FTP上的图片 HTTP URL。格式：http://centos/upload/img/yyyyMM-{uuid}.*
     * 
     * @param imageName
     * @return
     */
    public static String getHttpRefByImageName(String imageName) {
        return getHttpRefByImageName(imageName, true);
    }
    
    public static String getHttpRefByImageName(String imageName, boolean isConvertPath) {
        String imageRelativePath = PreferenceSetting.getPreferenceMap().get(PreferenceEnum.IMAGE_UPLOAD_PATH).getPreferenceValue();
        String ftpProxyBaseUrl = PreferenceSetting.getFileServer().getFtpProxyBaseUrl();
        if (ftpProxyBaseUrl.endsWith(FILE_SEPARATOR)) {
            ftpProxyBaseUrl = ftpProxyBaseUrl.substring(0, ftpProxyBaseUrl.length() - 1);
        }
        String yyyyMMString = imageName.substring(0,imageName.indexOf(MINUS));
        //拼接可通过HTTP访问FTP服务器上的图片文件的url
        return new StringBuffer(256)
                .append(ftpProxyBaseUrl)
                .append(imageRelativePath)
                .append(FILE_SEPARATOR)
                .append(isConvertPath ? yyyyMMString.concat(FILE_SEPARATOR).concat(imageName) : imageName)
                .toString();
    }
    
    
    public static Preference getPreference(final PreferenceEnum preferenceEnum) {
        return PreferenceSetting.getPreferenceMap().get(preferenceEnum);
    }
    
    
    public static void setPreference(final Map<PreferenceEnum, Preference> map) {
        if (!map.isEmpty()) {
            PreferenceSetting.initializePreference(map);
        }
    }

    
    
    static final class PreferenceSetting {
        private static volatile FileServer FILE_SERVER;
        private static volatile Crypto CRYPTO;
        private static volatile Map<PreferenceEnum, Preference> PREFERENCE_MAP;
        
        private static void initializeFileServer(final FileServer fileServer) {
            if (null == FILE_SERVER) {
                synchronized (PreferenceSetting.class) {
                    if (null == FILE_SERVER) {
                        FILE_SERVER = fileServer;
                    }
                }
            }
        }
        
        
        private static void initializePreference(final Map<PreferenceEnum, Preference> map) {
            if (null == PREFERENCE_MAP) {
                synchronized (PreferenceSetting.class) {
                    if (null == PREFERENCE_MAP) {
                        PREFERENCE_MAP = new ConcurrentHashMap<>();
                        PREFERENCE_MAP.putAll(map);
                    }
                }
            }
        }
        
        
        private static void initializeCrypto(final Crypto crypto) {
            if (null == CRYPTO) {
                synchronized (PreferenceSetting.class) {
                    if (null == CRYPTO) {
                        CRYPTO = crypto;
                    }
                }
            }
        }
        
        public static FileServer getFileServer() {
            if (null == FILE_SERVER) {
                throw new NullPointerException("Object must be initialized before use");
            }
            return FILE_SERVER;
        }
        
        
        public static Crypto getCrypto() {
            if (null == CRYPTO) {
                throw new NullPointerException("Object must be initialized before use");
            }
            return CRYPTO;
        }
        
        private static Map<PreferenceEnum, Preference> getPreferenceMap() {
            if (null == PREFERENCE_MAP) {
                throw new NullPointerException("Object must be initialized before use");
            }
            return PREFERENCE_MAP;
        }

    }

    
    
    static final class MappingTableHolder {
        
        private static volatile Map<Class<? extends Entity>, String> MAPPING_TABLE_MAP;
        
        private static Map<Class<? extends Entity>, String> getMappingTableMap() {
            if (null == MAPPING_TABLE_MAP) {
                initialize();
            }
            return MAPPING_TABLE_MAP;
        }
        
        private static void initialize() {
            if (null == MAPPING_TABLE_MAP) {
                synchronized (MappingTableHolder.class) {
                    if (null == MAPPING_TABLE_MAP) {
                        MAPPING_TABLE_MAP = new ConcurrentHashMap<>(64);
                    }
                }
            }
        }
    }
    
    
    
    private GlobalConfig() {}
}
