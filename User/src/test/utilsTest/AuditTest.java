package utilsTest;

import org.junit.jupiter.api.DisplayName;
import util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AuditTest {

    private AuditLog auditLog;

    @BeforeEach
    void setUp() {
        auditLog = new AuditLog();
    }

    @Test
    @DisplayName("getAll: Проверка добавления пользователя в логи")
    void log_add() {
        auditLog.log("CREATE_USER", "admin", "user1", "created user");

        List<AuditLog.AuditEntry> entries = auditLog.getAll();

        assertEquals(1, entries.size());
        assertEquals("CREATE_USER", entries.get(0).action());
        assertEquals("admin", entries.get(0).performer());
    }

    @Test
    @DisplayName("getByPerformer: Фильтрация по исполнителю")
    void checkFilter() {
        auditLog.log("CREATE_USER", "admin", "u1", "details");
        auditLog.log("DELETE_USER", "admin", "u2", "details");
        auditLog.log("CREATE_ROLE", "manager", "role1", "details");

        List<AuditLog.AuditEntry> result = auditLog.getByPerformer("admin");

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("getByPerformer: Проверка чувствительности к регистрам")
    void ignoreCase() {
        auditLog.log("ACTION", "Admin", "t", "d");

        List<AuditLog.AuditEntry> result = auditLog.getByPerformer("admin");

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getByAction: Фильтрация по действию")
    void filterActionCorrectly() {
        auditLog.log("CREATE_USER", "admin", "u1", "d");
        auditLog.log("DELETE_USER", "admin", "u2", "d");
        auditLog.log("CREATE_USER", "manager", "u3", "d");

        List<AuditLog.AuditEntry> result = auditLog.getByAction("CREATE_USER");

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("printLog: Проверка вывода логов")
    void printLog() {
        auditLog.log("CREATE_USER", "admin", "user1", "created");

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        auditLog.printLog();

        String result = output.toString();

        assertTrue(result.contains("CREATE_USER"));
        assertTrue(result.contains("admin"));
        assertTrue(result.contains("user1"));
    }

    @Test
    @DisplayName("printLog: Проверка на вывод пустого лога")
    void logPrintEmptyMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        auditLog.printLog();

        assertTrue(output.toString().contains("Audit log is empty"));
    }

    @Test
    @DisplayName("saveToFile: Проверка сохранения файла")
    void saveToFile() throws IOException {
        auditLog.log("CREATE_USER", "admin", "user1", "created");

        String filename = "test_audit_log.txt";

        auditLog.saveToFile(filename);

        File file = new File(filename);
        assertTrue(file.exists());

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String content = reader.readLine();

        assertTrue(content.contains("CREATE_USER"));

        reader.close();
        file.delete();
    }
}