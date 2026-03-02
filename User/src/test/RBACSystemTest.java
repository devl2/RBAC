import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class RBACSystemTest {

    private RBACSystem system;

    @BeforeEach
    public void setUp() {
        system = new RBACSystem();
        system.initialize();
    }

    @Test
    public void testManagersInitialized() {
        assertNotNull(system.getUserManager(), "UserManager должен быть инициализирован");
        assertNotNull(system.getRoleManager(), "RoleManager должен быть инициализирован");
        assertNotNull(system.getAssignmentManager(), "AssignmentManager должен быть инициализирован");
    }

    @Test
    public void testAdminUserCreated() {
        assertTrue(system.getUserManager().exists("testAdmin"), "Должен быть создан пользователь testAdmin");
        assertEquals("Admin Adminovi4", system.getUserManager().findByUserName("testAdmin").get().getFullname());
        assertEquals("admin@pochta.ru", system.getUserManager().findByUserName("testAdmin").get().getEmail());
    }

    @Test
    public void testRolesCreated() {
        assertEquals(3, system.getRoleManager().count(), "Должно быть создано 3 роли");

        assertTrue(system.getRoleManager().findByName("ADMIN").isPresent(), "Должна быть роль ADMIN");
        assertTrue(system.getRoleManager().findByName("USER").isPresent(), "Должна быть роль USER");
        assertTrue(system.getRoleManager().findByName("MANAGER").isPresent(), "Должна быть роль MANAGER");
    }

    @Test
    public void testAdminAssignment() {
        var adminUser = system.getUserManager().findByUserName("testAdmin").get();
        var assignments = system.getAssignmentManager().findByUser(adminUser);

        assertEquals(1, assignments.size(), "Должно быть одно назначение для testAdmin");

        var assignment = assignments.get(0);
        assertEquals("ADMIN", assignment.role().getName(), "Роль назначения должна быть ADMIN");
        assertTrue(assignment.isActive(), "Назначение должно быть активным");
    }

    @Test
    public void testGenerateStatistics() {
        String stats = system.generateStatistics();
        assertTrue(stats.contains("Users: 1"), "Статистика должна содержать 1 пользователя");
        assertTrue(stats.contains("Roles: 3"), "Статистика должна содержать 3 роли");
        assertTrue(stats.contains("Assignments: 1"), "Статистика должна содержать 1 назначение");
    }

    @Test
    public void testCurrentUserGetterSetter() {
        system.setCurrentUser("testAdmin");
        assertEquals("testAdmin", system.getCurrentUser());
    }
}