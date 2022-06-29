package com.blog.quark.id.generator;

import java.io.Serializable;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;


/**
 * SnowflakeId - 64bit
 * 　　　　　　　　　　　　　　　　　　　41bit                                     12bit
 *      |　　　　　　　　　　　　　　　时间戳　　　　　　　　　　　　　　　|             |   序列号　　 |
 *    0-00000000 00000000 00000000 00000000 00000000 0-00000000 00-00000000 0000
 *  1bit                                                   |  10bit  |
 *   不用                                                    工作机器id
 * 
 *  1位：不用。二进行最高位为1是负数，实际生成的id一般都使用整数，所以最高位固定为零。
 * 41位：用来记录时间戳（毫秒）。
 * 10位：用来记录工作机器id（包括5位datacenterId和5位workerId）。
 * 12位：序列号。用来记录同毫秒内产生的不同id。
 * 
 * SnowFlake可以保证：
 *      所有生成的id按时间趋势递增
 *      整个分布式系统内不会产生重复id（由workId区分）
 */

public final class SnowflakeId implements Serializable{
    private static final long serialVersionUID = 6311230190104729039L;
    
    /**
     * 开始时间截 2020-12-01 00:00:00:000
     */
    public static final long EPOCH;
    /**
     * 序列占有的位数
     */
    private static final int SEQUENCE_BITS = 12;
    /**
     * 机器id占有的位数
     */
    private static final int WORKER_ID_BITS = 10;
    /**
     * 生成序列的掩码: 4095
     */
    private static final long SEQUENCE_MASK = -1L ^ (-1L << SEQUENCE_BITS); // (1 << SEQUENCE_BITS) - 1
    /**
     * 机器ID左移12位（）
     */
    private static final long WORKER_ID_LEFT_SHIFT_BITS = SEQUENCE_BITS;
    /**
     * 时间截向左移22位(5+5+12)
     */
    private static final long TIMESTAMP_LEFT_SHIFT_BITS = WORKER_ID_LEFT_SHIFT_BITS + WORKER_ID_BITS;
    /**
     * 支持的最大数据标识id，结果是31
     */
    public static final long WORKER_ID_MAX_VALUE = -1L ^ (-1L << WORKER_ID_BITS);  // (1 << WORKER_ID_BITS) - 1
    
    /**
     * 设置默认的震颤周期值为：[0, 1]， 可设置的最大值为：SEQUENCE_MASK
     */
    private static final int DEFAULT_VIBRATION_VALUE = 1;
    
    /**
     * 最大可容忍的时间差值
     */
    private static final int MAX_TOLERATE_TIME_DIFFERENCE_MILLISECONDS = 10;
    
    private static final TimeService TIME_SERVICE = new TimeService();
    
    /**
     * 工作机器ID(0~1023)
     */
    private static volatile long WORKER_ID = 0;
    
    /**
     * 序列增量
     */
    private int sequenceOffset = -1;
    
    /**
     * 毫秒内序列(0~4095)
     */
    private long sequence = 0L;
    
    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;
    
    
    static {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2020, Calendar.DECEMBER, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        EPOCH = calendar.getTimeInMillis();
    }
    
    
    public SnowflakeId() {}
    
    private static long getWorkerId() {
        return WORKER_ID;
    }
    
    public static void setWorkId(long workId) {
        WORKER_ID = workId;
    }
    
    
    public synchronized long nextId() {
        long currentTimestamp = TIME_SERVICE.getCurrentMillis();
        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过，则抛出异常
        if (waitTolerateTimeDifferenceIfNeed(currentTimestamp)) {
            currentTimestamp = TIME_SERVICE.getCurrentMillis();
        }
        
        if (lastTimestamp == currentTimestamp) {  // 如果是同一时间生成的，则进行毫秒内序列
            // 毫秒内序列溢出
            if (0L == (sequence = (sequence + 1) & SEQUENCE_MASK)) {
                //阻塞到下一个毫秒,获得新的时间戳
                currentTimestamp = waitUntilNextTime(lastTimestamp);
            }
        } else {
            vibrateSequenceOffset();
            sequence = sequenceOffset;
        }
        lastTimestamp = currentTimestamp;
        return ((currentTimestamp - EPOCH) << TIMESTAMP_LEFT_SHIFT_BITS) | (getWorkerId() << WORKER_ID_LEFT_SHIFT_BITS) | sequence;
    }
    
    
    private boolean waitTolerateTimeDifferenceIfNeed(final long currentTimestamp) {
        if (lastTimestamp <= currentTimestamp) {
            return false;
        }
        long timeDiffMillis = lastTimestamp - currentTimestamp;
        if ( timeDiffMillis > MAX_TOLERATE_TIME_DIFFERENCE_MILLISECONDS) {
            throw new IllegalStateException(String.format("Clock moved backwards, last time is %d milliseconds, current time is %d milliseconds", lastTimestamp, currentTimestamp));
        }
        
        try {
            TimeUnit.MILLISECONDS.sleep(timeDiffMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        return true;
    }
    
    private long waitUntilNextTime(final long lastTimestamp) {
        long currentTimestamp = TIME_SERVICE.getCurrentMillis();
        while (currentTimestamp <= lastTimestamp) {
            currentTimestamp = TIME_SERVICE.getCurrentMillis();
        }
        return currentTimestamp;
    }
    
    
    /**
     * 标准的雪花算法，在生成id时，如果本次生成id的时间与上一次生成id的时间不是同一毫秒，即跨了毫秒，则序列部分会从0开始计算。
     * 如果不是高并发环境下，每次生成id都可能跨毫秒，这样每次生成的id都是偶数，
     * 如果根据id进行奇偶分片，则数据全部落到偶数表里面了，这种结果肯定不是我们期望的。
     * 我们期望的结果是，数据能均匀的分布到奇偶表中，那么跨毫秒生成的id的序列就不能一直从0开始。
     */
    private void vibrateSequenceOffset() {
        sequenceOffset = sequenceOffset >= DEFAULT_VIBRATION_VALUE ? 0 : sequenceOffset + 1;
    }
    
    /**
     * Time service.
     */
    static final class TimeService {
        
        public long getCurrentMillis() {
            return System.currentTimeMillis();
        }
    }
}
