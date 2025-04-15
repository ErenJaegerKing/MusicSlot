package com.ruoyi.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Cron表达式工具类（quartz类）
 * 符号表示的值：
 * * 表示所有值；
 * ? 表示未说明的值，即不关心它为何值；
 * - 表示一个指定的范围；
 * , 表示附加一个可能值；
 * / 符号前表示开始时间，符号后表示每次递增的值；
 * L("last") ("last") "L" 用在day-of-month字段意思是 "这个月最后一天"；
 * W("weekday") 只能用在day-of-month字段。用来描叙最接近指定天的工作日（周一到周五）
 * @author lenovo
 */
public class CronUtil {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("ss mm HH dd MM ? yyyy");

    /**
     * 年 （可选） 留空
     * 允许的特殊字符：留空, 1970-2099 , - * /
     */
    private String year;
    /**
     * 星期 可以用数字1-7表示（1 ＝ 星期日）或用字符口串“SUN, MON, TUE, WED, THU, FRI and SAT”表示
     * 允许的特殊字符：1-7 或者 SUN-SAT , - * ? / L C #
     */
    private String week;
    /**
     * 月  可以用0-11 或用字符串  “JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV and DEC” 表示
     * 允许的特殊字符：1-12 或者 JAN-DEC , - * /
     */
    private String month;
    /**
     * 日 可以用数字1-31 中的任一一个值，但要注意一些特别的月份
     * 允许的特殊字符：1-31 , - * ? / L W C
     */
    private String day;
    /**
     * 时 可以用数字0-23表示
     * 允许的特殊字符：0-23, - * /
     */
    private String hour;
    /**
     * 分 可以用数字0－59 表示
     * 允许的特殊字符：0-59,- * /
     */
    private String minutes;
    /**
     * 秒 可以用数字0－59 表示
     * 允许的特殊字符：0-59,- * /
     */
    private String seconds ;

    /***
     *  日期转换cron表达式 例如 "0 07 10 15 1 ? 2016"
     * @param date 时间点
     * @return
     */
    public static String getCron(Date date) {
        String formatTimeStr = null;
        if (Objects.nonNull(date)) {
            formatTimeStr = sdf.format(date);
        }
        return formatTimeStr;
    }

    /**
     * 获取指定日期的cron表达式
     * @param year 年
     * @param week 星期 可以用数字1-7表示（1 ＝ 星期日）或用字符口串“SUN, MON, TUE, WED, THU, FRI and SAT”表示
     * @param month 月 可以用0-11 或用字符串  “JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV and DEC” 表示
     * @param day 日 可以用数字1-31 中的任一一个值，但要注意一些特别的月份
     * @param hour 时 可以用数字0-23表示
     * @param minutes 分 可以用数字0－59 表示
     * @param seconds 秒 可以用数字0－59 表示
     * @return
     */
    public static String getCron(String year,String week,String month,String day,String hour,String minutes,String seconds) {
        return seconds+" "+minutes+" "+hour+" "+day+" "+month+" "+week+" "+year;
    }

    /**
     * 获取指定日期的cron表达式
     * @param week 星期 可以用数字1-7表示（1 ＝ 星期日）或用字符口串“SUN, MON, TUE, WED, THU, FRI and SAT”表示
     * @param month 月 可以用0-11 或用字符串  “JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV and DEC” 表示
     * @param day 日 可以用数字1-31 中的任一一个值，但要注意一些特别的月份
     * @param hour 时 可以用数字0-23表示
     * @param minutes 分 可以用数字0－59 表示
     * @param seconds 秒 可以用数字0－59 表示
     * @return
     */
    public static String getCron(String week,String month,String day,String hour,String minutes,String seconds) {
        return getCron("*",week,month,day,hour,minutes,seconds);
    }

    /**
     * 获取指定日期的cron表达式
     * @param month 月 可以用0-11 或用字符串  “JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV and DEC” 表示
     * @param day 日 可以用数字1-31 中的任一一个值，但要注意一些特别的月份
     * @param hour 时 可以用数字0-23表示
     * @param minutes 分 可以用数字0－59 表示
     * @param seconds 秒 可以用数字0－59 表示
     * @return
     */
    static String getCron(String month,String day,String hour,String minutes,String seconds) {
        return getCron("?",month,day,hour,minutes,seconds);
    }

    /**
     * 获取指定范围的Cron表达式 例如 13-14 30-31 11-12 20-21 04-05 1-2 2021-2022
     * @param year 年 使用（year1-year2） year1<=year2
     * @param week 星期 使用（week1-week2） 可以用数字1-7表示（1 ＝ 星期日）或用字符口串“SUN, MON, TUE, WED, THU, FRI and SAT”表示
     * @param month 月 使用（month1-month2） 可以用0-11 或用字符串  “JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV and DEC” 表示
     * @param day 日  使用（day1-day2） 可以用数字1-31 中的任一一个值，但要注意一些特别的月份
     * @param hour 时 使用（hour1-hour2） 可以用数字0-23表示
     * @param minutes 分  使用（minutes1-minutes2） 可以用数字0－59 表示
     * @param seconds 秒  使用（seconds1-seconds2） 可以用数字0－59 表示
     * @return
     */
    public static String getCronByRange(String year,String week,String month,String day,String hour,String minutes,String seconds) {
        return seconds+" "+minutes+" "+hour+" "+day+" "+month+" "+week+" "+year;
    }

    /**
     * 获取指定范围的Cron表达式 例如 13-14 30-31 11-12 20-21 04-05 1-2
     * @param week 星期 使用（week1-week2） 可以用数字1-7表示（1 ＝ 星期日）或用字符口串“SUN, MON, TUE, WED, THU, FRI and SAT”表示
     * @param month 月 使用（month1-month2） 可以用0-11 或用字符串  “JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV and DEC” 表示
     * @param day 日  使用（day1-day2） 可以用数字1-31 中的任一一个值，但要注意一些特别的月份
     * @param hour 时 使用（hour1-hour2） 可以用数字0-23表示
     * @param minutes 分  使用（minutes1-minutes2） 可以用数字0－59 表示
     * @param seconds 秒  使用（seconds1-seconds2） 可以用数字0－59 表示
     * @return
     */
    public static String getCronByRange(String week,String month,String day,String hour,String minutes,String seconds) {
        return getCron("*",week,month,day,hour,minutes,seconds);
    }

    /**
     * 获取指定范围的Cron表达式  例如 13-14 30-31 11-12 20-21 04-05
     * @param month 月 使用（month1-month2） 可以用0-11 或用字符串  “JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV and DEC” 表示
     * @param day 日  使用（day1-day2） 可以用数字1-31 中的任一一个值，但要注意一些特别的月份
     * @param hour 时 使用（hour1-hour2） 可以用数字0-23表示
     * @param minutes 分  使用（minutes1-minutes2） 可以用数字0－59 表示
     * @param seconds 秒  使用（seconds1-seconds2） 可以用数字0－59 表示
     * @return
     */
    static String getCronByRange(String month,String day,String hour,String minutes,String seconds) {
        return getCron("?",month,day,hour,minutes,seconds);
    }

    /**
     * 获取自定义星期、小时、分钟的Cron表达式
     * @param daysOfWeek 星期几列表（使用Cron的数字表示，2=周一）
     * @param hour 小时（24小时制）
     * @param minute 分钟
     * @return Cron表达式字符串
     */
    public static String getCustomWeekDayHourMinuteCron(String daysOfWeek, String hour, String minute) {
        // 格式: 秒 分 时 日 月 星期（年可选）
        // 这里设置为: 0秒 指定分钟 指定小时 任意日 任意月 指定星期
        return String.format("0 %s %s * * %s", minute, hour, daysOfWeek);
    }

}
