/*
 * VanConfigReaderTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.regex.Pattern;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ua.lab.coffeevan.exception.InvalidDataException;
import ua.lab.coffeevan.io.DataSources;
import ua.lab.coffeevan.model.Quality;

@DisplayName("Читання параметрів ініціалізації")
class VanConfigReaderTest {

    private static final double DELTA = 1e-9;
    private static final String VALID_CONFIG = String.join("\n",
            "# коментар",
            "van.capacity.liters = 25.5",
            "van.budget = 7000.50",
            "search.aroma.min = 6",
            "search.aroma.max = 9",
            "search.acidity.min = 4",
            "search.acidity.max = 8",
            "search.body.min = 5",
            "search.body.max = 10");

    private final VanConfigReader reader = new VanConfigReader();

    @Test
    @DisplayName("зчитує параметри фургона та діапазон якості")
    void readsValidConfig() throws IOException {
        VanConfig config = reader.read(new StringReader(VALID_CONFIG));

        assertEquals(25.5, config.getCapacityLiters(), DELTA);
        assertEquals(new BigDecimal("7000.50"), config.getBudget());
        assertEquals(new Quality(6, 4, 5), config.getSearchRange().getMin());
        assertEquals(new Quality(9, 8, 10), config.getSearchRange().getMax());
    }

    @Test
    @DisplayName("зчитує вбудований файл параметрів")
    void readsBundledConfig() throws IOException {
        try (Reader source = DataSources.openResource("van.properties")) {
            VanConfig config = reader.read(source);

            assertEquals(18.0, config.getCapacityLiters(), DELTA);
            assertEquals(new BigDecimal("5600"), config.getBudget());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            VanConfigReader.CAPACITY_KEY,
            VanConfigReader.BUDGET_KEY,
            VanConfigReader.AROMA_MIN_KEY,
            VanConfigReader.AROMA_MAX_KEY,
            VanConfigReader.ACIDITY_MIN_KEY,
            VanConfigReader.ACIDITY_MAX_KEY,
            VanConfigReader.BODY_MIN_KEY,
            VanConfigReader.BODY_MAX_KEY
    })
    @DisplayName("повідомляє про відсутній параметр")
    void reportsMissingParameter(String key) {
        String config = replaceLine(key, "");

        InvalidDataException e = assertThrows(InvalidDataException.class,
                () -> reader.read(new StringReader(config)));
        assertTrue(e.getMessage().contains(key));
    }

    @Test
    @DisplayName("повідомляє про порожнє значення параметра")
    void reportsBlankParameter() {
        String config = VALID_CONFIG.replace("= 7000.50", "=   ");

        InvalidDataException e = assertThrows(InvalidDataException.class,
                () -> reader.read(new StringReader(config)));
        assertTrue(e.getMessage().contains(VanConfigReader.BUDGET_KEY));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "van.capacity.liters = багато",
            "van.budget = 7k",
            "search.aroma.min = високий"
    })
    @DisplayName("повідомляє про нечислове значення")
    void reportsNonNumericValue(String line) {
        String key = line.substring(0, line.indexOf(' '));
        String config = replaceLine(key, line);

        InvalidDataException e = assertThrows(InvalidDataException.class,
                () -> reader.read(new StringReader(config)));
        assertInstanceOf(NumberFormatException.class, e.getCause());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "van.capacity.liters = -3",
            "van.budget = 0",
            "search.aroma.max = 11",
            "search.acidity.min = 9"
    })
    @DisplayName("повідомляє про значення поза допустимими межами")
    void reportsOutOfRangeValue(String line) {
        String key = line.substring(0, line.indexOf(' '));
        String config = replaceLine(key, line);

        InvalidDataException e = assertThrows(InvalidDataException.class,
                () -> reader.read(new StringReader(config)));
        assertTrue(e.getMessage().startsWith("Некоректні параметри фургона"));
    }

    private static String replaceLine(String key, String replacement) {
        return VALID_CONFIG.replaceAll(
                "(?m)^" + Pattern.quote(key) + " .*$", replacement);
    }
}
