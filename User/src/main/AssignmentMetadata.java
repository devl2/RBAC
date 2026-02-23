import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record AssignmentMetadata(String assignedBy, String assignedAt, String expiresAt, String reason) {

    public static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static AssignmentMetadata now(String assignedBy, String reason) {
        String currentTime = LocalDateTime.now().format(ISO_FORMATTER);
        return new AssignmentMetadata(assignedBy, currentTime, null, reason);
    }

    public boolean isExpired() {
        if (expiresAt == null) {
            return false;
        }
        LocalDateTime expiryDate = LocalDateTime.parse(expiresAt, ISO_FORMATTER);
        return expiryDate.isBefore(LocalDateTime.now());
    }

    public String format() {
        String expiresStr = expiresAt != null ? expiresAt : "Never";
        return String.format(
                "Assignment Metadata:\n" +
                        "  assigned By: %s\n" +
                        "  assigned At: %s\n" +
                        "  reason: %s",
                assignedBy, assignedAt, expiresStr,
                reason != null ? reason : "Not specified"
        );
    }
}