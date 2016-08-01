package com.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 * Title:共同校验方法
 * Description:
 * </pre>
 * 
 * @author Jeffen@sunwin
 * @version 1.00.00 2011-10-11 created by Jeffen
 */
public class CommonCheck {
	private static Pattern p = null;
	private static Matcher m = null;
	private static final String MAIL = "^[a-z,A-Z,0-9,_,\\-,\\.]{1,50}" + "@"
			+ "[a-z,A-Z,0-9,_,\\-,\\.]{1,50}$";
	private static final String PHONE = "[0-9,\\-,\\(,\\)]{0,15}";
	private static final String HMS = "(20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d";
	private static final String SIMPLE_YMD = "\\d{4}\\-(1[0-2]|0?[1-9])\\-([12]\\d|3[01]|0?[1-9])";
	private static final String SIMPLE_YMDHMS = "\\d{4}\\-(1[0-2]|0?[1-9])\\-([12]\\d|3[01]|0?[1-9])"
			+ " " + HMS;
	private static final String STANDARD_YMD = "^((((1[6-9]|[2-9]\\d)\\d{2})"
			+ "-(0?[13578]|1[02])"
			+ "-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})"
			+ "-(0?[13456789]|1[012])"
			+ "-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})"
			+ "-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))$";
	private static final String STANDARD_YMDHMS = "^((((1[6-9]|[2-9]\\d)\\d{2})"
			+ "-(0?[13578]|1[02])"
			+ "-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})"
			+ "-(0?[13456789]|1[012])"
			+ "-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})"
			+ "-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))"
			+ " " + HMS + "$";

	/**
	 * 判定是否为空串
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (str == null || "".equals(str.trim()) || "null".equals(str))
			return true;
		return false;
	}

	/**
	 * 判定是否为空串
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(Object o) {
		if (o == null)
			return true;
		return false;
	}

	/**
	 * null object to black object
	 * 
	 * @param str
	 * @return
	 */
	public static String null2black(String str) {
		if (isEmpty(str)) {
			return "";
		}

		return str;
	}

	/**
	 * 判定是否为空串
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	/**
	 * 判定是否为真
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isTrue(String str) {
		if (isEmpty(str))
			return false;
		if ("1".equals(str) || "TRUE".equals(str.toUpperCase()))
			return true;
		return false;
	}

	/**
	 * 判定是否为合法的Email格式
	 * 
	 * @param mail
	 * @return
	 */
	public static boolean isEmail(String mail) {
		p = Pattern.compile(MAIL);
		m = p.matcher(mail);
		return m.matches();
	}

	/**
	 * 判定是否为合法的Phone格式
	 * 
	 * @param phone
	 * @return
	 */
	public static boolean isPhone(String phone) {
		p = Pattern.compile(PHONE);
		m = p.matcher(phone);
		return m.matches();
	}

	/**
	 * 判定是否为合法的HH:mm:ss格式(简单校验)
	 * 
	 * @param hms
	 * @return
	 */
	public static boolean isHms(String hms) {
		p = Pattern.compile(HMS);
		m = p.matcher(hms);
		return m.matches();
	}

	/**
	 * 判定是否为合法的yyyy-MM-dd格式(简单校验)
	 * 
	 * @param ymd
	 * @return
	 */
	public static boolean isSimpleYmd(String ymd) {
		p = Pattern.compile(SIMPLE_YMD);
		m = p.matcher(ymd);
		return m.matches();
	}

	/**
	 * 判定是否为合法的yyyy-MM-dd HH:mm:ss格式(简单校验)
	 * 
	 * @param ymdhms
	 * @return
	 */
	public static boolean isSimpleYmdHms(String ymdhms) {
		p = Pattern.compile(SIMPLE_YMDHMS);
		m = p.matcher(ymdhms);
		return m.matches();
	}

	/**
	 * 判定是否为合法的yyyy-MM-dd格式(完全校验)
	 * 
	 * @param ymd
	 * @return
	 */
	public static boolean isStandardYmd(String ymd) {
		p = Pattern.compile(STANDARD_YMD);
		m = p.matcher(ymd);
		return m.matches();
	}

	/**
	 * 判定是否为合法的yyyy-MM-dd HH:mm:ss格式(完全校验)
	 * 
	 * @param ymdhms
	 * @return
	 */
	public static boolean isStandardYmdHms(String ymdhms) {
		p = Pattern.compile(STANDARD_YMDHMS);
		m = p.matcher(ymdhms);
		return m.matches();
	}

	/**
	 * 判定是否为数字型字符串
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		p = Pattern.compile("\\d*");
		m = p.matcher(str);
		return m.matches();
	}
}
