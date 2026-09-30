/*
 * ListCommand.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.console.command;

import java.util.Objects;

import ua.lab.coffeevan.report.CargoReportPrinter;
import ua.lab.coffeevan.stock.CoffeeCollection;
import ua.lab.coffeevan.van.VanWorkspace;

/**
 * Команда {@code list}: показує каталог і/або вантаж фургона.
 *
 * @author rina4203
 * @version 1.0
 */
public class ListCommand extends AbstractCommand {

    private final VanWorkspace workspace;
    private final CargoReportPrinter printer;

    /**
     * Створює команду.
     *
     * @param workspace робоче місце
     * @param printer   засіб виведення таблиць
     */
    public ListCommand(VanWorkspace workspace, CargoReportPrinter printer) {
        super("list", "list [catalog|van]",
                "показати каталог і/або фургон з номерами товарів");
        this.workspace = Objects.requireNonNull(workspace);
        this.printer = Objects.requireNonNull(printer);
    }

    @Override
    public void execute(Arguments arguments) {
        requireCount(arguments, 0, 1);
        boolean showCatalog = true;
        boolean showVan = true;
        if (!arguments.isEmpty()) {
            CoffeeCollection list = arguments.getList(0, workspace);
            showCatalog = list == workspace.getCatalog();
            showVan = !showCatalog;
        }
        if (showCatalog) {
            printer.printCollection(workspace.getCatalog());
        }
        if (showVan) {
            printer.printVanState(workspace.getVan());
            printer.printCollection(workspace.getVan());
        }
    }
}
