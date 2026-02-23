import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserManagerTest {

    private UserManager userManager;
    private User vasya;

    @BeforeEach
    void setUp() {
        userManager = new UserManager();
        vasya = User.create("vasya123", "Vasya Pupkin", "vasya@email.com");
    }

    @Nested
    @DisplayName("Тесты добавления пользователей")
    class AddTests {

        @Test
        @DisplayName("Добавление пользователя должно проходить успешно")
        void addValidUser() {
            userManager.add(vasya);

            assertEquals(1, userManager.count());
            assertTrue(userManager.exists("vasya123"));
        }

        @Test
        @DisplayName("Добавление null должно выбрасывать исключение")
        void addNullUser() {
            assertThrows(IllegalArgumentException.class, () -> userManager.add(null));
        }

        @Test
        @DisplayName("Добавление пользователя с существующим username должно выбрасывать исключение")
        void addDuplicateUsername() {
            userManager.add(vasya);
            User duplicateUser = User.create("vasya123", "Vasya Clone", "clone@email.com");

            assertThrows(IllegalArgumentException.class, () -> userManager.add(duplicateUser));
        }

        @Test
        @DisplayName("Добавление пользователя с существующим email должно выбрасывать исключение")
        void addDuplicateEmail() {
            userManager.add(vasya);
            User duplicateEmail = User.create("vova456", "Vova Ivanov", "vasya@email.com");

            assertThrows(IllegalArgumentException.class, () -> userManager.add(duplicateEmail));
        }
    }

    @Nested
    @DisplayName("Тесты поиска пользователей")
    class FindTests {

        private User vova;

        @BeforeEach
        void setUp() {
            userManager.add(vasya);
            vova = User.create("vova456", "Vova Ivanov", "vova@email.com");
            userManager.add(vova);
        }

        @Test
        @DisplayName("Поиск по существующему username должен возвращать пользователя")
        void findByExistingUsername() {
            Optional<User> found = userManager.findByUserName("vasya123");

            assertTrue(found.isPresent());
            assertEquals("Vasya Pupkin", found.get().getFullname());
        }

        @Test
        @DisplayName("Поиск по несуществующему username должен возвращать пустой Optional")
        void findByNonExistingUsername() {
            Optional<User> found = userManager.findByUserName("unknown");
            assertFalse(found.isPresent());
        }

        @Test
        @DisplayName("Поиск по существующему email должен возвращать пользователя")
        void findByExistingEmail() {
            Optional<User> found = userManager.findByEmail("vova@email.com");

            assertTrue(found.isPresent());
            assertEquals("vova456", found.get().getUsername());
        }

        @Test
        @DisplayName("Поиск по несуществующему email должен возвращать пустой Optional")
        void findByNonExistingEmail() {
            Optional<User> found = userManager.findByEmail("nonexistent@email.com");
            assertFalse(found.isPresent());
        }
    }

    @Nested
    @DisplayName("Тесты обновления пользователей")
    class UpdateTests {

        @BeforeEach
        void setUp() {
            userManager.add(vasya);
        }

        @Test
        @DisplayName("Обновление существующего пользователя должно проходить успешно")
        void updateExistingUser() {
            userManager.update("vasya123", "Vasya Updated", "vasyaupdated@email.com");

            Optional<User> updated = userManager.findByUserName("vasya123");
            assertTrue(updated.isPresent());
            assertEquals("Vasya Updated", updated.get().getFullname());
            assertEquals("vasyaupdated@email.com", updated.get().getEmail());
        }

        @Test
        @DisplayName("Обновление только имени должно сохранять старый email")
        void updateOnlyName() {
            userManager.update("vasya123", "Vasya Only", null);

            Optional<User> updated = userManager.findByUserName("vasya123");
            assertEquals("Vasya Only", updated.get().getFullname());
            assertEquals("vasya@email.com", updated.get().getEmail());
        }

        @Test
        @DisplayName("Обновление несуществующего пользователя должно выбрасывать исключение")
        void updateNonExistingUser() {
            assertThrows(IllegalArgumentException.class,
                    () -> userManager.update("unknown", "Name", "email@test.com"));
        }
    }

    @Nested
    @DisplayName("Тесты удаления пользователей")
    class RemoveTests {

        @BeforeEach
        void setUp() {
            userManager.add(vasya);
        }

        @Test
        @DisplayName("Удаление существующего пользователя должно возвращать true")
        void removeExistingUser() {
            boolean removed = userManager.remove(vasya);

            assertTrue(removed);
            assertEquals(0, userManager.count());
        }

        @Test
        @DisplayName("Удаление несуществующего пользователя должно возвращать false")
        void removeNonExistingUser() {
            User vova = User.create("vova456", "Vova Ivanov", "vova@email.com");
            assertFalse(userManager.remove(vova));
        }
    }

    @Nested
    @DisplayName("Тесты фильтрации и сортировки")
    class FilterTests {

        private User vova;

        @BeforeEach
        void setUp() {
            userManager.add(vasya);
            vova = User.create("vova456", "Vova Ivanov", "vova@email.com");
            userManager.add(vova);
            userManager.add(User.create("bob789", "Bob Johnson", "bob@email.com"));
        }

        @Test
        @DisplayName("Фильтрация по имени должна возвращать только подходящих пользователей")
        void filterByName() {
            UserFilter filter = user -> user.getFullname().contains("Ivanov");

            List<User> filtered = userManager.findByFilter(filter);

            assertEquals(1, filtered.size());
            assertEquals("vova456", filtered.get(0).getUsername());
        }

        @Test
        @DisplayName("Фильтр null должен возвращать всех пользователей")
        void nullFilter() {
            List<User> all = userManager.findByFilter(null);
            assertEquals(3, all.size());
        }

        @Test
        @DisplayName("Сортировка должна работать")
        void sorting() {
            Comparator<User> byName = Comparator.comparing(User::getUsername);
            List<User> sorted = userManager.findAll(null, byName);

            assertEquals("bob789", sorted.get(0).getUsername());
            assertEquals("vasya123", sorted.get(1).getUsername());
            assertEquals("vova456", sorted.get(2).getUsername());
        }
    }
}