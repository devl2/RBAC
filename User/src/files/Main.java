import bds.Permission;
import bds.Role;
import bds.User;
import managers.AssignmentManager;
import managers.RoleManager;
import managers.UserManager;
import bds.AssignmentMetadata;
import commands.CommandParser;
import commands.CommandRegistry;
import commands.RBACSystem;
import util.AuditLog;
import util.ConsoleUtils;
import util.ReportGenerator;

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
        AuditLog auditLog;
        ConsoleUtils consoleUtils;
        ReportGenerator reportGenerator = new ReportGenerator();
        Scanner scanner = new Scanner(System.in);
        int min = 0;
        int max = 5;

        CommandParser parser;
        RBACSystem system;
        CommandRegistry registry = new CommandRegistry();

        auditLog = new AuditLog();
        parser = new CommandParser();
        system = new RBACSystem(auditLog);

        system.initialize();

//        ConsoleUtils.promptString(scanner, message, false);
//        ConsoleUtils.promptInt(scanner, message, min, max);

        System.out.println(reportGenerator.generateUserReport(system.getUserManager(), system.getAssignmentManager()));
        System.out.println(reportGenerator.generateRoleReport(system.getRoleManager(), system.getAssignmentManager()));

        System.out.println(system.generateStatistics());

        //registry.executeCommand("user-create", new Scanner(""), system);

    }

}