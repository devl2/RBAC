import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class AssignmentManager implements Repository<RoleAssignment> {

    private final Map<String, RoleAssignment> assignments = new HashMap<>();
    private final UserManager userManager;
    private final RoleManager roleManager;
    private AuditLog auditLog;

    public AssignmentManager(UserManager userManager, RoleManager roleManager, AuditLog auditLog) {
        this.userManager = userManager;
        this.roleManager = roleManager;
        this.auditLog = auditLog;
    }

    @Override
    public void add(RoleAssignment item) {
        if (item == null) throw new IllegalArgumentException("Назначение не может быть null");

        if (userManager.findByUserName(item.user().username()).isEmpty())
            throw new IllegalArgumentException("Пользователь не найден: " + item.user().username());
        if (roleManager.findByName(item.role().getName()).isEmpty())
            throw new IllegalArgumentException("Роль не найдена: " + item.role().getName());

        if (isRoleAssignedToUser(item.user(), item.role()))
            throw new IllegalArgumentException("Роль '" + item.role().getName() + "' уже назначена пользователю " + item.user().username());

        if (assignments.containsKey(item.assignmentId()))
            throw new IllegalArgumentException("Назначение с ID '" + item.assignmentId() + "' уже существует");

        assignments.put(item.assignmentId(), item);

        auditLog.log(
                "ASSIGNMENT_ROLE",
                "system",
                item.assignmentType(),
                "Role assignment"
        );
    }

    @Override
    public boolean remove(RoleAssignment item) {

        if (item == null || item.assignmentId() == null) {
            return false;
        }

        RoleAssignment removed = assignments.remove(item.assignmentId());

        if (removed != null) {

            auditLog.log(
                    "REVOKE_ROLE",
                    "system",
                    removed.assignmentType(),
                    "Role revoked: " + removed.assignmentType()
            );

            return true;
        }

        return false;
    }

    @Override
    public Optional<RoleAssignment> findById(String id) {
        if (id == null || id.isBlank()) return Optional.empty();
        return Optional.ofNullable(assignments.get(id));
    }

    @Override
    public List<RoleAssignment> findAll() {
        return new ArrayList<>(assignments.values());
    }

    @Override
    public int count() {
        return assignments.size();
    }

    @Override
    public void clear() {
        assignments.clear();
    }

    public List<RoleAssignment> findByUser(User user) {
        if (user == null || user.getUsername() == null) return Collections.emptyList();

        List<RoleAssignment> result = new ArrayList<>();
        for (RoleAssignment assignment : assignments.values()) {
            if (user.getUsername().equals(assignment.user().getUsername())) {
                result.add(assignment);
            }
        }
        return result;
    }

    public List<RoleAssignment> findByRole(Role role) {
        if (role == null || role.getName() == null) return Collections.emptyList();

        List<RoleAssignment> result = new ArrayList<>();
        for (RoleAssignment assignment : assignments.values()) {
            if (role.getName().equals(assignment.role().getName())) {
                result.add(assignment);
            }
        }
        return result;
    }

    public List<RoleAssignment> findByFilter(AssignmentFilter filter) {
        if (filter == null) return findAll();

        List<RoleAssignment> result = new ArrayList<>();
        for (RoleAssignment assignment : assignments.values()) {
            if (filter.test(assignment)) {
                result.add(assignment);
            }
        }
        return result;
    }

    public List<RoleAssignment> findAll(AssignmentFilter filter, Comparator<RoleAssignment> sorter) {
        List<RoleAssignment> result = findByFilter(filter);
        if (sorter != null) {
            result.sort(sorter);
        }
        return result;
    }

    public List<RoleAssignment> getActiveAssignments() {
        LocalDate today = LocalDate.now();
        List<RoleAssignment> result = new ArrayList<>();

        for (RoleAssignment assignment : assignments.values()) {
            if (assignment.isActive()) {
                result.add(assignment);
            }
        }
        return result;
    }

    public List<RoleAssignment> getExpiredAssignments() {
        LocalDate today = LocalDate.now();
        List<RoleAssignment> result = new ArrayList<>();

        for (RoleAssignment assignment : assignments.values()) {
            if (!assignment.isActive()) {
                result.add(assignment);
            }
        }
        return result;
    }

    public boolean userHasRole(User user, Role role) {
        if (user == null || role == null) return false;

        LocalDate today = LocalDate.now();
        for (RoleAssignment assignment : assignments.values()) {
            if (user.getUsername().equals(assignment.user().getUsername()) &&
                    role.getName().equals(assignment.role().getName()) &&
                    assignment.isActive()) {
                return true;
            }
        }
        return false;
    }

    public boolean userHasPermission(User user, String permissionName, String resource) {
        if (user == null || permissionName == null || resource == null) return false;

        List<RoleAssignment> userAssignments = findByUser(user);

        for (RoleAssignment assignment : userAssignments) {
            if (assignment.isActive()) {
                Role role = assignment.role();
                if (role.hasPermission(permissionName, resource)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Set<Permission> getUserPermissions(User user) {
        if (user == null) return Collections.emptySet();

        Set<Permission> permissions = new HashSet<>();
        LocalDate today = LocalDate.now();

        for (RoleAssignment assignment : assignments.values()) {
            if (user.getUsername().equals(assignment.user().getUsername()) &&
                    assignment.isActive()) {

                Role role = assignment.role();
                permissions.addAll(role.getPermissions());
            }
        }
        return permissions;
    }

    public void revokeAssignment(String assignmentId) {
        if (assignmentId == null || assignmentId.isBlank()) {
            throw new IllegalArgumentException("ID назначения не может быть пустым");
        }

        RoleAssignment removed = assignments.remove(assignmentId);
        if (removed == null) {
            throw new IllegalArgumentException("Назначение с ID '" + assignmentId + "' не найдено");
        }
    }

    public void extendTemporaryAssignment(String assignmentId, String newExpirationDate) {
        if (assignmentId == null || assignmentId.isBlank()) {
            throw new IllegalArgumentException("ID назначения не может быть пустым");
        }

        RoleAssignment assignment = assignments.get(assignmentId);
        if (assignment == null) {
            throw new IllegalArgumentException("Назначение с ID '" + assignmentId + "' не найдено");
        }

        if (!(assignment instanceof TemporaryAssignment)) {
            throw new IllegalArgumentException("Можно продлить только временное назначение");
        }

        TemporaryAssignment tempAssignment = (TemporaryAssignment) assignment;

        try {
            LocalDate.parse(newExpirationDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            throw new IllegalArgumentException("Неверный формат даты. Используйте ГГГГ-ММ-ДД");
        }

        tempAssignment.extend(newExpirationDate);

        assignments.put(assignmentId, tempAssignment);
    }


    private boolean isRoleAssignedToUser(User user, Role role) {
        LocalDate today = LocalDate.now();

        for (RoleAssignment assignment : assignments.values()) {
            if (user.getUsername().equals(assignment.user().getUsername()) &&
                    role.getName().equals(assignment.role().getName()) &&
                    assignment.isActive()) {
                return true;
            }
        }
        return false;
    }

    public List<RoleAssignment> getAssignmentsByStatus(boolean active) {
        LocalDate today = LocalDate.now();
        List<RoleAssignment> result = new ArrayList<>();

        for (RoleAssignment assignment : assignments.values()) {
            if (active && assignment.isActive()) {
                result.add(assignment);
            } else if (!active && assignment.isActive()) {
                result.add(assignment);
            }
        }
        return result;
    }

    public Map<User, List<Role>> getUserRolesMap() {
        Map<User, List<Role>> userRoles = new HashMap<>();

        for (RoleAssignment assignment : assignments.values()) {
            if (assignment.isActive()) {
                User user = assignment.user();
                Role role = assignment.role();

                userRoles.computeIfAbsent(user, k -> new ArrayList<>()).add(role);
            }
        }
        return userRoles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssignmentManager that = (AssignmentManager) o;
        return Objects.equals(assignments, that.assignments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assignments);
    }
}