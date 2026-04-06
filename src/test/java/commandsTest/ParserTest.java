package commandsTest;

import commands.Command;
import commands.CommandParser;
import commands.RBACSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.AuditLog;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    private CommandParser parser;
    private RBACSystem system;
    private AuditLog auditLog;

    @BeforeEach
    void setUp() {
        auditLog = new AuditLog();
        parser = new CommandParser();
        system = new RBACSystem(auditLog);
    }

    @Test
    @DisplayName("registerCommand: Регистрация команды и её описания")
    void shouldRegisterCommand() {
        Command dummy = (scanner, sys) -> {};

        parser.registerCommand("test", "описание", dummy);

        assertTrue(parser.commands.containsKey("test"));
        assertEquals("описание", parser.commandDescriptions.get("test"));
    }

    @Test
    @DisplayName("executeCommand: Выполнение существующей команды")
    void shouldExecuteExistingCommand() {
        AtomicBoolean executed = new AtomicBoolean(false);

        Command dummy = (scanner, sys) -> executed.set(true);

        parser.registerCommand("run", "описание", dummy);

        parser.executeCommand("run", new Scanner(""), system);

        assertTrue(executed.get());
    }

    @Test
    @DisplayName("executeCommand: Обработка неизвестной команды без исключений")
    void shouldHandleUnknownCommandWithoutException() {
        assertDoesNotThrow(() ->
                parser.executeCommand("unknown", new Scanner(""), system)
        );
    }

    @Test
    @DisplayName("parseAndExecute: Корректная передача аргументов в команду")
    void shouldPassArgumentsCorrectly() {
        AtomicBoolean argumentCorrect = new AtomicBoolean(false);

        parser.registerCommand("echo", "описание", (scanner, sys) -> {
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if ("hello world".equals(line)) {
                    argumentCorrect.set(true);
                }
            }
        });

        parser.parseAndExecute("echo hello world", new Scanner(""), system);

        assertTrue(argumentCorrect.get());
    }

    @Test
    @DisplayName("parseAndExecute: Игнорирование пустого или null ввода")
    void shouldIgnoreBlankInput() {
        assertDoesNotThrow(() ->
                parser.parseAndExecute("   ", new Scanner(""), system)
        );

        assertDoesNotThrow(() ->
                parser.parseAndExecute(null, new Scanner(""), system)
        );
    }

    @Test
    @DisplayName("printHelp: Вывод справки со всеми зарегистрированными командами")
    void shouldPrintHelpWithRegisteredCommands() {
        parser.registerCommand("cmd1", "desc1", (s, sys) -> {});
        parser.registerCommand("cmd2", "desc2", (s, sys) -> {});

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        parser.printHelp();

        String output = out.toString();

        assertTrue(output.contains("cmd1"));
        assertTrue(output.contains("desc1"));
        assertTrue(output.contains("cmd2"));
        assertTrue(output.contains("desc2"));
    }

}