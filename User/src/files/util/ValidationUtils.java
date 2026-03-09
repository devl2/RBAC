package util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ValidationUtils {
    public static boolean isValidUsername(String username){
        if(username == null){
            return false;
        }

        if(!username.matches("^[a-zA-Z0-9_]+$")){
            return false;
        }

        if(username.length() < 3 || username.length() > 20){
            return false;
        }

        return true;
    }

    public static boolean isValidEmail(String email){
        if (email == null){
            return false;
        }
        return email.matches("^[a-zA-Z0-9_]+@?[a-zA-Z]+\\.[a-zA-Z]+$");
    }

    public static boolean isDateValid(String date) {
        if (date == null){
            return false;
        }
        String DATE_FORMAT = "dd-MM-yyyy";
        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static String normalizeString(String input) {
        if (input == null) {
            return null;
        }

        return input.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    public static void requireNonEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " не может быть пустым");
        }
    }
}
