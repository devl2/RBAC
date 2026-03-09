package managersTest;

import managers.AssignmentManager;
import managers.RoleManager;
import managers.UserManager;
import bds.*;
import org.junit.jupiter.api.*;
import util.AuditLog;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AssignmentManagerTest {

    private UserManager userManager;
    private RoleManager roleManager;
    private AssignmentManager assignmentManager;
    private AuditLog auditLog;

    private User vasya;
    private User vova;

    private Role adminRole;
    private Role userRole;
    private Role managerRole;

    @BeforeEach
    void setUp() {
        auditLog = new AuditLog();
        userManager = new UserManager(auditLog);
        roleManager = new RoleManager(auditLog);
        assignmentManager = new AssignmentManager(userManager, roleManager, auditLog);

        vasya = User.create("vasya123", "Vasya Pupkin", "vasya@email.com");
        vova = User.create("vova456", "Vova Sidorov", "vova@email.com");

        Permission read = new Permission("READ", "document", "Read");
        Permission write = new Permission("WRITE", "document", "Write");

        adminRole = new Role("ADMIN", "Admin", Set.of(read, write));
        userRole = new Role("USER", "bds.User", Set.of(read));
        managerRole = new Role("MANAGER", "Manager", Set.of());

        userManager.add(vasya);
        userManager.add(vova);

        roleManager.add(adminRole);
        roleManager.add(userRole);
        roleManager.add(managerRole);
    }

    private AssignmentMetadata metadata() {
        return new AssignmentMetadata(
                "system",
                "test",
                LocalDate.now().toString(),
                "system"
        );
    }

    @Test
    @DisplayName("permanentAssignment: Добавление постоянного назначения роли пользователю")
    void addPermanentAssignment() {
        var assignment = new PermanentAssignment(vasya, adminRole, metadata());
        assignmentManager.add(assignment);

        assertEquals(1, assignmentManager.count());
    }

    @Test
    @DisplayName("permanentAssignment: Попытка добавить дубликат назначения должна выбросить исключение")
    void duplicateAssignmentShouldThrow() {
        var a1 = new PermanentAssignment(vasya, adminRole, metadata());
        var a2 = new PermanentAssignment(vasya, adminRole, metadata());

        assignmentManager.add(a1);

        assertThrows(IllegalArgumentException.class,
                () -> assignmentManager.add(a2));
    }

    @Test
    @DisplayName("findByUser: Поиск назначений по пользователю")
    void findAssignmentsByUser() {
        assignmentManager.add(new PermanentAssignment(vasya, adminRole, metadata()));
        assignmentManager.add(new PermanentAssignment(vova, userRole, metadata()));

        List<RoleAssignment> vasyaAssignments =
                assignmentManager.findByUser(vasya);

        assertEquals(1, vasyaAssignments.size());
    }

    @Test
    @DisplayName("active & expired assignments: Разделение назначений на активные и истёкшие")
    void activeAndExpiredAssignments() {
        var active = new TemporaryAssignment(
                vasya,
                managerRole,
                metadata(),
                LocalDate.now().plusDays(10).toString(),
                false
        );

        var expired = new TemporaryAssignment(
                vova,
                userRole,
                metadata(),
                LocalDate.now().minusDays(5).toString(),
                false
        );

        assignmentManager.add(active);
        assignmentManager.add(expired);

        assertEquals(1, assignmentManager.getActiveAssignments().size());
        assertEquals(1, assignmentManager.getExpiredAssignments().size());
    }

    @Test
    @DisplayName("userHasPermission: Проверка наличия у пользователя прав по назначенным ролям")
    void userHasPermission() {
        assignmentManager.add(new PermanentAssignment(vasya, adminRole, metadata()));

        assertTrue(assignmentManager.userHasPermission(vasya, "READ", "document"));
        assertTrue(assignmentManager.userHasPermission(vasya, "WRITE", "document"));
        assertFalse(assignmentManager.userHasPermission(vova, "READ", "document"));
    }

    @Test
    @DisplayName("revokeAssignment: Отзыв назначения роли у пользователя")
    void revokeAssignment() {
        var assignment = new PermanentAssignment(vasya, adminRole, metadata());
        assignmentManager.add(assignment);

        String id = assignment.assignmentId();
        assignmentManager.revokeAssignment(id);

        assertFalse(assignmentManager.findById(id).isPresent());
    }

    @Test
    @DisplayName("extendTemporaryAssignment: Продление временного назначения роли")
    void extendTemporaryAssignment() {
        var temp = new TemporaryAssignment(
                vasya,
                managerRole,
                metadata(),
                LocalDate.now().plusDays(5).toString(),
                false
        );

        assignmentManager.add(temp);

        String id = temp.assignmentId();

        assignmentManager.extendTemporaryAssignment(
                id,
                LocalDate.now().plusDays(30).toString()
        );

        assertTrue(assignmentManager.findById(id).get().isActive());
    }

    @Test
    @DisplayName("extendTemporaryAssignment: Попытка продлить постоянное назначение должна выбросить исключение")
    void extendPermanentShouldThrow() {
        var perm = new PermanentAssignment(vasya, adminRole, metadata());
        assignmentManager.add(perm);

        assertThrows(IllegalArgumentException.class,
                () -> assignmentManager.extendTemporaryAssignment(
                        perm.assignmentId(),
                        LocalDate.now().plusDays(10).toString()
                ));
    }
}