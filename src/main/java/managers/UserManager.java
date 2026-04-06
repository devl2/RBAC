package managers;

import bds.User;
import filters.UserFilter;
import util.AuditLog;

import java.util.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UserManager implements Repository<User> {

    private final ConcurrentMap<String, User> users = new ConcurrentHashMap<>();
    private final AuditLog auditLog;

    private final Object lock = new Object();

    public UserManager(AuditLog auditLog) {
        this.auditLog = auditLog;
    }

    @Override
    public void add(User item) {
        if (item == null){
            throw new IllegalArgumentException("Пользователь не может быть null");
        }
        synchronized (lock){
            if (users.containsKey(item.getUsername())) {
                throw new IllegalArgumentException("Пользователь с username " + item.getUsername() + "уже существует");
            }

            if (item.getEmail() != null && !item.getEmail().isBlank()) {
                if (findByEmail(item.getEmail()).isPresent()) {
                    throw new IllegalArgumentException("Пользователь с email '" +
                            item.getEmail() + "' уже существует");
                }
            }

            users.put(item.getUsername(), item);
        }

        auditLog.log(
                "CREATE_USER",
                "system",
                item.getUsername(),
                "User created"
        );
    }

    @Override
    public boolean remove(User item) {

        if (item == null || item.getUsername() == null) {
            return false;
        }

        User removed;

        synchronized (lock){
            removed = users.remove(item.getUsername());
        }

        if (removed != null) {
            auditLog.log(
                    "DELETE_USER",
                    "system",
                    item.getUsername(),
                    "User deleted"
            );
            return true;
        }

        return false;
    }

    @Override
    public Optional<User> findById(String id) {
        if(id == null || id.isBlank()) return Optional.empty();

        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public int count() {
        return users.size();
    }

    @Override
    public void clear() {
        synchronized (lock){
            users.clear();
        }
    }

    public Optional<User> findByUserName(String username){
        if(username == null || username.isBlank()) return Optional.empty();

        return Optional.ofNullable(users.get(username));
    }

    public Optional<User> findByEmail(String email){
        if(email == null || email.isBlank()) return Optional.empty();

        for (User user : users.values()) {
            if (email.equals(user.getEmail())) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    public List<User> findByFilter(UserFilter filter) {
        if (filter == null) return findAll();

        return users.values()
                .stream()
                .filter(filter::test)
                .toList();
    }

    public List<User> findByFilterParallel(UserFilter filter) {
        if (filter == null) return findAll();

        return users.values()
                .parallelStream()
                .filter(filter::test)
                .toList();
    }

    public List<User> findAll(UserFilter filter, Comparator<User> sorter) {
        List<User> result = findByFilterParallel(filter);

        if (sorter != null) {
            result.sort(sorter);
        }

        return result;
    }

    public boolean exists(String username) {
        if (username == null || username.isBlank()) return false;
        return users.containsKey(username);
    }

    public void update(String username, String newFullName, String newEmail) {
        synchronized (lock){
            User existing = users.get(username);

            if (existing == null) {
                throw new IllegalArgumentException("Пользователь не найден: " + username);
            }

            String fullName = newFullName != null && !newFullName.isBlank() ? newFullName : existing.getFullname();
            String email = newEmail != null && !newEmail.isBlank() ? newEmail : existing.getEmail();

            if (!existing.getEmail().equals(email) && findByEmail(email).isPresent()) {
                throw new IllegalArgumentException("Email уже используется: " + email);
            }

            users.put(username, User.create(username, fullName, email));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserManager that = (UserManager) o;

        return Objects.equals(users, that.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(users);
    }

}
