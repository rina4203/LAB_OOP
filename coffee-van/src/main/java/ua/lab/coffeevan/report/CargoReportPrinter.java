/*
 * CargoReportPrinter.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.report;

import java.io.PrintStream;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ua.lab.coffeevan.model.Coffee;
import ua.lab.coffeevan.model.Quality;
import ua.lab.coffeevan.van.CoffeeVan;

/**
 * Виводить звіти про вантаж фургона у вигляді текстових таблиць.
 *
 * @author rina4203
 * @version 1.0
 */
public class CargoReportPrinter {

    /** Локаль для форматування чисел (десяткова кома). */
    private static final Locale LOCALE = Locale.forLanguageTag("uk-UA");

    private static final String HEADER_FORMAT =
            "%3s  %-30s %-9s %-30s %-18s %6s %9s %9s %7s  %s%n";
    private static final String ROW_FORMAT =
            "%3d  %-30s %-9s %-30s %-18s %6d %9.2f %9.2f %7.2f  %s%n";
    private static final String EMPTY_LIST_MESSAGE = "  (товарів немає)";

    private final PrintStream out;

    /**
     * Створює засіб виведення звітів.
     *
     * @param out потік, у який виводяться звіти
     */
    public CargoReportPrinter(PrintStream out) {
        this.out = Objects.requireNonNull(out, "Потік виведення не задано");
    }

    /**
     * Виводить підсумки завантаження фургона.
     *
     * @param van      завантажений фургон
     * @param rejected товари, які не вдалося завантажити
     */
    public void printLoadingSummary(CoffeeVan van, List<Coffee> rejected) {
        out.println("=== Фургон кави ===");
        out.printf(LOCALE, "Об'єм:     зайнято %.2f л з %.2f л "
                        + "(вільно %.2f л)%n",
                van.getLoadedVolumeLiters(), van.getCapacityLiters(),
                van.getFreeVolumeLiters());
        out.printf(LOCALE, "Бюджет:    витрачено %.2f грн з %.2f грн "
                        + "(залишок %.2f грн)%n",
                van.getCargoCost(), van.getBudget(),
                van.getRemainingBudget());
        out.printf(LOCALE, "Товарів:   завантажено %d, не вмістилося %d%n",
                van.getCargo().size(), rejected.size());
    }

    /**
     * Виводить таблицю товарів із заголовком.
     *
     * @param title заголовок таблиці
     * @param items товари для виведення
     */
    public void printItems(String title, List<Coffee> items) {
        out.println();
        out.println("--- " + title + " ---");
        if (items.isEmpty()) {
            out.println(EMPTY_LIST_MESSAGE);
            return;
        }
        out.printf(LOCALE, HEADER_FORMAT, "№", "Назва", "Сорт", "Стан",
                "Упаковка", "Вага,г", "Ціна,грн", "Грн/кг", "Об'єм,л",
                "Аромат/Кисл./Тіло");
        int number = 1;
        for (Coffee coffee : items) {
            out.printf(LOCALE, ROW_FORMAT, number,
                    coffee.getName(),
                    coffee.getVariety().getDisplayName(),
                    coffee.getPhysicalState() + ", "
                            + coffee.getStateDetails(),
                    coffee.getPackaging().getDisplayName(),
                    coffee.getNetWeightGrams(),
                    coffee.getPrice(),
                    coffee.getPricePerKilogram(),
                    coffee.getVolumeLiters(),
                    formatQuality(coffee.getQuality()));
            number++;
        }
    }

    private static String formatQuality(Quality quality) {
        return quality.getAroma() + "/" + quality.getAcidity() + "/"
                + quality.getBody();
    }
}
