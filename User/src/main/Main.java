import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            User user = User.create("vasya_qwerty", "Vasya Qwerty", "vasya_qwerty@mail.ru");
            Permission permission = Permission.validate("READ", "users", "askldlkasld");

            Set<Permission> permissions = new HashSet<>();
            permissions.add(new Permission("delete", "users", "Can delete users"));
            permissions.add(new Permission("read", "users", "Can read users"));

            Role adminRole = Role.validate("admin", "full system access", permissions);

            AssignmentMetadata metadata_test = AssignmentMetadata.now("system", "Initial setup");

            PermanentAssignment permanent_user = new PermanentAssignment(user, adminRole, metadata_test);

            System.out.println(user.format());
            System.out.println(permission.format());
            System.out.println(adminRole.format());
            System.out.println(metadata_test.format());
            System.out.println(permanent_user.summary());

            permanent_user.revoke();
            System.out.println("after revoke: ");
            System.out.println(permanent_user.summary());
            TemporaryAssignment temporary = new TemporaryAssignment(user, adminRole, metadata_test, "2030-01-01", false);

            System.out.println("Temporary assignment:");
            System.out.println(temporary.summary());
            System.out.println();

            System.out.println("Is active: " + temporary.isActive());

        } catch (IllegalArgumentException e) {
            System.out.println("Initialization error: " + e.getMessage());
        }

        try {
            User bad_user1 = User.create("vasya^-^", "Vasya", "vasya_qwerty@mail.ru");
            System.out.println(bad_user1.format());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        try {
            User bad_user2 = User.create("vasya_qwerty", "  ", "vasya_qwerty@mail.ru");
            System.out.println(bad_user2.format());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        try {
            User bad_user3 = User.create("vasya_qwerty", "Vasya", "@@@mail.ru");
            System.out.println(bad_user3.format());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        try {
            Permission bad_user4 = Permission.validate("re ad", "reports", "asdklajkdsja");
            System.out.println(bad_user4.format());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        try {
            Permission bad_user5 = Permission.validate("read", "reports", " ");
            System.out.println(bad_user5.format());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        try {
            Set<Permission> permissions = new HashSet<>();
            permissions.add(new Permission("delete", "users", "Can delete users"));
            permissions.add(new Permission("delete", "users", "Can delete users"));
            permissions.add(new Permission("read", "users", "Can read users"));
            Role bad_user6 = Role.validate("admin", "full system access", permissions);
            System.out.println(bad_user6.format());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        try {
            User user = User.create("vasya_qwerty", "Vasya Qwerty", "vasya_qwerty@mail.ru");

            Set<Permission> permissionss = new HashSet<>();
            permissionss.add(new Permission("delete", "users", "Can delete users"));
            permissionss.add(new Permission("read", "users", "Can read users"));

            Role adminRole = Role.validate("admin", "full system access", permissionss);
            AssignmentMetadata metadata_test = AssignmentMetadata.now("system", "Initial setup");
            TemporaryAssignment temporary = new TemporaryAssignment(user, adminRole, metadata_test, "2025-01-01", false);
            System.out.println(temporary.summary());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}