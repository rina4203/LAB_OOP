/*
 * SortCommand.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.console.command;

import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;

import ua.lab.coffeevan.model.Coffee;
import ua.lab.coffeevan.report.CargoReportPrinter;
import ua.lab.coffeevan.stock.CoffeeCollection;
import ua.lab.coffeevan.van.VanWorkspace;

/**
 * Команда {@code sort}: сортує фургон (або каталог) за співвідношенням
 * ціни та ваги - ціною кілограма кави.
 *
 * @author rina4203
 * @version 1.0
 */
public class SortCommand extends AbstractCommand {

    private static final String ASCENDING = "asc";
    private static final String DESCENDING = "desc";

    private final VanWorkspace workspace;
    private final CargoReportPrinter printer;

    /**
     * Створює команду.
     *
     * @param workspace робоче місце
     * @param printer   засіб виведення таблиць
     */
    public SortCommand(VanWorkspace workspace, CargoReportPrinter printer) {
        super("sort", "sort [catalog|van] [asc|desc]",
                "відсортувати за ціною кілограма (типово - фургон, "
                        + "за зростанням)");
        this.workspace = Objects.requireNonNull(workspace);
        this.printer = Objects.requireNonNull(printer);
    }

    @Override
    public void execute(Arguments arguments) {
        requireCount(arguments, 0, 2);
        CoffeeCollection list = workspace.getVan();
        boolean descending = false;
        for (int i = 0; i < arguments.count(); i++) {
            String word = arguments.get(i).toLowerCase(Locale.ROOT);
            if (DESCENDING.equals(word)) {
                descending = true;
            } else if (!ASCENDING.equals(word)) {
                list = arguments.getList(i, workspace);
            }
        }
        Comparator<Coffee> order = descending
                ? Coffee.BY_PRICE_PER_KILOGRAM.reversed()
                : Coffee.BY_PRICE_PER_KILOGRAM;
        list.sort(order);
        printer.printCollection(list);
    }
}
