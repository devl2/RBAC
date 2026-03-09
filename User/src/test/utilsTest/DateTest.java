package utilsTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.DateUtils;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class DateTest {

    @Test
    @DisplayName("getCurrentDate: возвращает дату в формате yyyy-MM-dd")
    void currentDateFormat() {
        String date = DateUtils.getCurrentDate();
        assertTrue(date.matches("\\d{4}-\\d{2}-\\d{2}"));
    }

    @Test
    @DisplayName("getCurrentDateTime: возвращает дату и время")
    void currentDateTimeFormat() {
        String dateTime = DateUtils.getCurrentDateTime();
        assertTrue(dateTime.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
    }

    @Test
    @DisplayName("isBefore / isAfter: корректно сравнивает даты")
    void dateComparison() {
        assertTrue(DateUtils.isBefore("2024-01-01", "2024-02-01"));
        assertTrue(DateUtils.isAfter("2024-03-01", "2024-02-01"));
    }

    @Test
    @DisplayName("addDays: добавляет дни к дате")
    void addDays() {
        String result = DateUtils.addDays("2024-01-01", 5);
        assertEquals("2024-01-06", result);
    }

    @Test
    @DisplayName("formatRelativeTime: today")
    void relativeToday() {
        String today = LocalDate.now().toString();
        assertEquals("today", DateUtils.formatRelativeTime(today));
    }

    @Test
    @DisplayName("formatRelativeTime: future")
    void relativeFuture() {
        String future = LocalDate.now().plusDays(2).toString();
        assertEquals("in 2 days", DateUtils.formatRelativeTime(future));
    }

    @Test
    @DisplayName("formatRelativeTime: past")
    void relativePast() {
        String past = LocalDate.now().minusDays(1).toString();
        assertEquals("1 day ago", DateUtils.formatRelativeTime(past));
    }
}