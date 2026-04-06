package util;

import java.util.List;
import java.util.Scanner;

public class ConsoleUtils {
    public static String promptString(Scanner scanner, String message, boolean required) {
        while (true) {
            System.out.print(message + ": ");

            if (!scanner.hasNextLine()) {
                if (!required) return "";
                else throw new IllegalStateException("Ожидаемый ввод отсутствует");
            }

            String input = scanner.nextLine().trim();

            if (!input.isEmpty() || !required) {
                return input;
            } else {
                System.out.println("Поле обязательно для заполнения!");
            }
        }
    }

    public static int promptInt(Scanner scanner, String message, int min, int max) {
        while (true) {
            System.out.print(message + " (" + min + "-" + max + "): ");
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.println("Введите число в диапазоне от " + min + " до " + max + "!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Некорректный ввод. Ожидается число!");
            }
        }
    }

    public static boolean promptYesNo(Scanner scanner, String message) {
        while (true) {
            System.out.print(message + " (yes/no): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("yes") || input.equals("y")) return true;
            if (input.equals("no") || input.equals("n")) return false;
        }
    }

    public static <T> T promptChoice(Scanner scanner, String message, List<T> options) {
        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException("Список опций пустой!");
        }

        while (true) {
            System.out.println(message + ":");
            for (int i = 0; i < options.size(); i++) {
                System.out.println("  " + (i + 1) + ") " + options.get(i).toString());
            }
            int choice = promptInt(scanner, "Выберите номер", 1, options.size());
            return options.get(choice - 1);
        }
    }
}