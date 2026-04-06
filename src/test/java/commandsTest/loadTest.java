package commandsTest;

import bds.*;
import commands.RBACSystem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.AuditLog;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.concurrent.TimeUnit;

public class loadTest {
    private final RBACSystem system = new RBACSystem(new AuditLog());
    private final int THREADS = 8;

    @Test
    @DisplayName("Нагрузочный тест системы")
    public void stressTestRBAC() throws InterruptedException {
        system.initialize();

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);

        for (int i = 0; i < THREADS; i++) {
            int threadId = i;
            executor.submit(() -> {
                try {
                    for (int u = 0; u < 10  ; u++) {
                        system.getUserManager().add(
                                User.create("user" + threadId + "_" + u,
                                        "User " + threadId + "_" + u,
                                        "user" + threadId + "_" + u + "@mail.com")
                        );
                    }

                    Permission read = new Permission("READ", "document", "Read");

                    var role = new Role("ROLE_" + threadId, "Role " + threadId, Set.of(read));
                    system.getRoleManager().add(role);

                    for (var user : system.getUserManager().findAll()) {
                        var assignment = new PermanentAssignment(user, role, AssignmentMetadata.now("test", "thread"));
                        system.getAssignmentManager().add(assignment);
                    }

                    system.getUserManager().findByUserName("user" + threadId + "_5");
                    system.getAssignmentManager().findByRole(role);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        System.out.println("Всего пользователей: " + system.getUserManager().count());
        System.out.println("Всего ролей: " + system.getRoleManager().count());
        System.out.println("Всего назначений: " + system.getAssignmentManager().count());

        Set<String> usernames = new HashSet<>();

        for (User u: system.getUserManager().findAll()){
            if(!usernames.add(u.getUsername())){
                System.out.println("duplicate name " + u.getUsername());
            }
        }

        for (int t = 0; t < THREADS; t++) {
            for (int u = 0; u < THREADS; u++) {
                String name = "user" + t + "_" + u;

                if (system.getUserManager().findByUserName(name).isEmpty()) {
                    System.out.println("Missing user: " + name);
                }
            }
        }
    }

}
