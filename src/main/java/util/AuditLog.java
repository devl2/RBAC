package util;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class AuditLog {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final List<AuditEntry> entries = Collections.synchronizedList(new ArrayList<>());
    private final LinkedBlockingQueue <AuditEntry> queue = new LinkedBlockingQueue<>();
    private final Thread workerThread;

    public record AuditEntry(
            String timestamp,
            String action,
            String performer,
            String target,
            String details
    ) {}

    public AuditLog(){
        workerThread = new Thread(() -> {
            while(true){
                try {
                    AuditEntry entry = queue.take();
                    entries.add(entry);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        workerThread.start();
    }

    public void flush() throws InterruptedException{
        while(!queue.isEmpty()){
            Thread.sleep(1);
        }
    }

    public void log(String action, String performer, String target, String details) {

        String timestamp = LocalDateTime.now().format(FORMATTER);

        AuditEntry entry = new AuditEntry(
                timestamp,
                action,
                performer,
                target,
                details
        );

        queue.add(entry);
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

        synchronized (entries){
            for (AuditEntry entry : entries) {
                if (entry.action().equalsIgnoreCase(action)) {
                    result.add(entry);
                }
            }
        }

        return result;
    }

    public void printLog() {
        if (entries.isEmpty()) {
            System.out.println("Audit log пуст");
            return;
        }

        synchronized (entries) {
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
    }

    public void saveToFile(String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            synchronized (entries) {
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
            }
        } catch (IOException e) {
            System.out.println("Ошибка сохранения в AuditLog: " + e.getMessage());
        }
    }
}
