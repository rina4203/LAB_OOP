/*
 * VanConfigTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ua.lab.coffeevan.model.Quality;
import ua.lab.coffeevan.model.QualityRange;

@DisplayName("Параметри ініціалізації")
class VanConfigTest {

    private static final double DELTA = 1e-9;
    private static final BigDecimal BUDGET = new BigDecimal("5600");
    private static final QualityRange RANGE = new QualityRange(
            new Quality(7, 5, 5), new Quality(10, 9, 8));

    @Test
    @DisplayName("зберігає параметри фургона та пошуку")
    void storesParameters() {
        VanConfig config = new VanConfig(18.0, BUDGET, RANGE);

        assertEquals(18.0, config.getCapacityLiters(), DELTA);
        assertSame(BUDGET, config.getBudget());
        assertSame(RANGE, config.getSearchRange());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1.0, Double.NaN,
            Double.NEGATIVE_INFINITY})
    @DisplayName("відхиляє некоректний об'єм")
    void rejectsInvalidCapacity(double capacity) {
        assertThrows(IllegalArgumentException.class,
                () -> new VanConfig(capacity, BUDGET, RANGE));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-100"})
    @DisplayName("відхиляє недодатний бюджет")
    void rejectsNonPositiveBudget(String budget) {
        BigDecimal value = new BigDecimal(budget);

        assertThrows(IllegalArgumentException.class,
                () -> new VanConfig(18.0, value, RANGE));
    }

    @Test
    @DisplayName("вимагає бюджет і діапазон якості")
    void requiresBudgetAndRange() {
        assertThrows(NullPointerException.class,
                () -> new VanConfig(18.0, null, RANGE));
        assertThrows(NullPointerException.class,
                () -> new VanConfig(18.0, BUDGET, null));
    }
}
