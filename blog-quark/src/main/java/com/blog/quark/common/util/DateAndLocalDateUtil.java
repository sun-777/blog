package com.blog.quark.common.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class DateAndLocalDateUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * java.time.LocalDate --> java.util.Date
     * @param localDate
     * @return
     */
    public static Date localDateToDate(LocalDate localDate) {
        // 1. 使用ZonedDateTime将LocalDate转换为Instant
        // 2. 使用from（）方法从Instant对象获取Date的实例
        final Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }
    
    
    /**
     * java.time.LocalDateTime --> java.util.Date
     * @param localDateTime
     * @return
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        final Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }
    
    

    /**
     * java.util.Date --> java.time.LocalDate
     * @param date
     * @return
     */
    public static LocalDate dateToLocalDate(Date date) {
        // 1. 将java.sql.Date转换为ZonedDateTime
        // 2. 使用它的toLocalDate（）方法从ZonedDateTime获取LocalDate
        final Instant instant = Instant.ofEpochMilli(date.getTime());
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }
    
    /**
     * java.util.Date --> java.time.LocalTime
     * @param date
     * @return
     */
    public static LocalTime dateToLocalTime(Date date) {
        // 1. 将java.sql.Date转换为ZonedDateTime
        // 2. 使用它的toLocalDate（）方法从ZonedDateTime获取LocalDate
        final Instant instant = Instant.ofEpochMilli(date.getTime());
        return instant.atZone(ZoneId.systemDefault()).toLocalTime();
    }
    
    
    /**
     * java.util.Date --> java.time.LocalDateTime
     * @param date
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        final Instant instant = Instant.ofEpochMilli(date.getTime());
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    
    public static String localDateToString(LocalDate localDate) {
        return localDate.format(DATE_FORMATTER);
    }
    
    
    public static String localDateToString(LocalDate localDate, DateTimeFormatter localDateFormatter) {
        return localDate.format(localDateFormatter);
    }
    
    public static String localDateTimeToString(LocalDateTime localDateTime) {
        return localDateTime.format(DATE_TIME_FORMATTER);
    }

    public static String localDateTimeToString(LocalDateTime localDateTime, DateTimeFormatter localDateTimeFormatter) {
        return localDateTime.format(localDateTimeFormatter);
    }
    /**
     * 日期字符串转LocalDate
     * @param localDateString 格式需要为"yyyy-MM-dd"
     * @return LocalDate
     */
    public static LocalDate toLocalDate(String localDateString) {
        return LocalDate.parse(localDateString, DATE_FORMATTER);
    }
    
    public static LocalDate toLocalDate(String localDateString, DateTimeFormatter localDateFormatter) {
        return LocalDate.parse(localDateString, localDateFormatter);
    }
    
    
    /**
     * 日期字符串转LocalDateTime
     * @param localDateTimeString 格式需要为"yyyy-MM-dd HH:mm:ss"
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(String localDateTimeString) {
        return LocalDateTime.parse(localDateTimeString, DATE_TIME_FORMATTER);
    }
    
    public static LocalDateTime toLocalDateTime(String localDateTimeString, DateTimeFormatter localDateTimeFormatter) {
        return LocalDateTime.parse(localDateTimeString, localDateTimeFormatter);
    }
    
    private DateAndLocalDateUtil() {}
}
