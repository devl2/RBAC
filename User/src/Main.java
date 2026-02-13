public static void main(String[] args){
    User test1 = User.validate("vasya_qwerty", "Vasya Qwerty", "vasya_qwerty@mail.ru");

    System.out.println(test1.format());

    try {
        User bad = User.validate("vasya^-^", "Vasya", "vasya_qwerty@mail.ru");
        System.out.println(bad.format());
    } catch (IllegalArgumentException e) {
        System.out.println(e.getMessage());
    }

    try{
        User bad2 = User.validate("vasya_qwerty", "  ", "vasya_qwerty@mail.ru");
        System.out.println(bad2.format());
    } catch (IllegalArgumentException e){
        System.out.println(e.getMessage());
    }

    try{
        User bad3 = User.validate("vasya_qwerty", "Vasya", "@@@mail.ru");
        System.out.println(bad3.format());
    } catch (IllegalArgumentException e){
        System.out.println(e.getMessage());
    }
}