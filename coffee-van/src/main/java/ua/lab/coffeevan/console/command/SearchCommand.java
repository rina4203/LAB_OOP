/*
 * SearchCommand.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.console.command;

import java.util.Objects;

import ua.lab.coffeevan.io.ValueParser;
import ua.lab.coffeevan.model.Quality;
import ua.lab.coffeevan.model.QualityRange;
import ua.lab.coffeevan.report.CargoReportPrinter;
import ua.lab.coffeevan.stock.CoffeeCollection;
import ua.lab.coffeevan.van.VanWorkspace;

/**
 * Команда {@code search}: знаходить у фургоні (або каталозі) товари,
 * параметри якості яких потрапляють у діапазон.
 *
 * <p>Діапазон задається для аромату, кислотності й тіла у вигляді
 * {@code мін-макс} або одним числом. Заданий діапазон запам'ятовується
 * для наступних пошуків.
 *
 * @author rina4203
 * @version 1.0
 */
public class SearchCommand extends AbstractCommand {

    private static final int RANGE_COUNT = 3;
    private static final String RANGE_SEPARATOR = "-";

    private final VanWorkspace workspace;
    private final CargoReportPrinter printer;

    /**
     * Створює команду.
     *
     * @param workspace робоче місце
     * @param printer   засіб виведення таблиць
     */
    public SearchCommand(VanWorkspace workspace, CargoReportPrinter printer) {
        super("search", "search [catalog|van] [<аромат> <кисл.> <тіло>]",
                "знайти товари з якістю в діапазоні, напр.: "
                        + "search 7-10 5-9 5-8");
        this.workspace = Objects.requireNonNull(workspace);
        this.printer = Objects.requireNonNull(printer);
    }

    @Override
    public void execute(Arguments arguments) {
        requireCount(arguments, 0, RANGE_COUNT + 1);
        CoffeeCollection list = workspace.getVan();
        int first = 0;
        if (arguments.isList(0)) {
            list = arguments.getList(0, workspace);
            first = 1;
        }
        int rangeCount = arguments.count() - first;
        if (rangeCount == RANGE_COUNT) {
            int[] aroma = parseBounds(arguments.get(first), "аромат");
            int[] acidity = parseBounds(arguments.get(first + 1),
                    "кислотність");
            int[] body = parseBounds(arguments.get(first + 2), "тіло");
            workspace.setSearchRange(new QualityRange(
                    new Quality(aroma[0], acidity[0], body[0]),
                    new Quality(aroma[1], acidity[1], body[1])));
        } else if (rangeCount != 0) {
            throw new IllegalArgumentException("використання: " + getUsage());
        }
        QualityRange range = workspace.getSearchRange();
        printer.printItems(capitalize(list.getDisplayName()) + ": якість "
                + range, list, list.findByQuality(range));
    }

    private static int[] parseBounds(String text, String label) {
        String[] parts = text.split(RANGE_SEPARATOR, -1);
        if (parts.length == 1) {
            int value = ValueParser.parseInt(parts[0], label);
            return new int[] {value, value};
        }
        if (parts.length == 2) {
            return new int[] {ValueParser.parseInt(parts[0], label),
                ValueParser.parseInt(parts[1], label)};
        }
        throw new IllegalArgumentException(label + ": очікується діапазон "
                + "на кшталт 7-10, отримано \"" + text + "\"");
    }
}
