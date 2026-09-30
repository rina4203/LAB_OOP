/*
 * VanCommand.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.console.command;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import ua.lab.coffeevan.io.ValueParser;
import ua.lab.coffeevan.model.Coffee;
import ua.lab.coffeevan.report.CargoReportPrinter;
import ua.lab.coffeevan.van.VanWorkspace;

/**
 * Команда {@code van}: показує стан фургона або змінює його об'єм
 * і бюджет.
 *
 * @author rina4203
 * @version 1.0
 */
public class VanCommand extends AbstractCommand {

    private final VanWorkspace workspace;
    private final CargoReportPrinter printer;

    /**
     * Створює команду.
     *
     * @param workspace робоче місце
     * @param printer   засіб виведення таблиць
     */
    public VanCommand(VanWorkspace workspace, CargoReportPrinter printer) {
        super("van", "van [<об'єм_л> <бюджет_грн>]",
                "показати фургон або задати новий об'єм і бюджет");
        this.workspace = Objects.requireNonNull(workspace);
        this.printer = Objects.requireNonNull(printer);
    }

    @Override
    public void execute(Arguments arguments) {
        requireCount(arguments, 0, 2);
        if (arguments.count() == 1) {
            throw new IllegalArgumentException("використання: " + getUsage());
        }
        if (arguments.count() == 2) {
            double capacity = ValueParser.parseDouble(arguments.get(0),
                    "об'єм");
            BigDecimal budget = ValueParser.parseDecimal(arguments.get(1),
                    "бюджет");
            List<Coffee> returned = workspace.resizeVan(capacity, budget);
            if (!returned.isEmpty()) {
                printer.printItems("Не вмістилися в новий фургон "
                        + "і повернулися в каталог", returned);
            }
        }
        printer.printVanState(workspace.getVan());
        printer.printCollection(workspace.getVan());
    }
}
