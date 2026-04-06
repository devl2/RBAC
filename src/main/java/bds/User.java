package bds;
import util.ValidationUtils;

public record User(String username, String fullname, String email){
    public static User create(String username, String fullname, String email){
        ValidationUtils.requireNonEmpty(fullname, "fullname");

        if (!ValidationUtils.isValidEmail(email)){
            throw new IllegalArgumentException("Введен неверный email");
        }
        if (!ValidationUtils.isValidUsername(username)){
            throw new IllegalArgumentException("Поле username должен содержать только латинские буквы, цифры и подчёркивание");
        }
        return new User(username, fullname, email);
    }

    public String getUsername(){ return username(); }

    public String getFullname(){ return fullname(); }

    public String getEmail(){ return email(); }

    public String format(){
        return String.format("%s (%s) <%s>", username, fullname, email);
    }

}