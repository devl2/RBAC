package filters;

import bds.Permission;
import bds.Role;

import java.util.Comparator;
import java.util.Set;

public class RoleSorters {
    public Comparator<Role> byName(){
        return new Comparator<Role>() {
            @Override
            public int compare(Role o1, Role o2) {
                if(o1 == null && o2 == null) return 0;
                if (o1 == null) return -1;
                if (o2 == null) return 1;

                String name1 = o1.getName();
                String name2 = o2.getName();

                if (name1 == null && name2 == null) return 0;
                if (name1 == null) return -1;
                if (name2 == null) return 1;

                return name1.compareTo(name2);
            }
        };
    }

    public Comparator<Role> byPermissionCount(){
        return new Comparator<Role>() {
            @Override
            public int compare(Role o1, Role o2) {
                if(o1 == null && o2 == null) return 0;
                if (o1 == null) return -1;
                if (o2 == null) return 1;

                Set<Permission> permissions1 = o1.getPermissions();
                Set<Permission> permissions2 = o2.getPermissions();

                if (permissions1 == null && permissions2 == null) return 0;
                if (permissions1 == null) return -1;
                if (permissions2 == null) return 1;

                return Integer.compare(permissions1.size(), permissions2.size());
            }
        };

    }
}
