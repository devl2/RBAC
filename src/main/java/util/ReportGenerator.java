package util;

import bds.Role;
import bds.RoleAssignment;
import bds.User;
import managers.AssignmentManager;
import managers.RoleManager;
import managers.UserManager;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportGenerator {

    public String generateUserReport(UserManager userManager, AssignmentManager assignmentManager) {

        Map<User, List<Role>> userRolesMap = assignmentManager.getUserRolesMap();

        String result = userManager.findAll()
                .parallelStream()
                .map(user -> {
                    String roles = userRolesMap.getOrDefault(user, List.of())
                            .stream()
                            .map(role -> "   - " + role.getName())
                            .collect(Collectors.joining("\n"));

                    if (roles.isEmpty()) {
                        roles = "  Roles: none";
                    } else {
                        roles = "  Roles:\n" + roles;
                    }

                    return String.format("User: %s\n%s\n", user.getUsername(), roles);
                })
                .collect(Collectors.joining("\n"));

        return "USER REPORT\n" + result;
    }


    public String generateRoleReport(RoleManager roleManager, AssignmentManager assignmentManager) {

        String result = roleManager.findAll()
                .parallelStream()
                .map(role -> {
                    int count = assignmentManager.findByRole(role).size();
                    return String.format("Role: %-20s Users: %d", role.getName(), count);
                })
                .collect(Collectors.joining("\n"));

        return "ROLE REPORT\n" + result;
    }


    public String generatePermissionMatrix(UserManager userManager, AssignmentManager assignmentManager) {

        String result = userManager.findAll()
                .parallelStream()
                .map(user -> {
                    List<RoleAssignment> roles = assignmentManager.findByUser(user);

                    String permissions = roles.isEmpty()
                            ? "No roles"
                            : roles.stream()
                            .map(ra -> ra.role().getPermissions().toString())
                            .collect(Collectors.joining(" "));

                    return String.format("User: %-15s | %s", user.getUsername(), permissions);
                })
                .collect(Collectors.joining("\n"));

        return "PERMISSION MATRIX\n" + result;
    }


    public void exportToFile(String report, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(report);
            System.out.println("Report saved to " + filename);
        } catch (IOException e) {
            System.out.println("Error saving report: " + e.getMessage());
        }
    }
}