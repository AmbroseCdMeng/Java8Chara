package com.mcd.java8.dateapi;

import org.junit.Test;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.Locale;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.time.temporal.TemporalAdjusters.nextOrSame;

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
        Instant.ofEpochSecond(3);//3 秒
        Instant.ofEpochSecond(3, 0);//同上
        Instant.ofEpochSecond(2, 1_000_000_000);//2 秒之后 10 亿纳秒（10 亿 纳秒 = 1 秒）
        Instant.ofEpochSecond(4, -1_000_000_000);//4 秒之前 10 亿纳秒

        Instant i = Instant.now();//获取当前时刻的时间戳
    }

    /**
     * 定义 Duration 和 Period
     *
     * 目前看到的所有类都实现了 Temporal 接口，该接口定义了如何读取和操纵为时间建模的对象的值。
     *
     * Duration 类的静态工厂方法 between 就是为这个目的而设计的。可以创建两个 LocalTime 对象、两个 LocalDateTime、或者两个 Instant 对象之间的 duration
     */
    @Test
    public void test4(){
        LocalTime time1 = LocalTime.of(8, 00, 00);
        LocalTime time2 = LocalTime.of(11, 30, 00);
        Duration d1 = Duration.between(time1, time2);//PT3H30M      3小时30分钟

        LocalDateTime dateTime1 = LocalDateTime.of(2020,06,01,13,00,00);
        LocalDateTime dateTime2 = LocalDateTime.of(2020,06,01,17,30,00);
        Duration d2 = Duration.between(dateTime1, dateTime2);//PT4H30M      4小时30分钟

        Instant instant1 = Instant.now();
        Instant instant2 = Instant.ofEpochSecond(10_000_000, 999_999);
        Duration d3 = Duration.between(instant1, instant2);

        /**
         * 由于 LocalDateTime 和 Instant 是为了不同的目的而设计的，一是为了便于人阅读，二是为了便于机器处理，
         * 如果试图在两类对象之间创建 duration， 会触发一个 DateTimeException 异常。
         */
        /* 如果需要以年、月或者日的方式对多个时间单位建模，可以使用 Period 类。
         * 使用该类的工厂方法 between， 可以使用得到两个 LocalDate 之间的时长
         *  */
        Period p1 = Period.between(
                LocalDate.of(2020,06,01),
                LocalDate.of(2020,06,10));//P9D     9天

        Period tenDays = Period.ofDays(10);//P10D
        Period threeWeeks = Period.ofWeeks(3);//P21D
        Period twoYearsSixMonthsOneDay = Period.of(2,6,1);

        Duration threeMinutes1 = Duration.ofMinutes(3);//PT3M
        Duration threeMinutes2 = Duration.of(3, ChronoUnit.MINUTES);//PT3M
    }

    /**
     * 操纵、解析和格式化日期
     */
    @Test
    public void test5(){
        /* with 直接修改 */
        LocalDate date1 = LocalDate.of(1996, 03, 10);//1996-03-10
        LocalDate date2 = date1.withYear(2020);//2020-03-10
        LocalDate date3 = date2.withDayOfMonth(01);//2020-03-01
        LocalDate date4 = date3.with(ChronoField.MONTH_OF_YEAR, 6);//2020-06-01

        /* 相对修改 */
        LocalDate date5 = date4.plusWeeks(1);//2020-06-08
        LocalDate date6 = date5.minusYears(3);//2017-06-08
        LocalDate date7 = date6.plus(6, ChronoUnit.MONTHS);//2017-12-08
    }

    /**
     * TemporalAdjuster
     *  日期的复杂操作
     */
    @Test
    public void test6(){
        LocalDate date1 = LocalDate.of(2020, 03, 16);//2020-03-16
        LocalDate date2 = date1.with(nextOrSame(DayOfWeek.SUNDAY));//2020-03-22
        LocalDate date3 = date2.with(lastDayOfMonth());//2020-03-31
    }

    /**
     * TemporalAdjuster
     * 日期的复杂操作 - 自定义规则
     */
    @Test
    public void test7(){
        /* 方式一 ： 调用自定义 TemporalAdjuster*/
        LocalDate date = LocalDate.now();
        date = date.with(new NextWorkingDay());

        /* 方式二 ： Lambda 形式调用 */
        /* 应为 TemporalAdjuster 是函数式接口，所以可以使用 Lambda 形式传递行为 */
        TemporalAdjuster nextWorkingDay = TemporalAdjusters.ofDateAdjuster(temporal->{
           DayOfWeek dayOfWeek = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
           int dayToAdd = 1;
           if (dayOfWeek == DayOfWeek.FRIDAY)
               dayToAdd = 3;
           else if ( dayOfWeek == DayOfWeek.SATURDAY)
               dayToAdd = 2;
           return temporal.plus(dayToAdd, ChronoUnit.DAYS);
        });

        date = date.with(nextWorkingDay);
    }

    /**
     * 输出解析日期--时间对象
     */
    @Test
    public void test8(){
        LocalDate date  = LocalDate.now();//2020-06-01
        String s1 = date.format(DateTimeFormatter.BASIC_ISO_DATE);//20200601
        String s2 = date.format(DateTimeFormatter.ISO_LOCAL_DATE);//2020-06-01

        LocalDate date1 = LocalDate.parse("20200423", DateTimeFormatter.BASIC_ISO_DATE);//2020-04-23
        LocalDate date2 = LocalDate.parse("2020-06-01", DateTimeFormatter.ISO_LOCAL_DATE);//2020-06-01

        DateTimeFormatter dfm = DateTimeFormatter.ofPattern("yyyy/MM/dd");//
        LocalDate date3 = LocalDate.of(2020, 06, 01);//2020-06-01
        String s3 = date3.format(dfm);//2020/06/01
        LocalDate date4 = LocalDate.parse(s3, dfm);//2020-06-01
    }
}
