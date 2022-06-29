package com.blog.quark.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

public final class CommonUtil {

    /**
     * 当保存资金的数据类型为BigDecimal时， 定义金额保留的精度
     */
    public static final int BIGDECAIMAL_SCALE = 3;
    /**
     * 当保存资金的数据类型为BigDecimal时， 定义金额的舍入模式为“四舍五入”
     */
    public static final RoundingMode BIGDECAIMAL_MODE = RoundingMode.HALF_UP;
    
    
    public static BigDecimal fixedScale(BigDecimal balance) {
        if (null != balance) {
            return balance.setScale(BIGDECAIMAL_SCALE, BIGDECAIMAL_MODE);
        }
        return null;
    }
    
    
    /**
     * char[] 转 byte[]
     * @param chars
     * @return
     */
    public static byte[] getBytes(final char[] chars) {
        return StandardCharsets.UTF_8.encode(
                CharBuffer.allocate(chars.length)
                          .put(chars)
                          .flip()
                ).array();
    }
    
    /**
     * byte[] 转 char[]
     * @param bytes
     * @return
     */
    public static char[] getChars(final byte[] bytes) {
        return StandardCharsets.UTF_8.decode(
                ByteBuffer.allocate(bytes.length)
                          .put(bytes)
                          .flip()
                ).array();
    }
    
    
    
    
    
    private CommonUtil() {}
}
