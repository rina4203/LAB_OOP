/*
 * FindCommand.java
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
 * Команда {@code find}: шукає товари за частиною назви
 * і в каталозі, і у фургоні.
 *
 * @author rina4203
 * @version 1.0
 */
public class FindCommand extends AbstractCommand {

    private final VanWorkspace workspace;
    private final CargoReportPrinter printer;

    /**
     * Створює команду.
     *
     * @param workspace робоче місце
     * @param printer   засіб виведення таблиць
     */
    public FindCommand(VanWorkspace workspace, CargoReportPrinter printer) {
        super("find", "find <частина назви>",
                "знайти товари за назвою в каталозі й фургоні");
        this.workspace = Objects.requireNonNull(workspace);
        this.printer = Objects.requireNonNull(printer);
    }

    @Override
    public void execute(Arguments arguments) {
        requireCount(arguments, 1, ANY_COUNT);
        String text = arguments.getRest(0);
        printMatches(workspace.getCatalog(), text);
        printMatches(workspace.getVan(), text);
    }

    private void printMatches(CoffeeCollection list, String text) {
        printer.printItems(capitalize(list.getDisplayName())
                + ": знайдено за \"" + text + "\"", list,
                list.findByName(text));
    }
}
