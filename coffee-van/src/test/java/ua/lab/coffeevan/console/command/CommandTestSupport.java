/*
 * CommandTestSupport.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.console.command;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import ua.lab.coffeevan.TestCoffees;
import ua.lab.coffeevan.config.VanConfig;
import ua.lab.coffeevan.model.Coffee;
import ua.lab.coffeevan.model.Quality;
import ua.lab.coffeevan.model.QualityRange;
import ua.lab.coffeevan.report.CargoReportPrinter;
import ua.lab.coffeevan.stock.CoffeeCollection;
import ua.lab.coffeevan.van.VanLoader;
import ua.lab.coffeevan.van.VanWorkspace;

/**
 * Спільні дані для тестів команд.
 *
 * <p>Каталог: Colombia Supremo (2,75 л, 1150 грн, якість 8/6/7),
 * Kenya AA (0,78 л, 420 грн, 9/9/6), Gold Arabica (1,45 л, 340 грн,
 * 6/4/5). Фургон: 5 л, 1600 грн - усі три товари разом не вміщуються
 * за бюджетом. Діапазон пошуку: аромат 7-10, кислотність 5-9, тіло 5-8.
 */
abstract class CommandTestSupport {

    protected static final QualityRange RANGE = new QualityRange(
            new Quality(7, 5, 5), new Quality(10, 9, 8));

    protected final ByteArrayOutputStream buffer =
            new ByteArrayOutputStream();
    protected final PrintStream out =
            new PrintStream(buffer, true, StandardCharsets.UTF_8);
    protected final CargoReportPrinter printer = new CargoReportPrinter(out);

    protected final Coffee colombia = TestCoffees.beans("Colombia Supremo",
            "1150");
    protected final Coffee kenya = TestCoffees.ground("Kenya AA", "420");
    protected final Coffee gold = TestCoffees.instant("Gold Arabica", "340");
    protected final VanWorkspace workspace = new VanWorkspace(
            new VanConfig(5.0, new BigDecimal("1600"), RANGE),
            List.of(colombia, kenya, gold), new VanLoader());

    protected String output() {
        return buffer.toString(StandardCharsets.UTF_8);
    }

    protected static Arguments args(String text) {
        return new Arguments(text);
    }

    protected static List<String> names(CoffeeCollection list) {
        return list.getItems().stream()
                .map(Coffee::getName)
                .collect(Collectors.toList());
    }
}
