package utilsTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.ValidationUtils;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationTest {

    private static final String FIELD_NAME = "fieldName";
    private static final String ERROR_MESSAGE = FIELD_NAME + " не может быть пустым";

    @Test
    @DisplayName("isValidUsername: корректные значения username")
    void usernameValidation_ValidValues() {
        assertTrue(ValidationUtils.isValidUsername("vasya_223"));
        assertTrue(ValidationUtils.isValidUsername("john123"));
    }

    @Test
    @DisplayName("isValidUsername: некорректные значения username")
    void usernameValidation_InvalidValues() {
        assertFalse(ValidationUtils.isValidUsername("VASYA(223)AAAAAAAAA"));
        assertFalse(ValidationUtils.isValidUsername("ab"));
    }

    @Test
    @DisplayName("isValidEmail: корректные значения email")
    void emailValidation_ValidValues() {
        assertTrue(ValidationUtils.isValidEmail("vasya@mail.ru"));
        assertTrue(ValidationUtils.isValidEmail("user@gmail.com"));
    }

    @Test
    @DisplayName("isValidEmail: некорректные значения email")
    void emailValidation_InvalidValues() {
        assertFalse(ValidationUtils.isValidEmail("vasya@@@@2323.ru"));
        assertFalse(ValidationUtils.isValidEmail("user@.com"));
    }

    @Test
    @DisplayName("requireNonEmpty: валидная строка не выбрасывает исключение")
    void requireNonEmpty_ValidString_DoesNotThrow() {
        assertDoesNotThrow(() ->
                ValidationUtils.requireNonEmpty("valid", FIELD_NAME)
        );
    }

    @Test
    @DisplayName("requireNonEmpty: пустая строка выбрасывает исключение")
    void requireNonEmpty_EmptyString_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ValidationUtils.requireNonEmpty("", FIELD_NAME)
        );

        assertEquals(ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("requireNonEmpty: строка из пробелов выбрасывает исключение")
    void requireNonEmpty_BlankString_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ValidationUtils.requireNonEmpty("   ", FIELD_NAME)
        );

        assertEquals(ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("requireNonEmpty выбрасывает исключение")
    void requireNonEmpty_NullString_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ValidationUtils.requireNonEmpty(null, FIELD_NAME)
        );

        assertEquals(ERROR_MESSAGE, exception.getMessage());
    }

}