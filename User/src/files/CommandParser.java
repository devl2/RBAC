import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CommandParser {
    public Map<String, Command> commands = new HashMap<>();
    public Map<String, String> commandDescriptions = new HashMap<>();

    public void registerCommand(String name, String description, Command command){
        commands.put(name, command);
        commandDescriptions.put(name, description);
    }

    public void executeCommand(String commandName, Scanner scanner, RBACSystem system){
        Command cmd = commands.get(commandName);
        if (cmd == null){
            System.out.println("Неверная команда: " + commandName);
            System.out.println("Введите 'help' для списка доступных команд");
            return;
        }
        cmd.execute(scanner, system);
    }

    public void printHelp() {
        System.out.println("Доступные команды:");
        System.out.printf("%-20s (%s)%n", "Команда", "Описание");
        System.out.println("------------------------------------------------------------");
        commandDescriptions.forEach((cmd, desc) ->
                System.out.printf("%-20s (%s)%n", cmd, desc));
    }

    public void parseAndExecute(String input, Scanner scanner, RBACSystem system) {

        if (input == null || input.isBlank()) {
            return;
        }

        String[] parts = input.trim().split("\\s+", 2);

        String commandName = parts[0].toLowerCase();
        String arguments = "";

        if (parts.length > 1) {
            arguments = parts[1];
        }

        Scanner commandScanner = new Scanner(arguments);

        executeCommand(commandName, commandScanner, system);
    }
}
