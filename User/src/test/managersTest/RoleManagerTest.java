package managersTest;

import managers.RoleManager;
import bds.Permission;
import bds.Role;
import filters.RoleFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import util.AuditLog;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RoleManagerTest {

    private RoleManager roleManager;
    private Role adminRole;
    private Role userRole;
    private Permission readPerm;
    private Permission writePerm;

    @BeforeEach
    void setUp() {
        AuditLog auditLog = new AuditLog();
        roleManager = new RoleManager(auditLog);

        readPerm = new Permission("READ", "document", "Can read");
        writePerm = new Permission("WRITE", "document", "Can write");

        adminRole = new Role("ADMIN", "Administrator role", new HashSet<>());
        userRole = new Role("USER", "bds.User role", new HashSet<>());
    }

    @Nested
    @DisplayName("Добавления ролей")
    class AddTests {

        @Test
        void addValidRole() {
            roleManager.add(adminRole);

            assertEquals(1, roleManager.count());
            assertTrue(roleManager.exists("ADMIN"));
        }

        @Test
        void addDuplicateName() {
            roleManager.add(adminRole);
            Role duplicate = new Role("ADMIN", "Another admin", new HashSet<>());

            assertThrows(IllegalArgumentException.class,
                    () -> roleManager.add(duplicate));
        }

        @Test
        void addNullRole() {
            assertThrows(IllegalArgumentException.class,
                    () -> roleManager.add(null));
        }
    }

    @Nested
    @DisplayName("Управления разрешениями")
    class PermissionTests {

        @BeforeEach
        void setUp() {
            roleManager.add(adminRole);
            roleManager.add(userRole);
        }

        @Test
        void addPermissionToRole() {
            roleManager.addPermissionToRole("ADMIN", readPerm);

            List<Role> rolesWithRead =
                    roleManager.findRolesWithPermission("READ", "document");

            assertEquals(1, rolesWithRead.size());
            assertEquals("ADMIN", rolesWithRead.get(0).getName());
        }

        @Test
        void addPermissionToNonExistingRole() {
            assertThrows(IllegalArgumentException.class,
                    () -> roleManager.addPermissionToRole("UNKNOWN", readPerm));
        }

        @Test
        void removePermissionFromRole() {
            roleManager.addPermissionToRole("ADMIN", readPerm);
            roleManager.addPermissionToRole("ADMIN", writePerm);

            roleManager.removePermissionFromRole("ADMIN", readPerm);

            List<Role> rolesWithRead =
                    roleManager.findRolesWithPermission("READ", "document");

            List<Role> rolesWithWrite =
                    roleManager.findRolesWithPermission("WRITE", "document");

            assertTrue(rolesWithRead.isEmpty());
            assertEquals(1, rolesWithWrite.size());
        }

        @Test
        void findRolesWithPermission() {
            roleManager.addPermissionToRole("ADMIN", readPerm);
            roleManager.addPermissionToRole("USER", readPerm);
            roleManager.addPermissionToRole("ADMIN", writePerm);

            List<Role> withRead =
                    roleManager.findRolesWithPermission("READ", "document");

            List<Role> withWrite =
                    roleManager.findRolesWithPermission("WRITE", "document");

            assertEquals(2, withRead.size());
            assertEquals(1, withWrite.size());
        }
    }

    @Nested
    @DisplayName("Удаления ролей")
    class RemoveTests {

        @BeforeEach
        void setUp() {
            roleManager.add(adminRole);
            roleManager.add(userRole);
        }

        @Test
        void removeExistingRole() {
            boolean removed = roleManager.remove(adminRole);

            assertTrue(removed);
            assertEquals(1, roleManager.count());
            assertFalse(roleManager.exists("ADMIN"));
        }

        @Test
        void removeNonExistingRole() {
            Role fake = new Role("FAKE", "Fake role", new HashSet<>());

            assertFalse(roleManager.remove(fake));
        }
    }

    @Nested
    @DisplayName("Поиск и фильтрация")
    class FindTests {

        @BeforeEach
        void setUp() {
            roleManager.add(adminRole);
            roleManager.add(userRole);
        }

        @Test
        void findByName() {
            Optional<Role> found = roleManager.findByName("ADMIN");

            Role role = found.orElseThrow();
            assertEquals("ADMIN", role.getName());
        }

        @Test
        void findById() {
            String id = adminRole.getId();

            Optional<Role> found = roleManager.findById(id);

            Role role = found.orElseThrow();
            assertEquals("ADMIN", role.getName());
        }

        @Test
        void findByFilter() {
            RoleFilter filter = role -> role.getName().contains("ER");

            List<Role> filtered = roleManager.findByFilter(filter);

            assertEquals(1, filtered.size());
            assertEquals("USER", filtered.get(0).getName());
        }

        @Test
        void findAllWithSorter() {
            Comparator<Role> byName =
                    Comparator.comparing(Role::getName);

            List<Role> sorted =
                    roleManager.findAll(null, byName);

            assertEquals(2, sorted.size());
            assertEquals("ADMIN", sorted.get(0).getName());
            assertEquals("USER", sorted.get(1).getName());
        }
    }

    @Nested
    @DisplayName("Вспомогательные методы")
    class HelperMethodsTests {

        @BeforeEach
        void setUp() {
            roleManager.add(adminRole);
            roleManager.add(userRole);
        }

        @Test
        void clearAllRoles() {
            assertEquals(2, roleManager.count());

            roleManager.clear();

            assertEquals(0, roleManager.count());
        }

        @Test
        void exists() {
            assertTrue(roleManager.exists("ADMIN"));
            assertFalse(roleManager.exists("FAKE"));
        }
    }
}