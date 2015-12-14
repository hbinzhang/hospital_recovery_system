package com.ericsson.skt.ssh;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeConventor {

	
	public static final String FORMAT_SLASH_24HOUR = "yyyy/MM/dd HH:mm:ss";
	// public static final String CURRENT_SLASH_24HOUR = "yyyy:MM:dd HH:mm:ss";

	
	public static final String FORMAT_MINUS_24HOUR = "yyyy-MM-dd HH:mm:ss";

	public static final String FORMAT_MINUS_24HOUR_VEPG = "yyyyMMdd-HHmmss";
	
	public static final String FORMAT_MINUS_DAY = "yyyy-MM-dd";

	
	public static final String FORMAT_NONSYMBOL_24HOUR = "yyyyMMddHHmmss";

	
	public static final String FORMAT_NONSYMBOL_24HOUR_NOSEC = "yyyyMMddHHmm";

	
	public static final String FORMAT_ORACLE_SLASH_24HOUR = "yyyy/mm/dd hh24:mi:ss";

	
	public static final String FORMAT_24HOUR = "HH:mm:ss";

	public static final long ONE_DAY_MS = 24 * 60 * 60000;

	
	public static final long ONE_WEEK_MS = 7 * 24 * 60 * 60000;

	
	public static final long ONE_HOUR_MS = 60 * 60000;

	public static final int ONE_MINUTE_MS = 60000;

	public static final int ONE_HOUR_MINUTE = 60;

	public static final int ONE_DAY_HOUR = 24;

	
	public static final int BASE_YEAR = 1900;

	public static String formatTimeStr(String rawTimeStr, String inputPattern,
			String outPattern) throws ParseException {
		if (inputPattern == null) {
			throw new IllegalArgumentException("inputPattern is null.");
		}
		if (outPattern == null) {
			throw new IllegalArgumentException("outPattern is null.");
		}
		if (rawTimeStr == null) {
			throw new IllegalArgumentException("rawTimeStr is null.");
		}
		SimpleDateFormat formatIn = new SimpleDateFormat(inputPattern);
		SimpleDateFormat formatOut = new SimpleDateFormat(outPattern);

		return formatOut.format(formatIn.parse(rawTimeStr));
	}

	public static long timeGap(long startTime, long endTime) {
		return (endTime - startTime);
	}

	public static long getCurrentTime() {
		return System.currentTimeMillis();
	}

	public static long stringTime2long(String time, String pattern) {
		if (time == null) {
			throw new IllegalArgumentException("time is null.");
		}
		if (pattern == null) {
			throw new IllegalArgumentException("pattern is null.");
		}
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		try {
			Date date = format.parse(time);
			return date.getTime();
		} catch (ParseException e) {
			return 0L;
		}
	}


	public static String longtime2String(long time, String pattern) {
		if (pattern == null) {
			throw new IllegalArgumentException("pattern is null.");
		}
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(new Date(time));
	}

	
	public static String date2String(Date time, String pattern) {
		if (time == null) {
			throw new IllegalArgumentException("time is null.");
		}
		if (pattern == null) {
			throw new IllegalArgumentException("pattern is null.");
		}
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(time);
	}

	
	public static Date stringTime2Date(String time, String pattern) {
		if (pattern == null) {
			throw new IllegalArgumentException("pattern is null.");
		}
		if (time == null) {
			throw new IllegalArgumentException("time is null.");
		}
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		try {
			return format.parse(time);
		} catch (ParseException e) {
			return null;
		}
	}
	
}
