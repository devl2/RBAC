package bds;

public class PermanentAssignment extends AbstractRoleAssignment {

    private boolean revoked;

    public PermanentAssignment(User user, Role role, AssignmentMetadata metadata) {
        super(user, role, metadata);
        this.revoked = false;
    }

    @Override
    public String assignmentType() {
        return "PERMANENT";
    }

    @Override
    public boolean isActive() {
        return !revoked;
    }

    public void revoke() {
        this.revoked = true;
    }

    public boolean isRevoked() {
        return revoked;
    }
}
