/*
 * ValueParserTest.java
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

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ua.lab.coffeevan.model.RoastLevel;

@DisplayName("Розбір введених значень")
class ValueParserTest {

    private static final double DELTA = 1e-9;

    @ParameterizedTest
    @ValueSource(strings = {"DARK", "dark", "  Dark "})
    @DisplayName("знаходить значення переліку незалежно від регістру")
    void parsesEnumIgnoringCase(String value) {
        assertSame(RoastLevel.DARK,
                ValueParser.parseEnum(RoastLevel.class, value, "обсмаження"));
    }

    @Test
    @DisplayName("для невідомого коду перелічує допустимі значення")
    void listsAllowedEnumValues() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> ValueParser.parseEnum(RoastLevel.class, " BURNT ",
                        "обсмаження"));

        assertEquals("обсмаження \"BURNT\" не існує, допустимі значення: "
                + "LIGHT, MEDIUM, DARK", e.getMessage());
    }

    @Test
    @DisplayName("розбирає ціле число")
    void parsesInt() {
        assertEquals(250, ValueParser.parseInt(" 250 ", "вага"));
    }

    @Test
    @DisplayName("пояснює, що очікувалося ціле число")
    void rejectsNonInteger() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> ValueParser.parseInt("пів кіло", "вага"));

        assertEquals("вага має бути цілим числом, отримано \"пів кіло\"",
                e.getMessage());
        assertInstanceOf(NumberFormatException.class, e.getCause());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1150,50", "1150.50", " 1150.50 "})
    @DisplayName("приймає дробові числа з комою чи крапкою")
    void parsesDecimalWithCommaOrDot(String value) {
        assertEquals(new BigDecimal("1150.50"),
                ValueParser.parseDecimal(value, "ціна"));
        assertEquals(1150.5, ValueParser.parseDouble(value, "ціна"), DELTA);
    }

    @Test
    @DisplayName("пояснює, що очікувалося число")
    void rejectsNonNumber() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> ValueParser.parseDouble("багато", "об'єм"));

        assertEquals("об'єм має бути числом, отримано \"багато\"",
                e.getMessage());
    }

    @Test
    @DisplayName("перелічує коди значень через кому")
    void listsCodes() {
        assertEquals("LIGHT, MEDIUM, DARK",
                ValueParser.codes(RoastLevel.values()));
    }
}
