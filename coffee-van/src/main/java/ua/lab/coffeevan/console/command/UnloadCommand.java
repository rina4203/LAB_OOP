/*
 * UnloadCommand.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.console.command;

import java.io.PrintStream;
import java.util.List;
import java.util.Objects;

import ua.lab.coffeevan.model.Coffee;
import ua.lab.coffeevan.report.CargoReportPrinter;
import ua.lab.coffeevan.van.VanWorkspace;

/**
 * Команда {@code unload}: повертає товар (або весь вантаж) з фургона
 * в каталог.
 *
 * @author rina4203
 * @version 1.0
 */
public class UnloadCommand extends AbstractCommand {

    private final VanWorkspace workspace;
    private final CargoReportPrinter printer;
    private final PrintStream out;

    /**
     * Створює команду.
     *
     * @param workspace робоче місце
     * @param printer   засіб виведення таблиць
     * @param out       потік виведення
     */
    public UnloadCommand(VanWorkspace workspace, CargoReportPrinter printer,
            PrintStream out) {
        super("unload", "unload <№|all>",
                "повернути товар (або весь вантаж) з фургона в каталог");
        this.workspace = Objects.requireNonNull(workspace);
        this.printer = Objects.requireNonNull(printer);
        this.out = Objects.requireNonNull(out);
    }

    @Override
    public void execute(Arguments arguments) {
        requireCount(arguments, 1, 1);
        if (arguments.isAll(0)) {
            List<Coffee> unloaded = workspace.unloadAll();
            out.println("Повернуто в каталог товарів: " + unloaded.size());
        } else {
            Coffee unloaded = workspace.unload(arguments.getItemIndex(0));
            out.println("Повернуто в каталог під №"
                    + workspace.getCatalog().size() + ": " + unloaded);
        }
        printer.printVanState(workspace.getVan());
    }
}
