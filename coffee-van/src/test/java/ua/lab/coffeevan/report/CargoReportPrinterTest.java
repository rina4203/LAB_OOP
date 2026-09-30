/*
 * CargoReportPrinterTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.lab.coffeevan.TestCoffees;
import ua.lab.coffeevan.model.Coffee;
import ua.lab.coffeevan.model.CoffeeBeans;
import ua.lab.coffeevan.model.CoffeeVariety;
import ua.lab.coffeevan.model.InstantCoffee;
import ua.lab.coffeevan.model.InstantProcess;
import ua.lab.coffeevan.model.PackagingType;
import ua.lab.coffeevan.model.Quality;
import ua.lab.coffeevan.model.RoastLevel;
import ua.lab.coffeevan.stock.CoffeeCatalog;
import ua.lab.coffeevan.van.CoffeeVan;

@ExtendWith(MockitoExtension.class)
@DisplayName("Звіт про вантаж")
class CargoReportPrinterTest {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final CargoReportPrinter printer = new CargoReportPrinter(
            new PrintStream(output, true, StandardCharsets.UTF_8));

    @Mock
    private CoffeeVan van;

    @Mock
    private Coffee coffee;

    @Test
    @DisplayName("підсумок містить об'єм, бюджет і кількість товарів")
    void printsLoadingSummary() {
        when(van.getLoadedVolumeLiters()).thenReturn(17.356);
        when(van.getCapacityLiters()).thenReturn(18.0);
        when(van.getFreeVolumeLiters()).thenReturn(0.644);
        when(van.getCargoCost()).thenReturn(new BigDecimal("5585"));
        when(van.getBudget()).thenReturn(new BigDecimal("5600"));
        when(van.getRemainingBudget()).thenReturn(new BigDecimal("15"));
        when(van.getCargo()).thenReturn(Collections.nCopies(9, coffee));

        printer.printLoadingSummary(van, List.of(coffee, coffee));

        String report = output();
        assertTrue(report.contains("=== Фургон кави ==="));
        assertTrue(report.contains("зайнято 17,36 л з 18,00 л"));
        assertTrue(report.contains("(вільно 0,64 л)"));
        assertTrue(report.contains("витрачено 5585,00 грн з 5600,00 грн"));
        assertTrue(report.contains("(залишок 15,00 грн)"));
        assertTrue(report.contains("завантажено 9, не вмістилося 2"));
    }

    @Test
    @DisplayName("таблиця містить заголовок, нумерацію та характеристики")
    void printsItemsTable() {
        Coffee beans = new CoffeeBeans("Colombia Supremo",
                CoffeeVariety.ARABICA, 1000, new BigDecimal("1150"),
                PackagingType.VACUUM_PACK, new Quality(8, 6, 7),
                RoastLevel.MEDIUM);
        Coffee sticks = new InstantCoffee("Barista Sticks",
                CoffeeVariety.ARABICA, 36, new BigDecimal("85"),
                PackagingType.SACHET, new Quality(6, 4, 5),
                InstantProcess.FREEZE_DRIED);

        printer.printItems("Вантаж", List.of(beans, sticks));

        String[] lines = output().split("\\R");
        assertEquals("", lines[0]);
        assertEquals("--- Вантаж ---", lines[1]);
        assertTrue(lines[2].contains("Назва"));
        assertTrue(lines[2].contains("Грн/кг"));
        assertTrue(lines[3].startsWith("  1  Colombia Supremo"));
        assertTrue(lines[3].contains("арабіка"));
        assertTrue(lines[3].contains("у зернах, обсмаження середнє"));
        assertTrue(lines[3].contains("вакуумна упаковка"));
        assertTrue(lines[3].contains("1150,00"));
        assertTrue(lines[3].contains("2,75"));
        assertTrue(lines[3].endsWith("8/6/7"));
        assertTrue(lines[4].startsWith("  2  Barista Sticks"));
        assertTrue(lines[4].contains("розчинна, сублімована"));
        assertTrue(lines[4].contains("2361,11"));
        assertEquals(5, lines.length);
    }

    @Test
    @DisplayName("для порожнього списку виводить повідомлення")
    void printsEmptyMessage() {
        printer.printItems("Пошук", List.of());

        String report = output();
        assertTrue(report.contains("--- Пошук ---"));
        assertTrue(report.contains("(товарів немає)"));
        assertFalse(report.contains("Назва"));
    }

    @Test
    @DisplayName("стан фургона містить заголовок, об'єм, бюджет і кількість")
    void printsVanState() {
        CoffeeVan realVan = new CoffeeVan(5.0, new BigDecimal("2000"));
        realVan.load(TestCoffees.beans("Colombia", "1150"));

        printer.printVanState(realVan);

        String report = output();
        assertTrue(report.contains("=== Стан фургона ==="));
        assertTrue(report.contains("зайнято 2,75 л з 5,00 л"));
        assertTrue(report.contains("витрачено 1150,00 грн з 2000,00 грн"));
        assertTrue(report.contains("Товарів:   у фургоні 1"));
    }

    @Test
    @DisplayName("список виводиться з назвою, кількістю й номерами")
    void printsCollection() {
        CoffeeCatalog catalog = new CoffeeCatalog(List.of(
                TestCoffees.beans("Colombia", "1150"),
                TestCoffees.ground("Kenya AA", "420")));

        printer.printCollection(catalog);

        String report = output();
        assertTrue(report.contains("--- Каталог (товарів: 2) ---"));
        assertTrue(report.contains("  1  Colombia"));
        assertTrue(report.contains("  2  Kenya AA"));
    }

    @Test
    @DisplayName("знайдені товари показуються з номерами у своєму списку")
    void printsItemsWithSourceNumbers() {
        Coffee kenya = TestCoffees.ground("Kenya AA", "420");
        CoffeeCatalog catalog = new CoffeeCatalog(List.of(
                TestCoffees.beans("Colombia", "1150"),
                TestCoffees.instant("Gold", "340"), kenya));

        printer.printItems("Знайдено", catalog, List.of(kenya));

        String[] lines = output().split("\\R");
        assertTrue(lines[3].startsWith("  3  Kenya AA"));
        assertEquals(4, lines.length);
    }

    @Test
    @DisplayName("вимагає потік виведення")
    void requiresOutputStream() {
        assertThrows(NullPointerException.class,
                () -> new CargoReportPrinter(null));
    }

    private String output() {
        return output.toString(StandardCharsets.UTF_8);
    }
}
