package commands;

import bds.*;
import managers.*;
import util.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RBACSystem {
    private UserManager userManager;
    private RoleManager roleManager;
    private AssignmentManager assignmentManager;
    private String currentUser;
    private AuditLog auditLog;
    private ExecutorService executorService;

    private int poolSize;

    public UserManager getUserManager(){
        return userManager;
    }

    public RBACSystem(AuditLog auditLog) {
        this.auditLog = auditLog;
        this.poolSize = 4;
        this.executorService = Executors.newFixedThreadPool(poolSize);
    }

    public RoleManager getRoleManager(){
        return roleManager;
    }

    public AssignmentManager getAssignmentManager(){
        return assignmentManager;
    }

    public AuditLog getAuditLog(){return auditLog;}

    public ExecutorService getExecutorService() {return executorService;}

    public int getPoolSize(){return poolSize;}

    public void setCurrentUser(String username) {
        this.currentUser = username;
    }

    public String getCurrentUser(){
        return currentUser;
    }

    public void initialize(){
        userManager = new UserManager(auditLog);
        roleManager = new RoleManager(auditLog);
        assignmentManager = new AssignmentManager(userManager, roleManager, auditLog);

        User testAdmin = User.create("testAdmin", "Admin Adminovi4", "admin@pochta.ru");
        User tempUser = User.create("tempUser", "User User", "user@pochta.ru");
        userManager.add(testAdmin);
        userManager.add(tempUser);

        Permission read = new Permission("READ", "document", "Read");
        Permission write = new Permission("WRITE", "document", "Write");
        Permission delete = new Permission("DELETE", "document", "Delete");

        Role adminRole = new Role("ADMIN", "Admin", Set.of(read, write, delete));
        Role userRole = new Role("USER", "User", Set.of(read));
        Role managerRole = new Role("MANAGER", "Manager", Set.of(read, write));

        roleManager.add(adminRole);
        roleManager.add(userRole);
        roleManager.add(managerRole);

        assignmentManager = new AssignmentManager(userManager, roleManager, auditLog);

        var assignment = new PermanentAssignment(testAdmin, adminRole, AssignmentMetadata.now("system", "test"));
        var tempAssignment = new TemporaryAssignment(tempUser, userRole, AssignmentMetadata.now("system", "test"), "2026-02-20", false);
        assignmentManager.add(assignment);
        assignmentManager.add(tempAssignment);
    }

    public String generateStatistics() {
        int userCount = userManager.count();
        int roleCount = roleManager.count();
        int assignmentCount = assignmentManager.count();

        String statsText = String.format(
                "Users: %d\nRoles: %d\nAssignments: %d",
                userCount, roleCount, assignmentCount
        );

        return FormatUtils.formatBox(statsText);
    }

    public void saveToFile() throws IOException {

        try (FileWriter writer = new FileWriter("data.txt")) {

            writer.write("USERS\n");
            for (User u : userManager.findAll()) {
                writer.write(
                        u.getUsername() + "|" +
                                u.getFullname() + "|" +
                                u.getEmail()
                );
                writer.write("\n");
            }

            writer.write("\nROLES\n");
            for (Role r : roleManager.findAll()) {
                writer.write(r.getId()+ "|" +
                        r.getName() + "|" +
                        r.getPermissions()+ "|"
                );
                writer.write("\n");
            }

            writer.write("\nASSIGNMENTS\n");
            for (RoleAssignment ra : assignmentManager.findAll()){
                writer.write(ra.assignmentId() + "|" +
                        ra.assignmentType() + "|" +
                        ra.user() + "|" +
                        ra.role() + "|"
                );
                writer.write("\n");
            }
        }
    }

    public ScheduledExecutorService scheduleTasks(int N){
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            List<TemporaryAssignment> temporaryAssignments = new ArrayList<>();

            for (RoleAssignment ra: getAssignmentManager().findAll()){
                if(ra instanceof TemporaryAssignment temp)
                    temporaryAssignments.add(temp);
            }

            int expiredCount = 0;

            for (TemporaryAssignment ta : temporaryAssignments){
                if(ta.isExpired()){
                    synchronized (ta){
                        if(ta.isActive())
                            expiredCount++;
                    }
                }
            }

            System.out.println("expired:" + expiredCount + "\ntemp assignments: " + temporaryAssignments.size());

        }, 0, N, TimeUnit.SECONDS);

        return scheduler;
    }
}
