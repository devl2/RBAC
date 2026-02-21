import java.util.Comparator;
import java.util.Set;

public class AssignmentSorters {
    public Comparator<RoleAssignment> byUsername(){
        return new Comparator<RoleAssignment>() {
            @Override
            public int compare(RoleAssignment o1, RoleAssignment o2) {
                if(o1 == null && o2 == null) return 0;
                if (o1 == null) return -1;
                if (o2 == null) return 1;

                String username1 = o1.user().getUsername();
                String username2 = o2.user().getUsername();

                if (username1 == null && username2 == null) return 0;
                if (username1 == null) return -1;
                if (username2 == null) return 1;

                return username1.compareTo(username2);
            }
        };
    }

    public Comparator<RoleAssignment> byRoleName(){
        return new Comparator<RoleAssignment>() {
            @Override
            public int compare(RoleAssignment o1, RoleAssignment o2) {
                if(o1 == null && o2 == null) return 0;
                if (o1 == null) return -1;
                if (o2 == null) return 1;

                String rolename1 = o1.role().getName();
                String rolename2 = o2.role().getName();

                if (rolename1 == null && rolename2 == null) return 0;
                if (rolename1 == null) return -1;
                if (rolename2 == null) return 1;

                return rolename1.compareTo(rolename2);
            }
        };
    }

    public Comparator<RoleAssignment> byAssignmentDate(){
        return new Comparator<RoleAssignment>() {
            @Override
            public int compare(RoleAssignment o1, RoleAssignment o2) {
                if(o1 == null && o2 == null) return 0;
                if (o1 == null) return -1;
                if (o2 == null) return 1;

                String date1 = o1.metadata().assignedAt();
                String date2 = o2.metadata().assignedAt();

                if (date1 == null && date2 == null) return 0;
                if (date1 == null) return -1;
                if (date2 == null) return 1;

                return date1.compareTo(date2);
            }
        };
    }
}
