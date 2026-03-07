package filters;

import bds.User;

import java.util.Comparator;

public class UserSorters {
    public Comparator<User> byUsername(){
        return new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                if(o1 == null && o2 == null) return 0;
                if (o1 == null) return -1;
                if (o2 == null) return 1;

                String username1 = o1.getUsername();
                String username2 = o2.getUsername();

                if (username1 == null && username2 == null) return 0;
                if (username1 == null) return -1;
                if (username2 == null) return 1;

                return username1.compareTo(username2);
            }
        };
    }

    public Comparator<User> byFullname(){
        return new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                if(o1 == null && o2 == null) return 0;
                if (o1 == null) return -1;
                if (o2 == null) return 1;

                String fullname1 = o1.getFullname();
                String fullname2 = o2.getFullname();

                if (fullname1 == null && fullname2 == null) return 0;
                if (fullname1 == null) return -1;
                if (fullname2 == null) return 1;

                return fullname1.compareTo(fullname2);
            }
        };

    }

    public Comparator<User> byEmail(){
        return new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                if(o1 == null && o2 == null) return 0;
                if (o1 == null) return -1;
                if (o2 == null) return 1;

                String email1 = o1.getEmail();
                String email2 = o2.getEmail();

                if (email1 == null && email2 == null) return 0;
                if (email1 == null) return -1;
                if (email2 == null) return 1;

                return email1.compareTo(email2);
            }
        };

    }
}
