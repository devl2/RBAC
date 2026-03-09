package utilsTest;

import bds.*;
import managers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.AuditLog;
import util.ReportGenerator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class ReportTest {
    private AuditLog auditLog;
    private User vasya;
    private Role adminRole;
    private UserManager um;
    private RoleManager rm;
    private AssignmentManager am;
    private RoleAssignment ra;

    @BeforeEach
    void setUp() {
        auditLog = new AuditLog();

        vasya = User.create("vasya223", "vasya", "vvv@asdsa.ru");
        adminRole = Role.create("ADMIN", "admin role", new HashSet<>());

        um = new UserManager(auditLog);
        rm = new RoleManager(auditLog);
        am = new AssignmentManager(um, rm, auditLog);

        um.add(vasya);
        rm.add(adminRole);

        ra = new PermanentAssignment(vasya, adminRole, AssignmentMetadata.now("system", "test"));
        am.add(ra);
    }

    @Test
    @DisplayName("generateUserReport: формирует отчёт")
    void userReport() {
        String report = new ReportGenerator().generateUserReport(um, am);
        assertTrue(report.contains("USER REPORT"));
        assertTrue(report.contains("vasya"));
        assertTrue(report.contains("ADMIN"));
    }

    @Test
    @DisplayName("generateRoleReport: считает пользователей роли")
    void roleReport() {
        String report = new ReportGenerator().generateRoleReport(rm, am);
        assertTrue(report.contains("ROLE REPORT"));
        assertTrue(report.contains("ADMIN"));
        assertTrue(report.contains("Users: 1"));
    }

    @Test
    @DisplayName("generatePermissionMatrix: выводит права пользователя")
    void permissionMatrix() {
        String report = new ReportGenerator().generatePermissionMatrix(um, am);
        assertTrue(report.contains("PERMISSION MATRIX"));
        assertTrue(report.contains("vasya"));
    }

    @Test
    @DisplayName("exportToFile: сохраняет файл")
    void exportFile() throws Exception {
        String report = "TEST REPORT";
        Path file = Files.createTempFile("report", ".txt");

        new ReportGenerator().exportToFile(report, file.toString());
        String content = Files.readString(file);

        assertEquals(report, content);
    }
}