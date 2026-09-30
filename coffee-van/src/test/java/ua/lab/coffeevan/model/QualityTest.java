/*
 * QualityTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Параметри якості")
class QualityTest {

    @Test
    @DisplayName("зберігає оцінки аромату, кислотності та тіла")
    void storesScores() {
        Quality quality = new Quality(8, 6, 7);

        assertEquals(8, quality.getAroma());
        assertEquals(6, quality.getAcidity());
        assertEquals(7, quality.getBody());
    }

    @Test
    @DisplayName("приймає граничні оцінки шкали")
    void acceptsScaleBounds() {
        Quality lowest = new Quality(Quality.MIN_SCORE, Quality.MIN_SCORE,
                Quality.MIN_SCORE);
        Quality highest = new Quality(Quality.MAX_SCORE, Quality.MAX_SCORE,
                Quality.MAX_SCORE);

        assertEquals(Quality.MIN_SCORE, lowest.getAroma());
        assertEquals(Quality.MAX_SCORE, highest.getBody());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 11})
    @DisplayName("відхиляє аромат поза шкалою")
    void rejectsInvalidAroma(int score) {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> new Quality(score, 5, 5));

        assertTrue(e.getMessage().contains("аромат"));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 11})
    @DisplayName("відхиляє кислотність поза шкалою")
    void rejectsInvalidAcidity(int score) {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> new Quality(5, score, 5));

        assertTrue(e.getMessage().contains("кислотність"));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 11})
    @DisplayName("відхиляє тіло поза шкалою")
    void rejectsInvalidBody(int score) {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> new Quality(5, 5, score));

        assertTrue(e.getMessage().contains("тіло"));
    }

    @Test
    @DisplayName("рівні за однакових оцінок")
    void equalWhenScoresMatch() {
        Quality quality = new Quality(8, 6, 7);
        Quality same = new Quality(8, 6, 7);

        assertEquals(quality, quality);
        assertEquals(quality, same);
        assertEquals(quality.hashCode(), same.hashCode());
    }

    @Test
    @DisplayName("не рівні, якщо відрізняється хоча б одна оцінка")
    void notEqualWhenAnyScoreDiffers() {
        Quality quality = new Quality(8, 6, 7);

        assertNotEquals(quality, new Quality(9, 6, 7));
        assertNotEquals(quality, new Quality(8, 5, 7));
        assertNotEquals(quality, new Quality(8, 6, 6));
        assertNotEquals(quality, null);
        assertNotEquals(quality, "8/6/7");
    }

    @Test
    @DisplayName("має зрозуміле текстове подання")
    void hasReadableToString() {
        assertEquals("аромат 8, кислотність 6, тіло 7",
                new Quality(8, 6, 7).toString());
    }
}
