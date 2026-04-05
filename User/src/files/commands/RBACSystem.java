package commands;

import bds.*;
import managers.*;
import util.*;
import filters.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        userManager.add(testAdmin);

        Permission read = new Permission("READ", "document", "Read");
        Permission write = new Permission("WRITE", "document", "Write");
        Permission delete = new Permission("DELETE", "document", "Delete");

        Role adminRole = new Role("ADMIN", "Admin", Set.of(read, write, delete));
        Role userRole = new Role("USER", "bds.User", Set.of(read));
        Role managerRole = new Role("MANAGER", "Manager", Set.of(read, write));

        roleManager.add(adminRole);
        roleManager.add(userRole);
        roleManager.add(managerRole);

        assignmentManager = new AssignmentManager(userManager, roleManager, auditLog);

        var assignment = new PermanentAssignment(testAdmin, adminRole, AssignmentMetadata.now("system", "test"));
        assignmentManager.add(assignment);
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
}
