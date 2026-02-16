public class UserFilters {
    public UserFilter byUsername(String username){ return user -> user.getUsername().equals(username); }

    public UserFilter byUsernameContains(String substring){ return user -> user.getUsername().contains(substring); }

    public UserFilter byEmail(String email){ return user -> user.getEmail().equals(email); }

    public UserFilter byEmailDomain(String domain){ return user -> user.getEmail().contains("@");}

    public UserFilter byFullNameContains(String substring){ return user -> user.getFullname().contains(substring); }

}
