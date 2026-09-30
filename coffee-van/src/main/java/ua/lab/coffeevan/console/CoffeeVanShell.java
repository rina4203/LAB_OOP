/*
 * CoffeeVanShell.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import ua.lab.coffeevan.CoffeeVanApplication;
import ua.lab.coffeevan.config.VanConfigReader;
import ua.lab.coffeevan.console.command.AddCommand;
import ua.lab.coffeevan.console.command.Arguments;
import ua.lab.coffeevan.console.command.Command;
import ua.lab.coffeevan.console.command.EditCommand;
import ua.lab.coffeevan.console.command.FindCommand;
import ua.lab.coffeevan.console.command.HelpCommand;
import ua.lab.coffeevan.console.command.ListCommand;
import ua.lab.coffeevan.console.command.LoadCommand;
import ua.lab.coffeevan.console.command.RemoveCommand;
import ua.lab.coffeevan.console.command.SaveCommand;
import ua.lab.coffeevan.console.command.SearchCommand;
import ua.lab.coffeevan.console.command.SortCommand;
import ua.lab.coffeevan.console.command.UnloadCommand;
import ua.lab.coffeevan.console.command.VanCommand;
import ua.lab.coffeevan.exception.CargoLoadingException;
import ua.lab.coffeevan.io.CoffeeCatalogReader;
import ua.lab.coffeevan.io.CoffeeCatalogWriter;
import ua.lab.coffeevan.io.CoffeeLineFormat;
import ua.lab.coffeevan.io.DataSources;
import ua.lab.coffeevan.report.CargoReportPrinter;
import ua.lab.coffeevan.van.VanLoader;
import ua.lab.coffeevan.van.VanWorkspace;

/**
 * Інтерактивний режим програми: користувач вводить команди, а програма
 * виконує їх над каталогом і фургоном.
 *
 * <p>Оболонка лише читає рядок, знаходить команду за першим словом
 * і передає їй решту рядка; уся робота з даними - у командах
 * та {@link VanWorkspace}.
 *
 * <p>Запуск: {@code java -cp coffee-van.jar
 * ua.lab.coffeevan.console.CoffeeVanShell [параметри [каталог]]}.
 *
 * @author rina4203
 * @version 1.0
 */
public class CoffeeVanShell {

    /** Запрошення до введення команди. */
    public static final String PROMPT = "> ";

    private static final List<String> EXIT_WORDS =
            List.of(HelpCommand.EXIT_USAGE, "quit", "вихід");
    private static final String WHITESPACE = "\\s+";

    private final BufferedReader input;
    private final PrintStream out;
    private final Map<String, Command> commands = new LinkedHashMap<>();

    /**
     * Створює оболонку з усіма командами.
     *
     * @param workspace робоче місце з каталогом і фургоном
     * @param input     джерело команд
     * @param out       потік виведення
     */
    public CoffeeVanShell(VanWorkspace workspace, BufferedReader input,
            PrintStream out) {
        Objects.requireNonNull(workspace, "Робоче місце не задано");
        this.input = Objects.requireNonNull(input, "Введення не задано");
        this.out = Objects.requireNonNull(out, "Виведення не задано");
        CargoReportPrinter printer = new CargoReportPrinter(out);
        CoffeeLineFormat format = new CoffeeLineFormat();
        register(new HelpCommand(commands.values(), out));
        register(new ListCommand(workspace, printer));
        register(new AddCommand(workspace, format, input, out));
        register(new EditCommand(workspace, format, out));
        register(new RemoveCommand(workspace, out));
        register(new FindCommand(workspace, printer));
        register(new VanCommand(workspace, printer));
        register(new LoadCommand(workspace, printer, out));
        register(new UnloadCommand(workspace, printer, out));
        register(new SortCommand(workspace, printer));
        register(new SearchCommand(workspace, printer));
        register(new SaveCommand(workspace, new CoffeeCatalogWriter(), out));
    }

    /**
     * Точка входу інтерактивного режиму.
     *
     * @param args необов'язкові шляхи до файлу параметрів та каталогу
     */
    public static void main(String[] args) {
        VanWorkspace workspace;
        try (Reader config = DataSources.open(args, 0,
                        CoffeeVanApplication.DEFAULT_CONFIG);
                Reader catalog = DataSources.open(args, 1,
                        CoffeeVanApplication.DEFAULT_CATALOG)) {
            workspace = new VanWorkspace(new VanConfigReader().read(config),
                    new CoffeeCatalogReader().read(catalog), new VanLoader());
        } catch (IOException e) {
            System.err.println("Помилка: " + e.getMessage());
            return;
        }
        BufferedReader input = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        try {
            new CoffeeVanShell(workspace, input, System.out).run();
        } catch (IOException e) {
            System.err.println("Помилка: " + e.getMessage());
        }
    }

    /**
     * Читає й виконує команди, доки користувач не введе {@code exit}
     * або введення не закінчиться.
     *
     * @throws IOException якщо команди неможливо прочитати
     */
    public void run() throws IOException {
        out.println("=== Фургон кави: інтерактивний режим ===");
        execute("help");
        String line = readCommand();
        while (line != null && !isExit(line)) {
            execute(line);
            line = readCommand();
        }
        out.println("До побачення!");
    }

    /**
     * Виконує один рядок з командою. Помилки не зупиняють оболонку -
     * вони виводяться, і можна вводити наступну команду.
     *
     * @param line рядок з назвою команди та аргументами
     */
    public void execute(String line) {
        String[] parts = line.trim().split(WHITESPACE, 2);
        if (parts[0].isEmpty()) {
            return;
        }
        Command command = commands.get(parts[0].toLowerCase(Locale.ROOT));
        if (command == null) {
            out.println("Невідома команда \"" + parts[0]
                    + "\". Перелік команд: help");
            return;
        }
        try {
            command.execute(new Arguments((parts.length > 1) ? parts[1] : ""));
        } catch (IOException | IllegalArgumentException
                | IndexOutOfBoundsException | CargoLoadingException e) {
            out.println("Помилка: " + e.getMessage());
        }
    }

    private String readCommand() throws IOException {
        out.println();
        out.print(PROMPT);
        out.flush();
        return input.readLine();
    }

    private static boolean isExit(String line) {
        return EXIT_WORDS.contains(line.trim().toLowerCase(Locale.ROOT));
    }

    private void register(Command command) {
        commands.put(command.getName(), command);
    }
}
