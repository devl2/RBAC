import managers.AssignmentManager;
import managers.RoleManager;
import managers.UserManager;
import bds.AssignmentMetadata;
import commands.CommandParser;
import commands.CommandRegistry;
import commands.RBACSystem;
import util.AuditLog;
import util.ConsoleUtils;

import java.time.LocalDate;
import java.util.*;

public class Main {

    private static AssignmentMetadata metadata() {
        return new AssignmentMetadata(
                "system",
                "test",
                LocalDate.now().toString(),
                "system"
        );
    }

    public static void main(String[] args) {
        UserManager userManager;
        RoleManager roleManager;
        AssignmentManager assignmentManager;
        AuditLog auditLog;
        ConsoleUtils consoleUtils;
        String message = "test";
        Scanner scanner = new Scanner(System.in);
        int min = 0;
        int max = 5;

        CommandParser parser;
        RBACSystem system;
        CommandRegistry registry = new CommandRegistry();

        auditLog = new AuditLog();
        parser = new CommandParser();
        system = new RBACSystem(auditLog);
        userManager = new UserManager(auditLog);
        roleManager = new RoleManager(auditLog);
        assignmentManager = new AssignmentManager(userManager, roleManager, auditLog);

        system.initialize();

        ConsoleUtils.promptString(scanner, message, false);
        ConsoleUtils.promptInt(scanner, message, min, max);

        //registry.executeCommand("help", new Scanner(""), system);

    }

}