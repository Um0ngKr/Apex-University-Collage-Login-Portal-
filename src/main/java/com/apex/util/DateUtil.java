package com.apex.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Date and Time utilities for formatting and parsing.
 */
public class DateUtil {

    private static final String DISPLAY_DATE_PATTERN = "dd-MM-yyyy";
    private static final String DISPLAY_DATETIME_PATTERN = "dd-MM-yyyy hh:mm a";
    private static final String SQL_DATE_PATTERN = "yyyy-MM-dd";

    public static String formatDisplayDate(Date date) {
        if (date == null) return "N/A";
        return new SimpleDateFormat(DISPLAY_DATE_PATTERN).format(date);
    }

    public static String formatDisplayDateTime(Timestamp timestamp) {
        if (timestamp == null) return "N/A";
        return new SimpleDateFormat(DISPLAY_DATETIME_PATTERN).format(timestamp);
    }

    public static Date parseSqlDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            java.util.Date parsed = new SimpleDateFormat(SQL_DATE_PATTERN).parse(dateStr.trim());
            return new Date(parsed.getTime());
        } catch (ParseException e) {
            return null;
        }
    }
}
