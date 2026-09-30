/*
 * CoffeeVanApplicationTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.lab.coffeevan.config.VanConfig;
import ua.lab.coffeevan.config.VanConfigReader;
import ua.lab.coffeevan.exception.InvalidDataException;
import ua.lab.coffeevan.io.CoffeeCatalogReader;
import ua.lab.coffeevan.model.Coffee;
import ua.lab.coffeevan.model.CoffeeBeans;
import ua.lab.coffeevan.model.CoffeeVariety;
import ua.lab.coffeevan.model.PackagingType;
import ua.lab.coffeevan.model.Quality;
import ua.lab.coffeevan.model.QualityRange;
import ua.lab.coffeevan.model.RoastLevel;
import ua.lab.coffeevan.report.CargoReportPrinter;
import ua.lab.coffeevan.van.CoffeeVan;
import ua.lab.coffeevan.van.VanLoader;

@ExtendWith(MockitoExtension.class)
@DisplayName("Програма \"Фургон кави\"")
class CoffeeVanApplicationTest {

    private static final double DELTA = 1e-9;
    private static final QualityRange RANGE = new QualityRange(
            new Quality(7, 5, 5), new Quality(10, 9, 8));

    private final Reader configSource = new StringReader("config");
    private final Reader catalogSource = new StringReader("catalog");

    @Mock
    private VanConfigReader configReader;

    @Mock
    private CoffeeCatalogReader catalogReader;

    @Mock
    private VanLoader vanLoader;

    @Mock
    private CargoReportPrinter printer;

    @Test
    @DisplayName("завантажує фургон, сортує вантаж і шукає за якістю")
    void runsFullScenario() throws IOException {
        Coffee cheapLowQuality = beans("Robusta", "500", new Quality(4, 2, 9));
        Coffee expensiveFine = beans("Kenya AA", "1600", new Quality(9, 8, 6));
        Coffee rejected = beans("Too much", "9000", new Quality(8, 8, 8));
        List<Coffee> catalog = List.of(expensiveFine, cheapLowQuality,
                rejected);
        when(configReader.read(configSource)).thenReturn(
                new VanConfig(20.0, new BigDecimal("3000"), RANGE));
        when(catalogReader.read(catalogSource)).thenReturn(catalog);
        when(vanLoader.load(any(CoffeeVan.class), eq(catalog)))
                .thenAnswer(invocation -> {
                    CoffeeVan van = invocation.getArgument(0);
                    van.load(expensiveFine);
                    van.load(cheapLowQuality);
                    return List.of(rejected);
                });
        CoffeeVanApplication application = createApplication();

        CoffeeVan van = application.run(configSource, catalogSource);

        assertEquals(20.0, van.getCapacityLiters(), DELTA);
        assertEquals(new BigDecimal("3000"), van.getBudget());
        assertEquals(List.of(cheapLowQuality, expensiveFine), van.getCargo());
        InOrder order = inOrder(printer);
        order.verify(printer).printLoadingSummary(van, List.of(rejected));
        order.verify(printer).printItems(
                CoffeeVanApplication.REJECTED_TITLE, List.of(rejected));
        order.verify(printer).printItems(CoffeeVanApplication.SORTED_TITLE,
                List.of(cheapLowQuality, expensiveFine));
        order.verify(printer).printItems(
                CoffeeVanApplication.SEARCH_TITLE + RANGE,
                List.of(expensiveFine));
    }

    @Test
    @DisplayName("не продовжує роботу з некоректними параметрами")
    void stopsOnInvalidConfig() throws IOException {
        when(configReader.read(configSource))
                .thenThrow(new InvalidDataException("bad config"));
        CoffeeVanApplication application = createApplication();

        assertThrows(InvalidDataException.class,
                () -> application.run(configSource, catalogSource));
        verifyNoInteractions(catalogReader, vanLoader, printer);
    }

    @Test
    @DisplayName("вимагає всі складові програми")
    void requiresAllComponents() {
        assertThrows(NullPointerException.class,
                () -> new CoffeeVanApplication(null, catalogReader,
                        vanLoader, printer));
        assertThrows(NullPointerException.class,
                () -> new CoffeeVanApplication(configReader, null,
                        vanLoader, printer));
        assertThrows(NullPointerException.class,
                () -> new CoffeeVanApplication(configReader, catalogReader,
                        null, printer));
        assertThrows(NullPointerException.class,
                () -> new CoffeeVanApplication(configReader, catalogReader,
                        vanLoader, null));
    }

    @Test
    @DisplayName("працює зі справжніми складовими та текстовими даними")
    void runsWithRealComponents() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        CoffeeVanApplication application = new CoffeeVanApplication(
                new VanConfigReader(), new CoffeeCatalogReader(),
                new VanLoader(), new CargoReportPrinter(
                        new PrintStream(output, true, StandardCharsets.UTF_8)));
        String config = String.join("\n",
                "van.capacity.liters=5", "van.budget=2000",
                "search.aroma.min=8", "search.aroma.max=10",
                "search.acidity.min=0", "search.acidity.max=10",
                "search.body.min=0", "search.body.max=10");
        String catalog = String.join("\n",
                "BEANS;Colombia;ARABICA;1000;1150;VACUUM_PACK;8;6;7;MEDIUM",
                "BEANS;Vietnam;ROBUSTA;1000;620;PAPER_BAG;5;2;9;DARK",
                "GROUND;Kenya AA;ARABICA;250;420;PAPER_BAG;9;9;6;MEDIUM");

        CoffeeVan van = application.run(new StringReader(config),
                new StringReader(catalog));

        assertEquals(2, van.getCargo().size());
        assertEquals("Colombia", van.getCargo().get(0).getName());
        assertEquals("Kenya AA", van.getCargo().get(1).getName());
        String report = output.toString(StandardCharsets.UTF_8);
        assertTrue(report.contains("завантажено 2, не вмістилося 1"));
        assertTrue(report.contains("Vietnam"));
    }

    @Test
    @DisplayName("повертає фургон, створений за параметрами")
    void returnsVanBuiltFromConfig() throws IOException {
        VanConfig config = new VanConfig(7.5, new BigDecimal("100"), RANGE);
        when(configReader.read(configSource)).thenReturn(config);
        when(catalogReader.read(catalogSource)).thenReturn(List.of());
        when(vanLoader.load(any(CoffeeVan.class), eq(List.of())))
                .thenReturn(List.of());

        CoffeeVan van = createApplication().run(configSource, catalogSource);

        assertEquals(7.5, van.getCapacityLiters(), DELTA);
        assertSame(config.getBudget(), van.getBudget());
    }

    private CoffeeVanApplication createApplication() {
        return new CoffeeVanApplication(configReader, catalogReader,
                vanLoader, printer);
    }

    private static Coffee beans(String name, String price, Quality quality) {
        return new CoffeeBeans(name, CoffeeVariety.ARABICA, 1000,
                new BigDecimal(price), PackagingType.PAPER_BAG, quality,
                RoastLevel.MEDIUM);
    }
}
