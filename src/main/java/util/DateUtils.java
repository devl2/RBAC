package util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtils {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String getCurrentDate() {
        return LocalDate.now().format(DATE_FORMAT);
    }

    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(DATETIME_FORMAT);
    }

    public static boolean isBefore(String date1, String date2) {
        return date1.compareTo(date2) < 0;
    }

    public static boolean isAfter(String date1, String date2) {
        return date1.compareTo(date2) > 0;
    }

    public static String addDays(String date, int days) {
        LocalDate d = LocalDate.parse(date, DATE_FORMAT);
        LocalDate result = d.plusDays(days);
        return result.format(DATE_FORMAT);
    }

    public static String formatRelativeTime(String date) {
        LocalDate target = LocalDate.parse(date, DATE_FORMAT);
        LocalDate today = LocalDate.now();

        long diff = ChronoUnit.DAYS.between(today, target);

        if (diff == 0) return "today";
        if (diff > 0) return "in " + diff + " day" + (diff > 1 ? "s" : "");
        else return Math.abs(diff) + " day" + (Math.abs(diff) > 1 ? "s" : "") + " ago";
    }
}