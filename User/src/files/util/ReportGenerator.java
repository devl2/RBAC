package util;

import bds.*;
import managers.*;
import filters.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ReportGenerator {

    public String generateUserReport(UserManager userManager, AssignmentManager assignmentManager) {
        StringBuilder sb = new StringBuilder();

        sb.append("USER REPORT\n");

        Map<User, List<Role>> userRolesMap = assignmentManager.getUserRolesMap();

        for (User user : userManager.findAll()) {

            sb.append(String.format("User: %s\n", user.getUsername()));

            List<Role> roles = userRolesMap.get(user);

            if (roles == null || roles.isEmpty()) {
                sb.append("  Roles: none\n");
            } else {
                sb.append("  Roles:\n");
                for (Role role : roles) {
                    sb.append(String.format("   - %s\n", role.getName()));
                }
            }

            sb.append("\n");
        }

        return sb.toString();
    }


    public String generateRoleReport(RoleManager roleManager, AssignmentManager assignmentManager) {
        StringBuilder sb = new StringBuilder();

        sb.append("ROLE REPORT \n");

        for (Role role : roleManager.findAll()) {

            int count = assignmentManager.findByRole(role).size();

            sb.append(String.format("Role: %-20s Users: %d\n", role.getName(), count));
        }

        return sb.toString();
    }


    public String generatePermissionMatrix(UserManager userManager, AssignmentManager assignmentManager) {

        StringBuilder sb = new StringBuilder();

        sb.append("PERMISSION MATRIX\n");

        for (User user : userManager.findAll()) {

            sb.append(String.format("User: %-15s | ", user.getUsername()));

            List<RoleAssignment> roles = assignmentManager.findByUser(user);

            if (roles.isEmpty()) {
                sb.append("No roles");
            } else {
                for (RoleAssignment ra : roles) {
                    sb.append(ra.role().getPermissions()).append(" ");
                }
            }

            sb.append("\n");
        }

        return sb.toString();
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