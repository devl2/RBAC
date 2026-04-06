package commandsTest;

import commands.CommandRegistry;
import commands.RBACSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import util.AuditLog;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterCommandTest {
    private RBACSystem system;
    private ByteArrayOutputStream outputStream;
    private CommandRegistry registry;
    private AuditLog auditLog;

    @BeforeEach
    void setUp() {
        auditLog = new AuditLog();
        system = new RBACSystem(auditLog);
        registry = new CommandRegistry();
        system.initialize();

        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    @DisplayName("Проверка регистрации команды")
    void checkCommandRegistration() {
        assertTrue(registry.commands.containsKey("user-list"),
                "Команда user-list не зарегистрирована!");
    }

    @Test
    @DisplayName("Должен показать всех пользователей без параметров")
    void shouldShowAllUsersWithoutParams() {
        registry.parseAndExecute("user-list", new Scanner(""), system);

        String output = outputStream.toString();

        assertTrue(output.contains("testAdmin"));
        assertTrue(output.contains("admin@pochta.ru"));
    }

    @Test
    @DisplayName("Должен фильтровать по username")
    void shouldFilterByUsername() {
        registry.parseAndExecute("user-list username=testAdmin", new Scanner(""), system);

        String output = outputStream.toString();
        assertTrue(output.contains("testAdmin"));
    }

    @Test
    @DisplayName("Должен фильтровать по email")
    void shouldFilterByEmail() {
        registry.parseAndExecute("user-list email=admin@pochta.ru", new Scanner(""), system);

        String output = outputStream.toString();
        assertTrue(output.contains("admin@pochta.ru"));
    }

    @Test
    @DisplayName("Должен фильтровать по нескольким параметрам")
    void shouldFilterByMultipleParams() {
        registry.parseAndExecute("user-list username=testAdmin email=admin@pochta.ru",
                new Scanner(""), system);

        String output = outputStream.toString();
        assertTrue(output.contains("testAdmin"));
    }

    @Test
    @DisplayName("Должен показать сообщение, если пользователи не найдены")
    void shouldShowNotFoundMessage() {
        registry.parseAndExecute("user-list username=NonExistentUser", new Scanner(""), system);

        String output = outputStream.toString();
        assertTrue(output.contains("Пользователи не найдены"));
    }

    @Test
    @DisplayName("Должен игнорировать регистр при фильтрации")
    void shouldIgnoreCase() {
        registry.parseAndExecute("user-list username=TESTADMIN", new Scanner(""), system);

        String output = outputStream.toString();
        assertTrue(output.contains("testAdmin"));
    }

    @Test
    void checkReportAsync() {

        registry.parseAndExecute("report-users-async", new Scanner(""), system);

        system.getExecutorService().shutdown();

        try {
            system.getExecutorService().awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            fail(e);
        }

        String output = outputStream.toString();

        assertTrue(output.contains("USER REPORT"));
        assertTrue(output.contains("testAdmin"));
        assertTrue(output.contains("ADMIN"));
    }

    @Test
    void checkSaveAsync(){
        registry.parseAndExecute("save-async", new Scanner(""), system);

        system.getExecutorService().shutdown();

        try {
            system.getExecutorService().awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            fail(e);
        }

        String output = outputStream.toString().trim();

        assertEquals("Данные сохранены", output);
    }
}