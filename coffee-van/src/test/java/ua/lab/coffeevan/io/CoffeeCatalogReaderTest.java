/*
 * CoffeeCatalogReaderTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ua.lab.coffeevan.exception.InvalidDataException;
import ua.lab.coffeevan.model.Coffee;
import ua.lab.coffeevan.model.CoffeeBeans;
import ua.lab.coffeevan.model.CoffeeVariety;
import ua.lab.coffeevan.model.GrindSize;
import ua.lab.coffeevan.model.GroundCoffee;
import ua.lab.coffeevan.model.InstantCoffee;
import ua.lab.coffeevan.model.InstantProcess;
import ua.lab.coffeevan.model.PackagingType;
import ua.lab.coffeevan.model.Quality;
import ua.lab.coffeevan.model.RoastLevel;

@DisplayName("Читання каталогу кави")
class CoffeeCatalogReaderTest {

    private static final String BEANS_LINE = "BEANS;Colombia Supremo;ARABICA;"
            + "1000;1150.00;VACUUM_PACK;8;6;7;MEDIUM";
    private static final String GROUND_LINE = "GROUND;Espresso Italiano;"
            + "ARABICA;250;330;TIN_CAN;8;5;8;FINE";
    private static final String INSTANT_LINE = "INSTANT;Crema Sticks;ROBUSTA;"
            + "50;95.50;SACHET;5;3;4;AGGLOMERATED";

    private final CoffeeCatalogReader reader = new CoffeeCatalogReader();

    @Test
    @DisplayName("створює каву у зернах з усіма характеристиками")
    void readsBeans() throws IOException {
        Coffee coffee = readSingle(BEANS_LINE);

        CoffeeBeans beans = assertInstanceOf(CoffeeBeans.class, coffee);
        assertEquals("Colombia Supremo", beans.getName());
        assertSame(CoffeeVariety.ARABICA, beans.getVariety());
        assertEquals(1000, beans.getNetWeightGrams());
        assertEquals(new BigDecimal("1150.00"), beans.getPrice());
        assertSame(PackagingType.VACUUM_PACK, beans.getPackaging());
        assertEquals(new Quality(8, 6, 7), beans.getQuality());
        assertSame(RoastLevel.MEDIUM, beans.getRoastLevel());
    }

    @Test
    @DisplayName("створює мелену каву")
    void readsGroundCoffee() throws IOException {
        Coffee coffee = readSingle(GROUND_LINE);

        GroundCoffee ground = assertInstanceOf(GroundCoffee.class, coffee);
        assertSame(GrindSize.FINE, ground.getGrindSize());
        assertSame(PackagingType.TIN_CAN, ground.getPackaging());
    }

    @Test
    @DisplayName("створює розчинну каву")
    void readsInstantCoffee() throws IOException {
        Coffee coffee = readSingle(INSTANT_LINE);

        InstantCoffee instant = assertInstanceOf(InstantCoffee.class, coffee);
        assertSame(InstantProcess.AGGLOMERATED, instant.getProcess());
        assertSame(CoffeeVariety.ROBUSTA, instant.getVariety());
        assertEquals(new BigDecimal("95.50"), instant.getPrice());
    }

    @Test
    @DisplayName("пропускає коментарі й порожні рядки, зберігає порядок")
    void skipsCommentsAndBlankLines() throws IOException {
        String catalog = String.join("\n",
                "# каталог",
                "",
                BEANS_LINE,
                "   ",
                "  # ще коментар",
                GROUND_LINE,
                INSTANT_LINE);

        List<Coffee> coffees = reader.read(new StringReader(catalog));

        assertEquals(3, coffees.size());
        assertInstanceOf(CoffeeBeans.class, coffees.get(0));
        assertInstanceOf(GroundCoffee.class, coffees.get(1));
        assertInstanceOf(InstantCoffee.class, coffees.get(2));
    }

    @Test
    @DisplayName("ігнорує пробіли навколо полів")
    void trimsFields() throws IOException {
        Coffee coffee = readSingle(" BEANS ; Kenya AA ; ARABICA ; 250 ; 420 ;"
                + " PAPER_BAG ; 9 ; 9 ; 6 ; LIGHT ");

        assertEquals("Kenya AA", coffee.getName());
        assertEquals(250, coffee.getNetWeightGrams());
    }

    @Test
    @DisplayName("повертає порожній каталог для порожнього файлу")
    void readsEmptyCatalog() throws IOException {
        assertTrue(reader.read(new StringReader("")).isEmpty());
    }

    @Test
    @DisplayName("зчитує вбудований каталог")
    void readsBundledCatalog() throws IOException {
        try (Reader source = DataSources.openResource("coffee-catalog.csv")) {
            List<Coffee> coffees = reader.read(source);

            assertEquals(15, coffees.size());
            assertTrue(coffees.stream()
                    .anyMatch(CoffeeBeans.class::isInstance));
            assertTrue(coffees.stream()
                    .anyMatch(GroundCoffee.class::isInstance));
            assertTrue(coffees.stream()
                    .anyMatch(InstantCoffee.class::isInstance));
        }
    }

    @Test
    @DisplayName("повідомляє номер рядка з неправильною кількістю полів")
    void reportsWrongFieldCount() {
        String catalog = "# коментар\n" + BEANS_LINE + "\nBEANS;Short;ARABICA";

        InvalidDataException e = assertThrows(InvalidDataException.class,
                () -> reader.read(new StringReader(catalog)));
        assertTrue(e.getMessage().startsWith("Рядок 3"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "LIQUID;Cold Brew;ARABICA;500;200;PAPER_BAG;7;5;6;MEDIUM",
            "BEANS;Mocca;MOCCA;500;200;PAPER_BAG;7;5;6;MEDIUM",
            "BEANS;Heavy;ARABICA;пів кіло;200;PAPER_BAG;7;5;6;MEDIUM",
            "BEANS;Free;ARABICA;500;безкоштовно;PAPER_BAG;7;5;6;MEDIUM",
            "BEANS;Boxed;ARABICA;500;200;BOX;7;5;6;MEDIUM",
            "BEANS;Perfect;ARABICA;500;200;PAPER_BAG;12;5;6;MEDIUM",
            "BEANS;Burnt;ARABICA;500;200;PAPER_BAG;7;5;6;BURNT",
            "GROUND;Dust;ARABICA;500;200;PAPER_BAG;7;5;6;POWDER",
            "INSTANT;Liquid;ARABICA;50;60;SACHET;5;3;4;LIQUID",
            "INSTANT;In bag;ARABICA;50;60;PAPER_BAG;5;3;4;FREEZE_DRIED",
            "BEANS;Empty;ARABICA;0;200;PAPER_BAG;7;5;6;MEDIUM"
    })
    @DisplayName("повідомляє про некоректний вміст рядка")
    void reportsInvalidContent(String line) {
        InvalidDataException e = assertThrows(InvalidDataException.class,
                () -> reader.read(new StringReader(line)));
        assertTrue(e.getMessage().startsWith("Рядок 1: "));
        assertInstanceOf(IllegalArgumentException.class, e.getCause());
    }

    private Coffee readSingle(String line) throws IOException {
        List<Coffee> coffees = reader.read(new StringReader(line));
        assertEquals(1, coffees.size());
        return coffees.get(0);
    }
}
