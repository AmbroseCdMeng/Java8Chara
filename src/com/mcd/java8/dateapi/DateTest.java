package com.mcd.java8.dateapi;

import org.junit.Test;

import java.time.*;
import java.time.temporal.ChronoField;
import java.util.Locale;

/**
 * Java8 新的日期和时间 API
 *
 *      Java 1.0 对日期和时间的支持只能依赖 java.util.Date 类
 *          但是，这个类无法表示日期，只能毫秒的精度表示时间。
 *              年份起始 1900
 *              月份起始 0
 *          这意味着 2014-3-18 需要 new Date(114, 2, 18) 创建。如此很是不直观。
 *
 *      Java 1.1 中 Date 类中很多方法被废弃了
 *          取而代之的是 java.util.Calendar 类
 *              月份起始 0
 *
 *      Java 8
 *          java.time.*
 *          LocalDate、LocalTime、Instant、Duration、Period
 */
public class DateTest {

    /**
     * LocalDate 对象的创建和使用
     *
     * LocalTime 的创建和使用
     */
    @Test
    public void test1(){
        //LocalDate 对象的创建和使用
        LocalDate date = LocalDate.of(2020, 5, 29);//2020-05-29
        int year = date.getYear();//2020
        Month month = date.getMonth();//May
        int day = date.getDayOfMonth();//29
        DayOfWeek dayOfWeek = date.getDayOfWeek();//FRIDAY
        int len = date.lengthOfMonth();//31
        boolean leap = date.isLeapYear();//true

        LocalDate today = LocalDate.now();

        //TemporalField 读取 LocalDate 的值
        int y = date.get(ChronoField.YEAR);
        int m = date.get(ChronoField.MONTH_OF_YEAR);
        int d = date.get(ChronoField.DAY_OF_MONTH);
        int w = date.get(ChronoField.DAY_OF_WEEK);

        LocalTime time = LocalTime.of(14,19,23);//14:19:23
        int hour = time.getHour();//14
        int minute = time.getMinute();//19
        int second = time.getSecond();//23

        //LocalDate 和 LocalTime 都可以通过解析代表它们的字符串创建
        LocalDate localDate = LocalDate.parse("2020-05-29");
        LocalTime localTime = LocalTime.parse("13:45:20");
    }

    /**
     * LocalDateTime 合并日期和时间
     */
    @Test
    public void test2(){
        LocalDateTime dt1 = LocalDateTime.of(1996, Month.MARCH, 10, 15, 15, 15);
        LocalDateTime dt2 = LocalDateTime.of(
                LocalDate.of(2020,1,1),
                LocalTime.of(16, 15));
        LocalDateTime dt3 = LocalDate.of(2003, 3, 16)
                .atTime(16,24,36);
        LocalDateTime dt4 = LocalDate.of(2008, 8, 8)
                .atTime(LocalTime.of(16, 43, 56));
        LocalDateTime dt5 = LocalTime.of(8,10,59)
                .atDate(LocalDate.of(2005, 4, 10));

        LocalDate date = dt1.toLocalDate();
        LocalTime time = dt1.toLocalTime();
    }

    /**
     * 机器的日期和时间格式
     * 从计算机角度来讲，时间最自然的格式并不是几点几分几秒，而是表示一个持续时间段上某个点的单一整型数。
     * 传统设定 UTC 时区 1970 年 1 月 1 日 午夜时分开始的秒数计算。（即时间戳）
     *
     * 这也是新的 java.time.Instant 类对时间建模的方式。
     */
    @Test
    public void test3(){
        //通过静态工厂方法 ofEpochSecond 传递一个代表秒数的值创建一个该类的实例。
        //该静态工厂方法 ofEpochSecond 还有一个增强的重载版本，它接收第二个以纳秒为单位的参数值，对传入作为秒数的参数进行调整。
        Instant.ofEpochSecond(3);
        Instant.ofEpochSecond(3, 0);
        Instant.ofEpochSecond(2, 1_000_000_000);
        Instant.ofEpochSecond(4, -1_000_000_000);
    }
}
