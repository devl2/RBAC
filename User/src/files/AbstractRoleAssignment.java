import java.util.*;

public abstract class AbstractRoleAssignment implements RoleAssignment{
    private final String assignmentId;
    private final User user;
    private final Role role;
    private final AssignmentMetadata metadata;

    public AbstractRoleAssignment(User user, Role role, AssignmentMetadata metadata) {
        this.assignmentId = "assign_" + UUID.randomUUID();
        this.user = Objects.requireNonNull(user, "Пользователь не может быть пустой");
        this.role = Objects.requireNonNull(role, "Роль не может быть пустой");
        this.metadata = Objects.requireNonNull(metadata, "Мета данные не могуть быть пустыми");
    }

    @Override
    public String assignmentId() {
        return assignmentId;
    }

    @Override
    public User user() {
        return user;
    }

    @Override
    public Role role() {
        return role;
    }

    @Override
    public AssignmentMetadata metadata() {
        return metadata;
    }

    @Override
    public abstract boolean isActive();

    @Override
    public abstract String assignmentType();

    @Override
    public boolean equals(Object obj) {
        if (obj == this){
            return true;
        }

        if (obj == null || obj.getClass() != this.getClass()){
            return false;
        }

        AbstractRoleAssignment roleAssignment = (AbstractRoleAssignment) obj;
        return Objects.equals(assignmentId, roleAssignment.assignmentId);
    }

    public int hashCode(){
        return Objects.hash(assignmentId);
    }

    public String summary() {
        String status = isActive() ? "ACTIVE" : "INACTIVE";

        return String.format("[%s] %s assigned to %s by %s at %s\nReason: %s\nStatus: %s",
                assignmentType(),
                role().getName(),
                user().username(),
                metadata().assignedBy(),
                metadata().assignedAt(),
                metadata().reason() != null ? metadata().reason() : "",
                status
        );
    }
}
