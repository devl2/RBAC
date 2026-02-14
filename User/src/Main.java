import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            User test1 = User.validate("vasya_qwerty", "Vasya Qwerty", "vasya_qwerty@mail.ru");
            Permission test2 = Permission.validate("READ", "users", "askldlkasld");
            System.out.println(test1.format());
            System.out.println(test2.format());
        } catch (IllegalArgumentException e) {
            System.out.println("Initialization error: " + e.getMessage());
        }

        try {
            User bad_user1 = User.validate("vasya^-^", "Vasya", "vasya_qwerty@mail.ru");
            System.out.println(bad_user1.format());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        try {
            User bad_user2 = User.validate("vasya_qwerty", "  ", "vasya_qwerty@mail.ru");
            System.out.println(bad_user2.format());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        try {
            User bad_user3 = User.validate("vasya_qwerty", "Vasya", "@@@mail.ru");
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
    }
}