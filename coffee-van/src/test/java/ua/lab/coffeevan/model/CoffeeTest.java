/*
 * CoffeeTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Базовий кавовий товар")
class CoffeeTest {

    private static final double DELTA = 1e-9;
    private static final String NAME = "Test Coffee";
    private static final BigDecimal PRICE = new BigDecimal("450");
    private static final Quality QUALITY = new Quality(7, 5, 6);

    @Test
    @DisplayName("зберігає спільні характеристики товару")
    void storesCommonProperties() {
        Coffee coffee = createCoffee(500, PRICE);

        assertEquals(NAME, coffee.getName());
        assertSame(CoffeeVariety.ARABICA, coffee.getVariety());
        assertEquals(500, coffee.getNetWeightGrams());
        assertEquals(new BigDecimal("450.00"), coffee.getPrice());
        assertSame(PackagingType.PAPER_BAG, coffee.getPackaging());
        assertSame(QUALITY, coffee.getQuality());
    }

    @Test
    @DisplayName("обрізає пробіли навколо назви")
    void trimsName() {
        Coffee coffee = new StubCoffee("  Kenya AA  ", CoffeeVariety.ARABICA,
                250, PRICE, PackagingType.PAPER_BAG, QUALITY);

        assertEquals("Kenya AA", coffee.getName());
    }

    @Test
    @DisplayName("обчислює об'єм кави за вагою та густиною")
    void calculatesCoffeeVolume() {
        Coffee coffee = createCoffee(500, PRICE);

        assertEquals(1.0, coffee.getCoffeeVolumeLiters(), DELTA);
    }

    @Test
    @DisplayName("враховує упаковку в об'ємі товару")
    void includesPackagingInVolume() {
        Coffee coffee = createCoffee(500, PRICE);

        assertEquals(1.25, coffee.getVolumeLiters(), DELTA);
    }

    @Test
    @DisplayName("обчислює ціну кілограма кави")
    void calculatesPricePerKilogram() {
        Coffee coffee = createCoffee(250, new BigDecimal("420"));

        assertEquals(new BigDecimal("1680.00"), coffee.getPricePerKilogram());
    }

    @Test
    @DisplayName("округлює ціну кілограма до копійок")
    void roundsPricePerKilogram() {
        Coffee coffee = createCoffee(36, new BigDecimal("85"));

        assertEquals(new BigDecimal("2361.11"), coffee.getPricePerKilogram());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    @DisplayName("відхиляє порожню назву")
    void rejectsBlankName(String name) {
        assertThrows(IllegalArgumentException.class,
                () -> new StubCoffee(name, CoffeeVariety.ARABICA, 250, PRICE,
                        PackagingType.PAPER_BAG, QUALITY));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -250})
    @DisplayName("відхиляє недодатну вагу")
    void rejectsNonPositiveWeight(int weight) {
        assertThrows(IllegalArgumentException.class,
                () -> createCoffee(weight, PRICE));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"0", "-10"})
    @DisplayName("відхиляє відсутню чи недодатну ціну")
    void rejectsInvalidPrice(String price) {
        BigDecimal value = (price == null) ? null : new BigDecimal(price);

        assertThrows(IllegalArgumentException.class,
                () -> createCoffee(250, value));
    }

    @Test
    @DisplayName("вимагає сорт, упаковку та параметри якості")
    void requiresVarietyPackagingAndQuality() {
        assertThrows(NullPointerException.class,
                () -> new StubCoffee(NAME, null, 250, PRICE,
                        PackagingType.PAPER_BAG, QUALITY));
        assertThrows(NullPointerException.class,
                () -> new StubCoffee(NAME, CoffeeVariety.ARABICA, 250, PRICE,
                        null, QUALITY));
        assertThrows(NullPointerException.class,
                () -> new StubCoffee(NAME, CoffeeVariety.ARABICA, 250, PRICE,
                        PackagingType.PAPER_BAG, null));
    }

    @Test
    @DisplayName("описує товар у текстовому поданні")
    void hasReadableToString() {
        Coffee coffee = createCoffee(500, PRICE);

        assertEquals("Test Coffee (арабіка, тестовий стан, тестові деталі, "
                + "паперовий пакет, 500 г, 450.00 грн)", coffee.toString());
    }

    private static Coffee createCoffee(int weight, BigDecimal price) {
        return new StubCoffee(NAME, CoffeeVariety.ARABICA, weight, price,
                PackagingType.PAPER_BAG, QUALITY);
    }

    /**
     * Найпростіша реалізація кави з густиною 0,5 г/см³ для перевірки
     * поведінки базового класу незалежно від підкласів.
     */
    private static final class StubCoffee extends Coffee {

        private static final double DENSITY = 0.5;

        StubCoffee(String name, CoffeeVariety variety, int weight,
                BigDecimal price, PackagingType packaging, Quality quality) {
            super(name, variety, weight, price, packaging, quality);
        }

        @Override
        public String getPhysicalState() {
            return "тестовий стан";
        }

        @Override
        public String getStateDetails() {
            return "тестові деталі";
        }

        @Override
        public double getBulkDensity() {
            return DENSITY;
        }
    }
}
