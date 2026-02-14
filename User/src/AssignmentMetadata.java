import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public record AssignmentMetadata(String assignedBy, String date, String reason) {
    public static AssignmentMetadata now(String assignedBy, String reason){
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
        return new AssignmentMetadata(assignedBy, currentTime, reason);
    }

    public String format(){
        return String.format(
                "Assignment Metadata:\n" +
                        "  assigned By: %s\n" +
                        "  date: %s\n" +
                        "  reason: %s",
                assignedBy, date,
                reason != null ? reason : "Not specified"
        );
    }
}
