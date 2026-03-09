package utilsTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.FormatUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FormatTest {

    @Test
    @DisplayName("formatTable: создаёт таблицу")
    void table() {
        String[] headers = {"Name", "Age"};
        List<String[]> rows = List.of(
                new String[]{"vasya", "25"},
                new String[]{"bob", "30"}
        );

        String result = FormatUtils.formatTable(headers, rows);

        assertTrue(result.contains("Name"));
        assertTrue(result.contains("vasya"));
        assertTrue(result.contains("+"));
        assertTrue(result.contains("|"));
    }

    @Test
    @DisplayName("formatBox: текст в рамке")
    void box() {
        String result = FormatUtils.formatBox("Hello");

        assertTrue(result.contains("Hello"));
        assertTrue(result.startsWith("+"));
        assertTrue(result.endsWith("+"));
    }

    @Test
    @DisplayName("formatHeader: делает верхний регистр")
    void header() {
        String result = FormatUtils.formatHeader("tasks");

        assertEquals("\nTASKS\n", result);
    }

    @Test
    @DisplayName("truncate: не обрезает короткую строку")
    void truncateShort() {
        assertEquals("Hello", FormatUtils.truncate("Hello", 10));
    }

    @Test
    @DisplayName("truncate: обрезает длинную строку")
    void truncateLong() {
        assertEquals("Hel...", FormatUtils.truncate("HelloWorld", 6));
    }

    @Test
    @DisplayName("truncate: маленький лимит")
    void truncateSmallLimit() {
        assertEquals("...", FormatUtils.truncate("Hello", 2));
    }

    @Test
    @DisplayName("padRight: добавляет пробелы справа")
    void padRight() {
        assertEquals("Hi   ", FormatUtils.padRight("Hi", 5));
    }

    @Test
    @DisplayName("padLeft: добавляет пробелы слева")
    void padLeft() {
        assertEquals("   Hi", FormatUtils.padLeft("Hi", 5));
    }
}