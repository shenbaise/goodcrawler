/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sbs.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-6-29
 * 日期操作工具类
 */
public class DateTimeUtil {
	/**
	 * 缺省的日期显示格式： yyyy-MM-dd
	 */
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

	/**
	 * 缺省的日期时间显示格式：yyyy-MM-dd HH:mm:ss
	 */
	public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static final String DEFAULT_YEAR_FORMT = "yyyy";

	/**
	 * 私有构造方法，禁止对该类进行实例化
	 */
	private DateTimeUtil() {
	}

	/**
	 * 得到系统当前日期时间
	 * 
	 * @return 当前日期时间
	 */
	public static Date getNow() {
		return Calendar.getInstance().getTime();
	}

	/**
	 * 得到用缺省方式格式化的当前日期
	 * 
	 * @return 当前日期
	 */
	public static String getDate() {
		return getDateTime(DEFAULT_DATE_FORMAT);
	}

	public static String getDate(Date date) {
		return getDateTime(date, DEFAULT_DATE_FORMAT);
	}
	public static String getDateMin(Date date) {
		return getDateTime(date, "yyyy-MM-dd HH:mm");
	}

	public static String getDate(java.sql.Date date) {
		return getDateTime(date, DEFAULT_DATE_FORMAT);
	}

	/**
	 * 得到用缺省方式格式化的当前日期及时间
	 * 
	 * @return 当前日期及时间
	 */
	public static String getDateTime() {
		return getDateTime(DEFAULT_DATETIME_FORMAT);
	}

	/**
	 * 得到系统当前日期及时间，并用指定的方式格式化
	 * 
	 * @param pattern
	 *            显示格式
	 * @return 当前日期及时间
	 */
	public static String getDateTime(String pattern) {
		Date datetime = Calendar.getInstance().getTime();

		return getDateTime(datetime, pattern);
	}

	/**
	 * 得到系统当前日期及时间，并用指定的方式格式化
	 * 
	 * @param pattern
	 *            显示格式
	 * @return 当前日期及时间
	 */
	public static String getDateTime(String data, String pattern) {
		Date datetime = Calendar.getInstance().getTime();
		// SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

		return getDateTime(datetime, pattern);
	}
	
	public static String getDateTime(Date date) {
        return getDateTime(date, null);
    }
	
	public static String getDateTime(long time) {
	    try {
	        Date date = new Date(time);
	        return getDateTime(date, null);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
        return null;
    }
	
	/**
	 * 得到用指定方式格式化的日期
	 * 
	 * @param date
	 *            需要进行格式化的日期
	 * @param pattern
	 *            显示格式
	 * @return 日期时间字符串
	 */
	public static String getDateTime(Date date, String pattern) {
		try {
			if (null == pattern || "".equals(pattern)) {
				pattern = DEFAULT_DATETIME_FORMAT;
			}
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
			return dateFormat.format(date);
		} catch (Exception e) {
		}
		return "";
	}

	/**
	 * 得到当前年份
	 * 
	 * @return 当前年份
	 */
	public static int getCurrentYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}
	
	public static int getYearOfDate(Date date){
		 Calendar cal = Calendar.getInstance();
		 cal.setTime(date);
		 return cal.get(Calendar.YEAR);
	}
	/**
	 * 得到当前月份
	 * 
	 * @return 当前月份
	 */
	public static int getCurrentMonth() {
		// 用get得到的月份数比实际的小1，需要加上
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
	}

	/**
	 * 得到当前日
	 * 
	 * @return 当前日
	 */
	public static int getCurrentDay() {
		return Calendar.getInstance().get(Calendar.DATE);
	}

	public static java.sql.Timestamp getCurrentSqlDay() {
		return new java.sql.Timestamp(new Date().getTime());
	}

	/**
	 * 取得当前日期以后若干天的日期。如果要得到以前的日期，参数用负数。 例如要得到上星期同一天的日期，参数则为-7
	 * 
	 * @param days
	 *            增加的日期数
	 * @return 增加以后的日期
	 */
	public static Date addDays(int days) {
		return add(getNow(), days, Calendar.DATE);
	}

	/**
	 * 取得指定日期以后若干天的日期。如果要得到以前的日期，参数用负数。
	 * 
	 * @param date
	 *            基准日期
	 * @param days
	 *            增加的日期数
	 * @return 增加以后的日期
	 */
	public static Date addDays(Date date, int days) {
		return add(date, days, Calendar.DATE);
	}

	public static Date addDays(String date, int days) {
		return add(parse(date), days, Calendar.DATE);
	}

	/**
	 * 取得当前日期以后某月的日期。如果要得到以前月份的日期，参数用负数。
	 * 
	 * @param months
	 *            增加的月份数
	 * @return 增加以后的日期
	 */
	public static Date addMonths(int months) {
		return add(getNow(), months, Calendar.MONTH);
	}

	/**
	 * 取得指定日期以后某月的日期。如果要得到以前月份的日期，参数用负数。 注意，可能不是同一日子，例如2003-1-31加上一个月是2003-2-28
	 * 
	 * @param date
	 *            基准日期
	 * @param months
	 *            增加的月份数
	 * @return 增加以后的日期
	 */
	public static Date addMonths(Date date, int months) {
		return add(date, months, Calendar.MONTH);
	}
	
	public static Date addMinutes(Date date, int amount) {
        return add(date, amount, Calendar.MINUTE);
    }

	/**
	 * 内部方法。为指定日期增加相应的天数或月数
	 * 
	 * @param date
	 *            基准日期
	 * @param amount
	 *            增加的数量
	 * @param field
	 *            增加的单位，年，月或者日
	 * @return 增加以后的日期
	 */
	private static Date add(Date date, int amount, int field) {
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);
		calendar.add(field, amount);

		return calendar.getTime();
	}

	/**
	 * 计算两个日期相差天数。 用第一个日期减去第二个。如果前一个日期小于后一个日期，则返回负数
	 * 
	 * @param one
	 *            第一个日期数，作为基准
	 * @param two
	 *            第二个日期数，作为比较
	 * @return 两个日期相差天数
	 */
	public static long diffDays(Date one, Date two) {
		return (one.getTime() - two.getTime()) / (24 * 60 * 60 * 1000);
	}

	/**
	 * 计算两个日期相差月份数 如果前一个日期小于后一个日期，则返回负数
	 * 
	 * @param one
	 *            第一个日期数，作为基准
	 * @param two
	 *            第二个日期数，作为比较
	 * @return 两个日期相差月份数
	 */
	public static int diffMonths(Date one, Date two) {

		Calendar calendar = Calendar.getInstance();

		// 得到第一个日期的年分和月份数
		calendar.setTime(one);
		int yearOne = calendar.get(Calendar.YEAR);
		int monthOne = calendar.get(Calendar.MONDAY);

		// 得到第二个日期的年份和月份
		calendar.setTime(two);
		int yearTwo = calendar.get(Calendar.YEAR);
		int monthTwo = calendar.get(Calendar.MONDAY);

		return (yearOne - yearTwo) * 12 + (monthOne - monthTwo);
	}

	/**
	 * 将Object转成日期
	 * 
	 * @param date
	 * @return
	 */
	public static Date parse(Object date) {
        return parse((String)date, "");
    }
	
	/**
	 * 将字符串转成日期
	 * @param datestr
	 * @return
	 */
	public static Date parse(String datestr) {
		return parse(datestr, "");
	}
	
	public static Timestamp parseTimestamp(String datestr) {
        Date date = parse(datestr, "");
        date = date == null ? new Date() : date;
        return new Timestamp(date.getTime());
    }

	/**
	 * 将一个字符串用给定的格式转换为日期类型。<br>
	 * 注意：如果返回null，则表示解析失败
	 * 
	 * @param datestr
	 *            需要解析的日期字符串
	 * @param pattern
	 *            日期字符串的格式，默认为“yyyy-MM-dd”的形式
	 * @return 解析后的日期
	 */
	public static Date parse(String datestr, String pattern) {
		Date date = null;

		if (null == pattern || "".equals(pattern)) {
			pattern = DEFAULT_DATE_FORMAT;
		}

		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
			date = dateFormat.parse(datestr);
		} catch (ParseException e) {
		}

		return date;
	}

	public static java.sql.Timestamp parseSqlDate(String datestr, String pattern) {
		Date date = null;
		
		if (null == pattern || "".equals(pattern)) {
			pattern = DEFAULT_DATE_FORMAT;
		}

		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
			date = dateFormat.parse(datestr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (date != null) {
			return new java.sql.Timestamp(date.getTime());
		} else {
			return null;
		}
	}

	/**
	 * 返回本月的最后一天
	 * 
	 * @return 本月最后一天的日期
	 */
	public static Date getMonthLastDay() {
		return getMonthLastDay(getNow());
	}

	/**
	 * 返回给定日期中的月份中的最后一天
	 * 
	 * @param date
	 *            基准日期
	 * @return 该月最后一天的日期
	 */
	public static Date getMonthLastDay(Date date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		// 将日期设置为下一月第一天
		calendar.set(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH) + 1, 1);

		// 减去1天，得到的即本月的最后一天
		calendar.add(Calendar.DATE, -1);

		return calendar.getTime();
	}

	/**
	 * 获取上个月的最后一天
	 * 
	 * 当前月第一天，减一
	 * 
	 * @return
	 */
	public static Date getPreMonthLastDay() {
		// Date date = add(new Date(),-1,Calendar.MONTH);
		// date = getMonthLastDay(date);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());

		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				1);
		calendar.add(Calendar.DATE, -1);

		return calendar.getTime();
	}

	/**
	 * 获取上个月第一天日期
	 * 
	 * @return
	 */
	public static Date getPreMonthFirstDay() {
		// Date date = add(new Date(),-1,Calendar.MONTH);
		// date = getMonthLastDay(date);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());

		calendar.set(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH) - 1, 1);

		return calendar.getTime();
	}

	/**
	 * 返回当月第一天的日期
	 * 
	 * @return
	 */
	public static Date getCurrMonthFirstDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());

		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				1);

		return calendar.getTime();
	}

	/**
	 * 获取上周第一天的日期 第一天为星期一
	 * 
	 * @return
	 */
	public static Date getPreWeekFirstDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());

		calendar.setFirstDayOfWeek(1);
		int week = calendar.get(Calendar.DAY_OF_WEEK);
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				day - week - 5);
		return calendar.getTime();
	}

	/**
	 * 获取上周最后一天的日期 最后一天为星期天
	 * 
	 * @return
	 */
	public static Date getPreWeekLastDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());

		calendar.setFirstDayOfWeek(1);
		int week = calendar.get(Calendar.DAY_OF_WEEK);
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				day + 1 - week);
		return calendar.getTime();
	}

	
	/**
	 * 返回数字型日期如 20070830
	 * 
	 * @return
	 */
	public static int getNumberDate() {
		return getNumberDate(new Date());
	}

	public static int getNumberDate(Date date) {
		String str = getDateTime(date, "yyyyMMdd");
		return Integer.parseInt(str);
	}

	public static int getCurrentHour() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 如果传入的日期在当前日期上一天之后或者是当前日期
	 * 
	 * 根据传入的hour小时的数字，判断当前的小时的数字，是否大于传入的hour 如果大于或等于，则返回当前上一天的日期，如果小于则返回前天的日期
	 * 
	 * @param date
	 * @param hour
	 * @return
	 */
	public static Date getLastDateByHour(Date date, int hour) {
		Date current = parse(getDate());

		// 表示在昨天之前
		if (diffDays(parse(getDate(date)), current) >= -1) {
			// 返回前一天的日期
			if (getCurrentHour() >= hour) {
				return addDays(new Date(), -1);
			} else {
				return addDays(new Date(), -2);
			}
		} else {
			return date;
		}
	}

	public static Date convertDate(Timestamp time) {
		return time == null ? null : new Date(time.getTime());
	}

	/**
	 * 得到当前时区，当天的时间以long形式表示 秒的形式表示 得到的形式为 当前时区为北京东8区，每天的早8点整
	 * 
	 * @return
	 */
	public static long getDayTime() {
		Date date = new Date();
		long l = date.getTime() / (1000 * 3600 * 24);

		return l * 3600 * 24;
	}
	
	public static int[] getDateElement(Date date) {
	    Calendar calendar = Calendar.getInstance();
	    date = date == null ? new Date() : date;
	    calendar.setTime(date);
	    
	    int[] e = new int[6];
	    
	    e[0] = calendar.get(Calendar.YEAR);
	    e[1] = calendar.get(Calendar.MONTH)+1;
	    e[2] = calendar.get(Calendar.DAY_OF_MONTH);
	    
	    e[3] = calendar.get(Calendar.HOUR_OF_DAY);
	    e[4] = calendar.get(Calendar.MINUTE);
	    e[5] = calendar.get(Calendar.SECOND);
	    
	    return e;
	}
	
	public static long getYearMonth() {
	    return getYearMonth(new Date());
	}
	
	public static long getYearMonth(Date date) {	    
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
	    String str = dateFormat.format(date);	    
	    return Long.parseLong(str);
	}
	
	/**
	 * 获取对应精度的日期
	 * 
	 * 
	 * @param precision
	 * 精度 = YEAR 如日期 2012-02-15 12:30:10 则返回 2012-01-01 00:00:00
	 * 精度 = DATE 如日期 2012-02-15 12:30:10 则返回 2012-02-15 00:00:00
	 * @return
	 */
	public static Date getPrecisionDate(int precision) {
		return getPrecisionDate(new Date(), precision);
		
	}
	
	public static Date getPrecisionDate(Date date, int precision) {
	    Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        
        StringBuilder sb = new StringBuilder();
        if(Calendar.YEAR == precision) {
            sb.append(calendar.get(Calendar.YEAR)).append("-01-01 00:00:00");
        } else if(Calendar.MONTH == precision) {
            sb.append(calendar.get(Calendar.YEAR)).append("-")
              .append(calendar.get(Calendar.MONTH)+1)
              .append("-01 00:00:00");
        } else if(Calendar.DATE == precision) {
            sb.append(calendar.get(Calendar.YEAR)).append("-")
              .append(calendar.get(Calendar.MONTH)+1).append("-")
              .append(calendar.get(Calendar.DATE))
              .append(" 00:00:00");
        } else if(Calendar.HOUR == precision) {
            sb.append(calendar.get(Calendar.YEAR)).append("-")
              .append(calendar.get(Calendar.MONTH)+1).append("-")
              .append(calendar.get(Calendar.DATE)).append(" ")
              .append(calendar.get(Calendar.HOUR))
              .append(":00:00");
        } else if(Calendar.MINUTE == precision) {
            sb.append(calendar.get(Calendar.YEAR)).append("-")
              .append(calendar.get(Calendar.MONTH)+1).append("-")
              .append(calendar.get(Calendar.DATE)).append(" ")
              .append(calendar.get(Calendar.HOUR)).append(":")
              .append(calendar.get(Calendar.MINUTE))
              .append(":00");
        }
        
        return "".equals(sb.toString()) ? calendar.getTime() : parse(sb.toString(), DEFAULT_DATETIME_FORMAT);
	}
	
	// add by zzq
	/**
	 * 返回某天所在周的周一的日期
	 * @param date
	 * @return
	 */
	public static Date getFirstDayOfWeek(Date date) {
		Calendar c = new GregorianCalendar();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
		return c.getTime();
	}
	
	/**
	 * 返回某天所在周的周日的日期
	 * @param date
	 * @return
	 */
	public static Date getLastDayOfWeek(Date date) {
		Calendar c = new GregorianCalendar();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6);
		return c.getTime();
	}
	
	/**
	 * 返回某天所在周的周几的日期
	 * @param date
	 * @param day 周几,1、2、3、4、5、6、7
	 * @return
	 */
	public static Date getDayOfWeek(Date date ,int day){
		Calendar c = new GregorianCalendar();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + day - 1);
		return c.getTime();
	}
	/**
	 * 返回周几
	 * @param date
	 * @return
	 */
	public static int getDayOfWeek(Date date){
		Calendar calendar = Calendar.getInstance();  
		calendar.setTime(date);  
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)-1;  
		if(dayOfWeek <0)dayOfWeek=0;
		return dayOfWeek;
	}
}