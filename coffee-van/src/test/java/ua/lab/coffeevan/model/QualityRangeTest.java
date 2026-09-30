/*
 * QualityRangeTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("Діапазон параметрів якості")
class QualityRangeTest {

    private static final Quality MIN = new Quality(6, 4, 5);
    private static final Quality MAX = new Quality(9, 8, 7);

    private final QualityRange range = new QualityRange(MIN, MAX);

    @Test
    @DisplayName("містить якість усередині діапазону")
    void containsQualityInside() {
        assertTrue(range.contains(new Quality(7, 6, 6)));
    }

    @Test
    @DisplayName("межі діапазону входять до нього")
    void boundsAreInclusive() {
        assertTrue(range.contains(MIN));
        assertTrue(range.contains(MAX));
    }

    @ParameterizedTest(name = "аромат {0}, кислотність {1}, тіло {2}")
    @CsvSource({
            "5, 6, 6",
            "10, 6, 6",
            "7, 3, 6",
            "7, 9, 6",
            "7, 6, 4",
            "7, 6, 8"
    })
    @DisplayName("не містить якість, якщо хоч один параметр поза межами")
    void excludesQualityOutside(int aroma, int acidity, int body) {
        assertFalse(range.contains(new Quality(aroma, acidity, body)));
    }

    @Test
    @DisplayName("не приймає порожню якість для перевірки")
    void rejectsNullQuality() {
        assertThrows(NullPointerException.class, () -> range.contains(null));
    }

    @Test
    @DisplayName("вимагає обидві межі")
    void requiresBothBounds() {
        assertThrows(NullPointerException.class,
                () -> new QualityRange(null, MAX));
        assertThrows(NullPointerException.class,
                () -> new QualityRange(MIN, null));
    }

    @ParameterizedTest(name = "верхня межа {0}/{1}/{2}")
    @CsvSource({
            "5, 8, 7",
            "9, 3, 7",
            "9, 8, 4"
    })
    @DisplayName("відхиляє нижню межу, більшу за верхню")
    void rejectsInvertedBounds(int aroma, int acidity, int body) {
        Quality max = new Quality(aroma, acidity, body);

        assertThrows(IllegalArgumentException.class,
                () -> new QualityRange(MIN, max));
    }

    @Test
    @DisplayName("повертає свої межі")
    void returnsBounds() {
        assertSame(MIN, range.getMin());
        assertSame(MAX, range.getMax());
    }

    @Test
    @DisplayName("має зрозуміле текстове подання")
    void hasReadableToString() {
        assertEquals("аромат 6-9, кислотність 4-8, тіло 5-7",
                range.toString());
    }
}
