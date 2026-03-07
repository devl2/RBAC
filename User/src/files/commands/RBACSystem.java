package commands;

import bds.*;
import managers.*;
import util.*;
import filters.*;

import java.util.Set;

public class RBACSystem {
    private UserManager userManager;
    private RoleManager roleManager;
    private AssignmentManager assignmentManager;
    private String currentUser;
    private AuditLog auditLog;

    public UserManager getUserManager(){
        return userManager;
    }

    public RBACSystem(AuditLog auditLog) {this.auditLog = auditLog;}

    public RoleManager getRoleManager(){
        return roleManager;
    }

    public AssignmentManager getAssignmentManager(){
        return assignmentManager;
    }

    public AuditLog getAuditLog(){return auditLog;}

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

        return "Users: %d, Roles: %d,Assignments: %d" .formatted(userCount, roleCount, assignmentCount);
    }
}
