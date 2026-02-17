public class RoleFilters {
    public RoleFilter byName(String name){ return role -> role.getName().equals(name); }

    public RoleFilter byNameContains(String substring) {return role -> role.getName().contains(substring);}

    public RoleFilter hasPermission (Permission permission){ return role -> role.hasPermission(permission); }

    public RoleFilter hasPermission (String permissonName, String resource) {return role -> role.hasPermission(permissonName, resource); }

    public RoleFilter hasAtLeastNPermissions(int n){ return role -> role.getPermissions().size() >= n;}
}
