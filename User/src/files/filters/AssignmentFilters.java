package filters;

import bds.AssignmentMetadata;
import bds.Role;
import bds.User;

import java.time.LocalDateTime;

public class AssignmentFilters {

    public AssignmentFilter byUser(User user) {
        return assignment -> {
            return user.equals(assignment.user());
        };
    }

    public AssignmentFilter byUsername(String username) {
        return assignment -> {
            return username.equals(assignment.user().getUsername());
        };
    }

    public AssignmentFilter byRole(Role role) {
        return assignment -> {
            return role.equals(assignment.role());
        };

    }
    public AssignmentFilter byRoleName(String roleName) {
        return assignment -> {
            return roleName.equals(assignment.role().getName());
        };
    }

    public AssignmentFilter activeOnly = assignment -> assignment.isActive();
    public AssignmentFilter inactiveOnly = assignment -> !assignment.isActive();

    public AssignmentFilter byType(String type) {
        return assignment -> {
            return type.equals(assignment.assignmentType());
        };
    }
    public AssignmentFilter assignedBy(String username) {
        return assignment -> {
            return username.equals(assignment.user().getUsername());
        };
    }
    public AssignmentFilter assignedAfter(String date) {
        return assignment -> {
            return assignment.metadata().assignedAt().compareTo(date) > 0;
        };
    }
    public AssignmentFilter expiringBefore(String date) {
        return assignment -> {
            LocalDateTime targetDate = LocalDateTime.parse(date, AssignmentMetadata.ISO_FORMATTER);
            LocalDateTime expiresAt = LocalDateTime.parse(assignment.metadata().expiresAt());

            return expiresAt.isBefore(targetDate);
        };
    }

}
