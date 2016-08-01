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
 * Title:��ͬ��-��������ת��
 * Description:���������������֮���໥ת��
 * </pre>
 * 
 * @author jeffen@sunwin
 * @version 1.00.00 2011-11-17 created by Jeffen
 */
public class DataTypeUtil {

	public static final String STR_YM = "yyyy��MM��";
	public static final String STR_YMD = "yyyy-MM-dd";
	public static final String STR_YMD_HMS = "yyyy-MM-dd HH:mm:ss";
	public static final String STR_HM = "HH:mm";

	public static final SimpleDateFormat SDF_YMD = new SimpleDateFormat(STR_YMD);

	public static final SimpleDateFormat SDF_YMD_HMS = new SimpleDateFormat(
			STR_YMD_HMS);
	public static final SimpleDateFormat SDF_HM = new SimpleDateFormat(STR_HM);

	/**
	 * �հ��ַ�
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
	 *            С��λ��
	 * @return
	 * @see java.math.BigDecimal
	 */
	public static BigDecimal str2big(String str, int scale) {
		if (CommonCheck.isEmpty(str))
			str = "0";
		// ȡ��BigDecimal
		try {
			BigDecimal bd = new BigDecimal(str);
			// ����С��λ������һ��������С��λ�����ڶ���������ȡ�᷽��(��������)
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
	 *            С��λ��
	 * @return
	 * @see java.math.BigDecimal
	 */
	public static Double str2dou(String str) {
		if (CommonCheck.isEmpty(str))
			str = "0";
		// ȡ��BigDecimal
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
	 * ȡ��������ʱ��������ַ���
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
	 * ȡ������ID��ʱ���
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
	 * ��ȡָ����ʽ��ǰʱ����ַ���
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
	 * ��ȡĬ�ϸ�ʽ��ǰʱ����ַ���
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
	 * ��ȡĬ�ϸ�ʽ��ǰʱ����ַ���
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
	 * ��ȡĬ�ϸ�ʽ��ǰʱ����ַ���
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
	 * ��ȡĬ�ϸ�ʽ��ǰʱ����ַ���
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
	 * ��ȡĬ�ϸ�ʽָ��ʱ����ַ���
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
	 * ��ȡĬ�ϸ�ʽָ��ʱ����ַ���
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
	 * ��ȡָ����ʽָ��ʱ����ַ���
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
	 * ���ݵ�ǰʱ��ָ����ʽ�������к�
	 * 
	 * @param format
	 * @return
	 */
	public static String getSecSerialNOByDate(String format) {

		return getSecSerialNOByDate(new Date(), format);
	}

	/**
	 * ����ָ��ʱ��ָ����ʽ�������к�
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
	 * ��ȡ����1970��1��1��0��0��0��ĺ�����
	 * 
	 * @return
	 */
	public static String getMSecSerialNoByDate() {

		return String.valueOf(System.currentTimeMillis());

	}

	/**
	 * ��ȡ���ڵ�ǰһ��
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
	 * ��ȡ���ڵ�ǰ����
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
	 * ��ȡ���ڵĺ�һ��
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
	 * ��ȡ���ڵ�ǰһ��
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
	 * ��ȡ���ڵ���һ���µ�����
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
	 * ��ȡ���ڵ���һ�����ڵ�����
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
	 * ��ȡ�ϸ��µĵ�һ��
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
	 * ��ȡ�ϸ��µ����һ��
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
	 * ��ȡ��ǰ���ڵ�ǰN���ʱ��
	 * 
	 * @param date
	 * @return
	 */
	public static Date getSecBefore(int sec) {
		Date date = new Date();
		return getSecBefore(date, sec);
	}

	/**
	 * ��ȡָ�����ڵ�ǰN���ʱ��
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
	 * ��ȡʱ���
	 * 
	 * @param startDateTime
	 * @param format
	 *            0: x��yСʱz�� ; 1��(+-)x������һ����һ�졿; 2: (+)x; 9: �ո� | 1-59����ǰ |
	 *            1-23Сʱǰ | 1-11��ǰ | n��ǰ
	 * @return
	 */
	public static String getDateDiff(String startDateTime, int format) {
		String def = "0��0Сʱ0��";
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
				return "" + day + "��" + hour + "Сʱ" + min + "��";
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
							// ֻ��ʾ������
							return "�ո�";
						}
						// ֻ��ʾ������
						return "" + min + "����ǰ";
					}
					// ֻ��ʾСʱ��
					return "" + hour + "Сʱǰ";

				}
				if (day > 0 && day < 30) {
					// ֻ��ʾ����
					return "" + day + "��ǰ";
				}

				// ֻ��ʾ����
				return "" + (day / 365) + "��ǰ";

			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "date diff error.";
		}

		return def;

	}

	/**
	 * ���ڸ�ʽ���ַ������룺yyyy-MM-dd
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
	 * ���ڸ�ʽ���ַ������룺yyyy-MM-dd
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
	 * ����ʱ���ʽ���ַ������룺yyyy-MM-dd HH:mm:ss
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
	 * ���ڸ�ʽ���ַ������룺yyyy-MM-dd
	 * 
	 * @param date
	 * @return
	 */
	public static Date formatYmdTemp() {
		return formatYmdTemp(getCurrentDateStrByFormat());
	}

	/**
	 * �ַ�������׷��Ԫ��
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
	 * �ַ�������׷���ַ�������
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
	 * ���Ժ���
	 * */
	public static void main(String[] args) {
		System.out.println(DataTypeUtil.getCurrentTimeStrByFormat());
		System.out.println(SDF_YMD_HMS.format(DataTypeUtil.getSecBefore(30)));

		String yyyy = DataTypeUtil.getCurrentTimeStrByFormat("yyyy");// ��ǰ��
		String M = DataTypeUtil.getCurrentTimeStrByFormat("M");// ��ǰ��
		String d = DataTypeUtil.getCurrentTimeStrByFormat("d");// ��ǰ��
		System.out.println("��ǰ�꣺" + yyyy);
		System.out.println("��ǰ�£�" + M);
		System.out.println("��ǰ�գ�" + d);

	}
}
