package bds;

import util.DateUtils;

import java.time.LocalDate;
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
        String assignedAtFormatted = assignedAt != null
                ? LocalDateTime.parse(assignedAt, ISO_FORMATTER)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                : "Unknown";

        String expiresStr;
        if (expiresAt != null) {
            LocalDate expiryDate = LocalDate.parse(expiresAt, DateUtils.DATE_FORMAT);
            expiresStr = DateUtils.formatRelativeTime(expiryDate.toString());
        } else {
            expiresStr = "Never";
        }

        return String.format(
                "Assignment Metadata:\n" +
                        "  Assigned By: %s\n" +
                        "  Assigned At: %s\n" +
                        "  Expires: %s\n" +
                        "  Reason: %s",
                assignedBy,
                assignedAtFormatted,
                expiresStr,
                reason != null ? reason : "Not specified"
        );
    }
}