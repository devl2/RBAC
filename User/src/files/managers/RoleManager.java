package managers;

import bds.Permission;
import bds.Role;
import filters.RoleFilter;
import util.AuditLog;

import java.util.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RoleManager implements Repository<Role> {

    private final ConcurrentMap<String, Role> rolesById = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Role> rolesByName = new ConcurrentHashMap<>();
    private final AuditLog auditLog;

    private final Object lock = new Object();

    public RoleManager(AuditLog auditLog) {
        this.auditLog = auditLog;
    }

    @Override
    public void add(Role item) {
        if (item == null) {
            throw new IllegalArgumentException("Роль не может быть null");
        }

        synchronized (lock){
            if (rolesByName.containsKey(item.getName())) {
                throw new IllegalArgumentException("Роль с именем '" + item.getName() + "' уже существует");
            }

            if (rolesById.containsKey(item.getId())) {
                throw new IllegalArgumentException("Роль с id '" + item.getId() + "' уже существует");
            }

            rolesById.put(item.getId(), item);
            rolesByName.put(item.getName(), item);
        }

        auditLog.log(
                "CREATE_ROLE",
                "system",
                item.getName(),
                "bds.Role created"
        );
    }

    @Override
    public boolean remove(Role item) {
        if (item == null || item.getId() == null) return false;

        Role removed;
        synchronized (lock) {
            removed = rolesById.remove(item.getId());

            if (removed != null) {
                rolesByName.remove(removed.getName());
                auditLog.log(
                        "DELETE_ROLE",
                        "system",
                        removed.getName(),
                        "bds.Role deleted"
                );
                return true;
            }

            return false;
        }
    }

    @Override
    public Optional<Role> findById(String id) {
        if (id == null || id.isBlank()) return Optional.empty();
        return Optional.ofNullable(rolesById.get(id));
    }

    @Override
    public List<Role> findAll() {
        return new ArrayList<>(rolesById.values());
    }

    @Override
    public int count() {
        return rolesById.size();
    }

    @Override
    public void clear() {
        synchronized (lock){
            rolesById.clear();
            rolesByName.clear();
        }
    }

    public Optional<Role> findByName(String name) {
        if (name == null || name.isBlank()) return Optional.empty();
        return Optional.ofNullable(rolesByName.get(name));
    }

    public List<Role> findByFilter(RoleFilter filter) {
        if (filter == null) return findAll();

        List<Role> result = new ArrayList<>();

        for (Role role : rolesById.values()) {
            if (filter.test(role)) {
                result.add(role);
            }
        }

        return result;
    }

    public List<Role> findAll(RoleFilter filter, Comparator<Role> sorter) {
        List<Role> result = findByFilter(filter);
        if (sorter != null) {
            result.sort(sorter);
        }
        return result;
    }

    public boolean exists(String name) {
        if (name == null || name.isBlank()) return false;
        return rolesByName.containsKey(name);
    }

    public void addPermissionToRole(String roleName, Permission permission) {
        synchronized (lock) {
        Optional<Role> roleOpt = findByName(roleName);
            if (roleOpt.isEmpty()) {
                throw new IllegalArgumentException("Роль с именем '" + roleName + "' не найдена");
            }

            if (permission == null) {
                throw new IllegalArgumentException("Разрешение не может быть null");
            }

            Role role = roleOpt.get();
            role.addPermission(permission);

            rolesById.put(role.getId(), role);
            rolesByName.put(role.getName(), role);
        }
    }

    public void removePermissionFromRole(String roleName, Permission permission) {
        synchronized (lock){
            Role role = rolesByName.get(roleName);
            if (role == null) {
                throw new IllegalArgumentException("Роль с именем '" + roleName + "' не найдена");
            }

            if (permission == null) {
                throw new IllegalArgumentException("Разрешение не может быть null");
            }

            role.removePermission(permission);
            rolesById.put(role.getId(), role);
            rolesByName.put(role.getName(), role);
        }
    }

    public List<Role> findRolesWithPermission(String permissionName, String resource) {
        if (permissionName == null || permissionName.isBlank() ||
                resource == null || resource.isBlank()) {
            return Collections.emptyList();
        }

        List<Role> result = new ArrayList<>();
        for (Role role : rolesById.values()) {
            if (role.hasPermission(permissionName, resource)) {
                result.add(role);
            }
        }
        return result;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleManager that = (RoleManager) o;
        return Objects.equals(rolesById, that.rolesById);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rolesById);
    }

    @Override
    public String toString() {
        return "Managers.RoleManager{" +
                "roles=" + rolesById.values() +
                '}';
    }
}