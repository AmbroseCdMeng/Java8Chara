package com.mcd.java8.dateapi;

import java.time.DayOfWeek;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;

/**
 * 自定义 TemporalAdjuster 接口
 * <p>
 * 计算下一个工作日，自动过滤周六日
 */
public class NextWorkingDay implements TemporalAdjuster {
    @Override
    public Temporal adjustInto(Temporal temporal) {
        DayOfWeek dayOfWeek = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));//获取当前日期
        int dayToAdd = 1;//默认 + 1 天
        if (dayOfWeek == DayOfWeek.FRIDAY)
            dayToAdd = 3;//如果周五 + 3 天
        else if (dayOfWeek == DayOfWeek.SATURDAY)
            dayToAdd = 2;//如果周六 + 2 天
        return temporal.plus(dayToAdd, ChronoUnit.DAYS);//返回修改后的日期
    }
}
