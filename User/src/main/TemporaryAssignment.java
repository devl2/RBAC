import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TemporaryAssignment extends AbstractRoleAssignment {

    private String expiresAt;
    private boolean autoRenew;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TemporaryAssignment(User user, Role role, AssignmentMetadata metadata,
                               String expiresAt, boolean autoRenew) {
        super(user, role, metadata);
        this.expiresAt = expiresAt;
        this.autoRenew = autoRenew;
    }

    @Override
    public String assignmentType() {
        return "TEMPORARY";
    }

    @Override
    public boolean isActive() {
        return !isExpired();
    }

    public boolean isExpired() {
        LocalDate today = LocalDate.now();
        LocalDate endDate = LocalDate.parse(expiresAt, FORMATTER);
        return today.isAfter(endDate);
    }

    public void extend(String newExpirationDate) {
        this.expiresAt = newExpirationDate;
    }

    public String getTimeRemaining() {
        LocalDate today = LocalDate.now();
        LocalDate endDate = LocalDate.parse(expiresAt, FORMATTER);
        long daysRemaining = ChronoUnit.DAYS.between(today, endDate);
        return daysRemaining >= 0 ? daysRemaining + " days remaining" : "Expired";
    }

    @Override
    public String summary() {
        String status = isActive() ? "ACTIVE" : "EXPIRED";
        return String.format("[%s] %s assigned to %s by %s at %s\n" +
                        "Expires at: %s\nReason: %s\nStatus: %s",
                assignmentType(),
                role().getName(),
                user().username(),
                metadata().assignedBy(),
                metadata().assignedAt(),
                expiresAt,
                metadata().reason() != null ? metadata().reason() : "",
                status
        );
    }

    public String getExpiresAt() { return expiresAt; }
    public boolean isAutoRenew() { return autoRenew; }
}
