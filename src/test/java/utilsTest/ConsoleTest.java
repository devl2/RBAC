package utilsTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.ConsoleUtils;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class ConsoleTest {

    private Scanner scanner(String input) {
        return new Scanner(new ByteArrayInputStream(input.getBytes()));
    }

    @Test
    @DisplayName("promptString: ввод строки")
    void string() {
        assertEquals("Hello", ConsoleUtils.promptString(scanner("Hello\n"), "Enter", true));
    }

    @Test
    @DisplayName("promptString: пустой ввод если optional")
    void stringOptional() {
        assertEquals("", ConsoleUtils.promptString(scanner("\n"), "Enter", false));
    }

    @Test
    @DisplayName("promptString: повтор после пустого")
    void stringRetry() {
        assertEquals("Hello", ConsoleUtils.promptString(scanner("\nHello\n"), "Enter", true));
    }

    @Test
    @DisplayName("promptInt: корректное число")
    void intValid() {
        assertEquals(5,
                ConsoleUtils.promptInt(scanner("5\n"), "Number", 1, 10));
    }

    @Test
    @DisplayName("promptInt: повтор после ошибки")
    void intRetry() {
        assertEquals(7, ConsoleUtils.promptInt(scanner("abc\n20\n7\n"), "Number", 1, 10));
    }

    @Test
    @DisplayName("promptYesNo: yes")
    void yes() {
        assertTrue(ConsoleUtils.promptYesNo(scanner("yes\n"), "Continue"));
    }

    @Test
    @DisplayName("promptYesNo: no")
    void no() {
        assertFalse(ConsoleUtils.promptYesNo(scanner("no\n"), "Continue"));
    }

    @Test
    @DisplayName("promptYesNo: повтор после ошибки")
    void yesRetry() {
        assertTrue(ConsoleUtils.promptYesNo(scanner("maybe\ny\n"), "Continue"));
    }

    @Test
    @DisplayName("promptChoice: выбор элемента")
    void choice() {
        List<String> options = List.of("A", "B", "C");

        assertEquals("B", ConsoleUtils.promptChoice(scanner("2\n"), "Choose", options));
    }

    @Test
    @DisplayName("promptChoice: пустой список")
    void emptyOptions() {
        assertThrows(IllegalArgumentException.class,
                () -> ConsoleUtils.promptChoice(scanner("1\n"), "Choose", List.of()));
    }
}