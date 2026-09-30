/*
 * LoadCommand.java
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
 * Команда {@code load}: переносить товар з каталогу у фургон
 * або завантажує всі товари, що вміщуються.
 *
 * @author rina4203
 * @version 1.0
 */
public class LoadCommand extends AbstractCommand {

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
    public LoadCommand(VanWorkspace workspace, CargoReportPrinter printer,
            PrintStream out) {
        super("load", "load [<№>|all]",
                "перенести товар № з каталогу у фургон "
                        + "(без номера - усе, що вміщується)");
        this.workspace = Objects.requireNonNull(workspace);
        this.printer = Objects.requireNonNull(printer);
        this.out = Objects.requireNonNull(out);
    }

    @Override
    public void execute(Arguments arguments) {
        requireCount(arguments, 0, 1);
        if (arguments.isEmpty() || arguments.isAll(0)) {
            List<Coffee> loaded = workspace.loadAllThatFit();
            printer.printItems("Завантажено у фургон", loaded);
            out.println("У каталозі залишилося товарів: "
                    + workspace.getCatalog().size());
        } else {
            Coffee loaded = workspace.loadFromCatalog(
                    arguments.getItemIndex(0));
            out.println("Завантажено у фургон під №"
                    + workspace.getVan().size() + ": " + loaded);
        }
        printer.printVanState(workspace.getVan());
    }
}
