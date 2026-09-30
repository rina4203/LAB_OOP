/*
 * HelpCommand.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.console.command;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Objects;

/**
 * Команда {@code help}: виводить перелік усіх команд з підказками.
 *
 * @author rina4203
 * @version 1.0
 */
public class HelpCommand extends AbstractCommand {

    /** Команда виходу, яку обробляє сама оболонка. */
    public static final String EXIT_USAGE = "exit";

    private static final String EXIT_DESCRIPTION = "вийти з програми";

    private final Collection<Command> commands;
    private final PrintStream out;

    /**
     * Створює команду.
     *
     * @param commands усі команди оболонки (разом із цією)
     * @param out      потік виведення
     */
    public HelpCommand(Collection<Command> commands, PrintStream out) {
        super("help", "help", "показати перелік команд");
        this.commands = Objects.requireNonNull(commands);
        this.out = Objects.requireNonNull(out);
    }

    @Override
    public void execute(Arguments arguments) {
        requireCount(arguments, 0, 0);
        int width = commands.stream()
                .mapToInt(command -> command.getUsage().length())
                .max()
                .orElse(0);
        String format = "  %-" + Math.max(width, EXIT_USAGE.length())
                + "s  %s%n";
        out.println("Команди (номери товарів - як у таблицях, з 1; "
                + "списки: catalog - каталог, van - фургон):");
        for (Command command : commands) {
            out.printf(format, command.getUsage(), command.getDescription());
        }
        out.printf(format, EXIT_USAGE, EXIT_DESCRIPTION);
    }
}
