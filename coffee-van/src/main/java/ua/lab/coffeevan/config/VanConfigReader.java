/*
 * VanConfigReader.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.config;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.Properties;

import ua.lab.coffeevan.exception.InvalidDataException;
import ua.lab.coffeevan.model.Quality;
import ua.lab.coffeevan.model.QualityRange;

/**
 * Зчитує параметри ініціалізації з файлу у форматі {@code .properties}.
 *
 * <p>Приклад вмісту файлу:
 * <pre>
 * van.capacity.liters=30
 * van.budget=6000
 * search.aroma.min=7
 * search.aroma.max=10
 * search.acidity.min=5
 * search.acidity.max=9
 * search.body.min=4
 * search.body.max=8
 * </pre>
 *
 * @author rina4203
 * @version 1.0
 */
public class VanConfigReader {

    /** Ключ вантажного об'єму фургона, л. */
    public static final String CAPACITY_KEY = "van.capacity.liters";

    /** Ключ бюджету на закупівлю, грн. */
    public static final String BUDGET_KEY = "van.budget";

    /** Ключ нижньої межі аромату. */
    public static final String AROMA_MIN_KEY = "search.aroma.min";

    /** Ключ верхньої межі аромату. */
    public static final String AROMA_MAX_KEY = "search.aroma.max";

    /** Ключ нижньої межі кислотності. */
    public static final String ACIDITY_MIN_KEY = "search.acidity.min";

    /** Ключ верхньої межі кислотності. */
    public static final String ACIDITY_MAX_KEY = "search.acidity.max";

    /** Ключ нижньої межі тіла. */
    public static final String BODY_MIN_KEY = "search.body.min";

    /** Ключ верхньої межі тіла. */
    public static final String BODY_MAX_KEY = "search.body.max";

    /**
     * Зчитує параметри ініціалізації.
     *
     * @param reader джерело даних у форматі {@code .properties}
     * @return параметри фургона та пошуку
     * @throws InvalidDataException якщо параметр відсутній
     *                              чи має некоректне значення
     * @throws IOException          якщо сталася помилка читання
     */
    public VanConfig read(Reader reader) throws IOException {
        Properties properties = new Properties();
        properties.load(reader);
        try {
            double capacity = Double.parseDouble(
                    getRequired(properties, CAPACITY_KEY));
            BigDecimal budget = new BigDecimal(
                    getRequired(properties, BUDGET_KEY));
            Quality min = new Quality(
                    getInt(properties, AROMA_MIN_KEY),
                    getInt(properties, ACIDITY_MIN_KEY),
                    getInt(properties, BODY_MIN_KEY));
            Quality max = new Quality(
                    getInt(properties, AROMA_MAX_KEY),
                    getInt(properties, ACIDITY_MAX_KEY),
                    getInt(properties, BODY_MAX_KEY));
            return new VanConfig(capacity, budget,
                    new QualityRange(min, max));
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException(
                    "Некоректні параметри фургона: " + e.getMessage(), e);
        }
    }

    private static int getInt(Properties properties, String key)
            throws InvalidDataException {
        return Integer.parseInt(getRequired(properties, key));
    }

    private static String getRequired(Properties properties, String key)
            throws InvalidDataException {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new InvalidDataException(
                    "Відсутній параметр \"" + key + "\"");
        }
        return value.trim();
    }
}
