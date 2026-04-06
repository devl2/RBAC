package commandsTest;

import commands.RBACSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.AuditLog;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.ScheduledExecutorService;

import static org.junit.jupiter.api.Assertions.*;

public class RBACSystemTest {

    private RBACSystem system;
    private AuditLog auditLog;

    @BeforeEach
    public void setUp() {
        auditLog = new AuditLog();
        system = new RBACSystem(auditLog);
        system.initialize();
    }

    @Test
    @DisplayName("Инициализация менеджеров")
    public void testManagersInitialized() {
        assertNotNull(system.getUserManager(), "Managers.UserManager должен быть инициализирован");
        assertNotNull(system.getRoleManager(), "Managers.RoleManager должен быть инициализирован");
        assertNotNull(system.getAssignmentManager(), "Managers.AssignmentManager должен быть инициализирован");
    }

    @Test
    @DisplayName("Создание пользователя testAdmin")
    public void testAdminUserCreated() {
        assertTrue(system.getUserManager().exists("testAdmin"), "Должен быть создан пользователь testAdmin");
        assertEquals("Admin Adminovi4", system.getUserManager().findByUserName("testAdmin").get().getFullname());
        assertEquals("admin@pochta.ru", system.getUserManager().findByUserName("testAdmin").get().getEmail());
    }

    @Test
    @DisplayName("Создание ролей")
    public void testRolesCreated() {
        assertEquals(3, system.getRoleManager().count(), "Должно быть создано 3 роли");

        assertTrue(system.getRoleManager().findByName("ADMIN").isPresent(), "Должна быть роль ADMIN");
        assertTrue(system.getRoleManager().findByName("USER").isPresent(), "Должна быть роль USER");
        assertTrue(system.getRoleManager().findByName("MANAGER").isPresent(), "Должна быть роль MANAGER");
    }

    @Test
    @DisplayName("Назначение роли ADMIN пользователю testAdmin")
    public void testAdminAssignment() {
        var adminUser = system.getUserManager().findByUserName("testAdmin").get();
        var assignments = system.getAssignmentManager().findByUser(adminUser);

        assertEquals(1, assignments.size(), "Должно быть одно назначение для testAdmin");

        var assignment = assignments.get(0);
        assertEquals("ADMIN", assignment.role().getName(), "Роль назначения должна быть ADMIN");
        assertTrue(assignment.isActive(), "Назначение должно быть активным");
    }

    @Test
    @DisplayName("Генерация статистики")
    public void testGenerateStatistics() {
        String stats = system.generateStatistics();
        assertTrue(stats.contains("Users: 2"), "Статистика должна содержать 2 пользователей");
        assertTrue(stats.contains("Roles: 3"), "Статистика должна содержать 3 роли");
        assertTrue(stats.contains("Assignments: 2"), "Статистика должна содержать 2 назначения");
    }

    @Test
    @DisplayName("Проверка установки и получения текущего пользователя")
    public void testCurrentUserGetterSetter() {
        system.setCurrentUser("testAdmin");
        assertEquals("testAdmin", system.getCurrentUser());
    }

    @Test
    @DisplayName("Проверка метода schedule tasks")
    void scheduleTasks() throws InterruptedException {
        RBACSystem system = new RBACSystem(new AuditLog());
        system.initialize();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        ScheduledExecutorService scheduler = system.scheduleTasks(1);

        Thread.sleep(1500);

        scheduler.shutdownNow();

        String result = output.toString().trim();

        assertTrue(result.contains("expired: 0"));
        assertTrue(result.contains("temp assignments: 1"));
    }
}