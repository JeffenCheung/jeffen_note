/**
 * jeffen@bjsunwin.com
 */
package com.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 
 * <pre>
 * Title:共同类-数据类型转换
 * Description:处理各种数据类型之间相互转换
 * </pre>
 * 
 * @author jeffen@sunwin
 * @version 1.00.00 2011-11-17 created by Jeffen
 */
public class DataTypeUtil {

	public static final String STR_YM = "yyyy年MM月";
	public static final String STR_YMD = "yyyy-MM-dd";
	public static final String STR_YMD_HMS = "yyyy-MM-dd HH:mm:ss";
	public static final String STR_HM = "HH:mm";

	public static final SimpleDateFormat SDF_YMD = new SimpleDateFormat(STR_YMD);

	public static final SimpleDateFormat SDF_YMD_HMS = new SimpleDateFormat(
			STR_YMD_HMS);
	public static final SimpleDateFormat SDF_HM = new SimpleDateFormat(STR_HM);

	/**
	 * 空白字符
	 * 
	 * @param str
	 * @return
	 */
	public static String null2blank(String str) {
		if (CommonCheck.isEmpty(str) || "null".equals(str))
			return "";
		return str;
	}

	/**
	 * String to BigDecimal
	 * 
	 * @param str
	 * @param scale
	 *            小数位数
	 * @return
	 * @see java.math.BigDecimal
	 */
	public static BigDecimal str2big(String str, int scale) {
		if (CommonCheck.isEmpty(str))
			str = "0";
		// 取得BigDecimal
		try {
			BigDecimal bd = new BigDecimal(str);
			// 设置小数位数，第一个变量是小数位数，第二个变量是取舍方法(四舍五入)
			bd = bd.setScale(scale, BigDecimal.ROUND_HALF_UP);
			return bd;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * String to Double
	 * 
	 * @param str
	 * @param scale
	 *            小数位数
	 * @return
	 * @see java.math.BigDecimal
	 */
	public static Double str2dou(String str) {
		if (CommonCheck.isEmpty(str))
			str = "0";
		// 取得BigDecimal
		try {
			return Double.parseDouble(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * int to BigDecimal
	 * 
	 * @param i
	 * @return
	 * @see java.math.BigDecimal
	 */
	public static BigDecimal int2big(int i) {
		return str2big(String.valueOf(i), 0);
	}

	/**
	 * 取得年月是时分秒毫秒字符串
	 * 
	 * <p>
	 * format: yyyyMMddHHmmssFFF
	 * 
	 * @return
	 */
	public static String getDateTimeStemp() {
		String str = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssFFF");
			str = sdf.format(new Date()).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 取得数据ID用时间戳
	 * 
	 * @param prefix
	 * 
	 * @return
	 */
	public static String getDateTimeStemp(String prefix) {
		// System.out.println(prefix +
		// getDateTimeStemp().toString()+"_"+(int)(Math.random()*10));
		return prefix + getDateTimeStemp() + "_" + (int) (Math.random() * 10);
	}

	/**
	 * 获取指定格式当前时间的字符串
	 * 
	 * @param format
	 * @return
	 */
	public static String getCurrentTimeStrByFormat(String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		Date tDate = new Date();
		String dateString = formatter.format(tDate);
		return dateString;
	}

	/**
	 * 获取默认格式当前时间的字符串
	 * 
	 * <p>
	 * format:yyyy-MM-dd
	 * 
	 * @return
	 */
	public static String getCurrentYM() {
		return getCurrentTimeStrByFormat(STR_YM);
	}

	/**
	 * 获取默认格式当前时间的字符串
	 * 
	 * <p>
	 * format:yyyy-MM-dd
	 * 
	 * @return
	 */
	public static String getCurrentHM() {
		return getCurrentTimeStrByFormat(STR_HM);
	}

	/**
	 * 获取默认格式当前时间的字符串
	 * 
	 * <p>
	 * format:yyyy-MM-dd HH:mm:ss
	 * 
	 * @return
	 */
	public static String getCurrentTimeStrByFormat() {
		return getCurrentTimeStrByFormat(STR_YMD_HMS);
	}

	/**
	 * 获取默认格式当前时间的字符串
	 * 
	 * <p>
	 * format:yyyy-MM-dd
	 * 
	 * @return
	 */
	public static String getCurrentDateStrByFormat() {
		return getCurrentTimeStrByFormat(STR_YMD);
	}

	/**
	 * 获取默认格式指定时间的字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateTimeStrByFormat(Date date) {
		if (null == date) {
			return "";
		}
		return getDateTimeStrByFormat(date, STR_YMD_HMS);
	}

	/**
	 * 获取默认格式指定时间的字符串
	 * 
	 * @param date
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String getDateTimeStrByFormat(String strDate) {
		Date date = new Date(strDate);
		return getDateTimeStrByFormat(date, STR_YMD_HMS);
	}

	/**
	 * 获取指定格式指定时间的字符串
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String getDateTimeStrByFormat(Date date, String format) {
		if (null == date) {
			return "";
		}
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String dateString = formatter.format(date);
		return dateString;
	}

	/**
	 * 根据当前时间指定格式生成序列号
	 * 
	 * @param format
	 * @return
	 */
	public static String getSecSerialNOByDate(String format) {

		return getSecSerialNOByDate(new Date(), format);
	}

	/**
	 * 根据指定时间指定格式生成序列号
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String getSecSerialNOByDate(Date date, String format) {
		if (null == date) {
			return "";
		}
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String dateString = formatter.format(date);
		return dateString;
	}

	/**
	 * 获取距离1970年1月1日0点0分0秒的毫秒数
	 * 
	 * @return
	 */
	public static String getMSecSerialNoByDate() {

		return String.valueOf(System.currentTimeMillis());

	}

	/**
	 * 获取日期的前一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getTheDayBefore(Date date) {
		if (null == date) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return cal.getTime();
	}

	/**
	 * 获取日期的前几天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getTheDayBefore(Date date, int dayCount) {
		if (null == date) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, -dayCount);
		return cal.getTime();
	}

	/**
	 * 获取日期的后一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getTheDayAfter(Date date) {
		if (null == date) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}

	/**
	 * 获取日期的前一天
	 * 
	 * @param date
	 * @return
	 */
	public static String getTheDayBefore(Date date, String format) {
		if (null == date) {
			return "";
		}
		String time = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		time = simpleDateFormat.format(cal.getTime());
		return time;
	}

	/**
	 * 获取日期的上一个月的日期
	 * 
	 * @param date
	 * @return
	 */
	public static String getTheMonthBefore(Date date, String format) {
		if (null == date) {
			return "";
		}
		String time = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, -1);
		time = simpleDateFormat.format(cal.getTime());
		return time;
	}

	/**
	 * 获取日期的上一个星期的日期
	 * 
	 * @param date
	 * @return
	 */
	public static String getTheWeekBefore(Date date, String format) {
		if (null == date) {
			return "";
		}
		String time = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.WEEK_OF_YEAR, -1);
		time = simpleDateFormat.format(cal.getTime());
		return time;
	}

	/**
	 * 获取上个月的第一天
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String getLastMonthFirstDay(Date date, String format) {
		if (null == date) {
			return "";
		}
		SimpleDateFormat df = new SimpleDateFormat(format);
		GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -1);
		Date theDate = calendar.getTime();
		gcLast.setTime(theDate);
		gcLast.set(Calendar.DAY_OF_MONTH, 1);
		String day_first_prevM = df.format(gcLast.getTime());
		return day_first_prevM;
	}

	/**
	 * 获取上个月的最后一天
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static String getLastMonthLastDay(Date date, String format) {
		if (null == date) {
			return "";
		}
		SimpleDateFormat df = new SimpleDateFormat(format);
		Calendar cal = Calendar.getInstance();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -1);
		calendar.add(cal.MONTH, 1);
		calendar.set(cal.DATE, 1);
		calendar.add(cal.DATE, -1);
		String day_end_prevM = df.format(calendar.getTime());
		return day_end_prevM;
	}

	/**
	 * 获取当前日期的前N秒的时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getSecBefore(int sec) {
		Date date = new Date();
		return getSecBefore(date, sec);
	}

	/**
	 * 获取指定日期的前N秒的时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getSecBefore(Date date, int sec) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.SECOND, -sec);
		return cal.getTime();
	}

	/**
	 * 获取时间差
	 * 
	 * @param startDateTime
	 * @param format
	 *            0: x天y小时z分 ; 1：(+-)x【不足一天算一天】; 2: (+)x; 9: 刚刚 | 1-59分钟前 |
	 *            1-23小时前 | 1-11月前 | n年前
	 * @return
	 */
	public static String getDateDiff(String startDateTime, int format) {
		String def = "0天0小时0分";
		def = startDateTime;
		if (CommonCheck.isEmpty(startDateTime))
			return def;

		try {
			java.util.Date now = new Date();
			java.util.Date date;

			date = SDF_YMD_HMS.parse(startDateTime);

			long l = now.getTime() - date.getTime();
			long day = l / (24 * 60 * 60 * 1000);
			long hour = (l / (60 * 60 * 1000) - day * 24);
			long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
			@SuppressWarnings("unused")
			long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);

			if (format == 0) {
				return "" + day + "天" + hour + "小时" + min + "分";
			}

			if (format == 1) {
				return "" + (day + 1);
			}

			if (format == 2) {
				return "" + Math.abs(day);
			}

			if (format == 9) {
				if (day == 0) {
					if (hour == 0) {
						if(min==0){
							// 只显示分钟数
							return "刚刚";
						}
						// 只显示分钟数
						return "" + min + "分钟前";
					}
					// 只显示小时数
					return "" + hour + "小时前";

				}
				if (day > 0 && day < 30) {
					// 只显示天数
					return "" + day + "天前";
				}

				// 只显示年数
				return "" + (day / 365) + "年前";

			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "date diff error.";
		}

		return def;

	}

	/**
	 * 日期格式化字符串输入：yyyy-MM-dd
	 * 
	 * @param date
	 * @return
	 */
	public static String formatYmdTemp(Date date) {

		if (date == null)
			return null;
		return SDF_YMD.format(date);

	}

	/**
	 * 日期格式化字符串输入：yyyy-MM-dd
	 * 
	 * @param date
	 * @return
	 */
	public static Date formatYmdTemp(String date) {

		if (CommonCheck.isEmpty(date))
			return null;

		try {
			return SDF_YMD.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 日期时间格式化字符串输入：yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public static Date formatYmdHmsTemp(String date) {

		if (CommonCheck.isEmpty(date))
			return null;

		try {
			return SDF_YMD_HMS.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 日期格式化字符串输入：yyyy-MM-dd
	 * 
	 * @param date
	 * @return
	 */
	public static Date formatYmdTemp() {
		return formatYmdTemp(getCurrentDateStrByFormat());
	}

	/**
	 * 字符串数组追加元素
	 * 
	 * @param strLst
	 * @param strLst
	 * @return
	 */
	public static String[] reMakeStrLst(String[] strLst, String str) {
		if (strLst == null || strLst.length == 0)
			strLst = new String[] {};
		String[] temp = new String[strLst.length + 1];
		for (int i = 0; i < strLst.length; i++) {
			temp[i] = strLst[i];
		}
		temp[temp.length - 1] = str;
		return temp;
	}

	/**
	 * 字符串数组追加字符串数组
	 * 
	 * @param strLst
	 * @param strLstEx
	 * @return
	 */
	public static String[] reMakeStrLst(String[] strLst, String[] strLstEx) {
		if (strLst == null || strLst.length == 0)
			strLst = new String[] {};
		if (strLstEx == null || strLstEx.length == 0)
			strLstEx = new String[] {};

		String[] temp = new String[strLst.length + strLstEx.length];
		for (int i = 0; i < strLst.length; i++) {
			temp[i] = strLst[i];
		}

		for (int i = 0; i < strLstEx.length; i++) {
			temp[strLst.length + i] = strLstEx[i];
		}

		return temp;
	}

	/**
	 * 测试函数
	 * */
	public static void main(String[] args) {
		System.out.println(DataTypeUtil.getCurrentTimeStrByFormat());
		System.out.println(SDF_YMD_HMS.format(DataTypeUtil.getSecBefore(30)));

		String yyyy = DataTypeUtil.getCurrentTimeStrByFormat("yyyy");// 当前年
		String M = DataTypeUtil.getCurrentTimeStrByFormat("M");// 当前月
		String d = DataTypeUtil.getCurrentTimeStrByFormat("d");// 当前日
		System.out.println("当前年：" + yyyy);
		System.out.println("当前月：" + M);
		System.out.println("当前日：" + d);

	}
}
