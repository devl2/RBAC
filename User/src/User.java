public record User(String username, String fullname, String email){
    public static User validate(String username, String fullname, String email){
        if(username == null || username.isBlank()){
            throw new IllegalArgumentException("Строка не может быть пустой или null");
        }
        if(fullname == null || fullname.isBlank()){
            throw new IllegalArgumentException("Строка не может быть пустой или null");
        }
        if(email == null || email.isBlank()){
            throw new IllegalArgumentException("Строка не может быть пустой или null");
        }
        if(!username.matches("^[a-zA-Z0-9_]+$")){
            throw new IllegalArgumentException("Поле username должен содержать только латинские буквы, цифры и подчёркивание");
        }
        if(username.length() < 3 || username.length() > 20){
            throw new IllegalArgumentException("Поле username должен быть от 3 до 20 символов");
        }
        if (!email.matches("^[a-zA-Z0-9_]+@?[a-zA-Z]+\\.[a-zA-Z]+$")) {
            throw new IllegalArgumentException("Введен неверный email");
        }

        return new User(username, fullname, email);

    }

    public String format(){
        return String.format("%s (%s) <%s>", username, fullname, email);
    }
}