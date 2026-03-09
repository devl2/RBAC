package util;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AuditLog {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private List<AuditEntry> entries = new ArrayList<>();

    public record AuditEntry(
            String timestamp,
            String action,
            String performer,
            String target,
            String details
    ) {}

    public void log(String action, String performer, String target, String details) {

        String timestamp = LocalDateTime.now().format(FORMATTER);

        AuditEntry entry = new AuditEntry(
                timestamp,
                action,
                performer,
                target,
                details
        );

        entries.add(entry);
    }

    public List<AuditEntry> getAll() {
        return new ArrayList<>(entries);
    }

    public List<AuditEntry> getByPerformer(String performer) {

        List<AuditEntry> result = new ArrayList<>();

        for (AuditEntry entry : entries) {
            if (entry.performer().equalsIgnoreCase(performer)) {
                result.add(entry);
            }
        }

        return result;
    }

    public List<AuditEntry> getByAction(String action) {

        List<AuditEntry> result = new ArrayList<>();

        for (AuditEntry entry : entries) {
            if (entry.action().equalsIgnoreCase(action)) {
                result.add(entry);
            }
        }

        return result;
    }

    public void printLog() {

        if (entries.isEmpty()) {
            System.out.println("Audit log is empty");
            return;
        }

        for (AuditEntry e : entries) {
            System.out.printf(
                    "[%s] ACTION=%s | PERFORMER=%s | TARGET=%s | DETAILS=%s%n",
                    e.timestamp(),
                    e.action(),
                    e.performer(),
                    e.target(),
                    e.details()
            );
        }
    }

    public void saveToFile(String filename) {

        try (FileWriter writer = new FileWriter(filename)) {

            for (AuditEntry e : entries) {

                writer.write(String.format(
                        "[%s] ACTION=%s | PERFORMER=%s | TARGET=%s | DETAILS=%s%n",
                        e.timestamp(),
                        e.action(),
                        e.performer(),
                        e.target(),
                        e.details()
                ));
            }

        } catch (IOException e) {
            System.out.println("Error saving audit log: " + e.getMessage());
        }
    }
}
