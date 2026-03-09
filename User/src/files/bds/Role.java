package bds;

import util.FormatUtils;

import java.util.*;

public class Role{
    private final String id;
    private String name;
    private String description;
    private Set<Permission> permissions;

    public Role (String name, String description, Set<Permission> permissions){
        this.id = createID();
        this.name = name;
        this.description = description;
        this.permissions = new HashSet<>(permissions);
    }

    private String createID(){
        return "role_" + UUID.randomUUID().toString();
    }

    public Set<Permission> getPermissions(){
        return Collections.unmodifiableSet(permissions);
    }

    public void addPermission(Permission permission){
        permissions.add(permission);
    }
    public void removePermission(Permission permission){
        permissions.remove(permission);
    }
    public boolean hasPermission(Permission permission){
        return permissions.contains(permission);
    }
    public boolean hasPermission(String permissionName, String resource){
        for (Permission p : permissions) {
            if(p.name().equals(permissionName) && p.resource().equals(resource)) {
                return true;
            }
        }
        return false;
    }

    public static Role create(String name, String description, Set<Permission> permissions){
        return new Role(name, description, permissions);
    }

    @Override
    public boolean equals(Object obj){
        if (obj == this){
            return true;
        }

        if (obj == null || obj.getClass() != this.getClass()){
            return false;
        }

        Role role = (Role) obj;
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode(){
        return Objects.hashCode(id);
    }

    @Override
    public String toString(){
        return String.format("bds.Role{id='%s', name='%s', permissions=%d}",
                id, name, permissions.size());
    }

    public String format() {
        String header = String.format(
                "Role: %s [ID: %s]\nDescription: %s\nPermissions (%d):",
                name, id, description, permissions.size()
        );

        List<String[]> rows = new ArrayList<>();
        for (Permission p : permissions) {
            rows.add(new String[]{p.format()});
        }

        String table = FormatUtils.formatTable(new String[]{"Permission"}, rows);

        return header + "\n" + table;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название роли не может быть пустым");
        }
        this.name = name;
    }

    public void setDescription(String description) {
        if (description == null) description = "";
        this.description = description;
    }

    public String getName(){
        return name;
    }

    public String getId(){
        return id;
    }
}