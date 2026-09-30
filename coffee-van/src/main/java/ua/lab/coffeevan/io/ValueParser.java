/*
 * ValueParser.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.io;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Перетворює текст, введений користувачем або прочитаний з файлу,
 * на значення потрібного типу з зрозумілими повідомленнями про помилки.
 *
 * <p>Коди переліків не залежать від регістру, а в дробових числах
 * дозволено як крапку, так і кому.
 *
 * @author rina4203
 * @version 1.0
 */
public final class ValueParser {

    private ValueParser() {
    }

    /**
     * Знаходить значення переліку за кодом.
     *
     * @param <E>   тип переліку
     * @param type  клас переліку
     * @param value код значення, наприклад {@code "paper_bag"}
     * @param label назва поля для повідомлення про помилку
     * @return значення переліку
     * @throws IllegalArgumentException якщо такого коду немає
     */
    public static <E extends Enum<E>> E parseEnum(Class<E> type,
            String value, String label) {
        String code = value.trim().toUpperCase(Locale.ROOT);
        for (E constant : type.getEnumConstants()) {
            if (constant.name().equals(code)) {
                return constant;
            }
        }
        throw new IllegalArgumentException(String.format(
                "%s \"%s\" не існує, допустимі значення: %s",
                label, value.trim(), codes(type.getEnumConstants())));
    }

    /**
     * Перетворює текст на ціле число.
     *
     * @param value текст
     * @param label назва поля для повідомлення про помилку
     * @return ціле число
     * @throws IllegalArgumentException якщо текст не є цілим числом
     */
    public static int parseInt(String value, String label) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format(
                    "%s має бути цілим числом, отримано \"%s\"",
                    label, value.trim()), e);
        }
    }

    /**
     * Перетворює текст на дробове число.
     *
     * @param value текст, наприклад {@code "12,5"}
     * @param label назва поля для повідомлення про помилку
     * @return число
     * @throws IllegalArgumentException якщо текст не є числом
     */
    public static double parseDouble(String value, String label) {
        return parseDecimal(value, label).doubleValue();
    }

    /**
     * Перетворює текст на точне десяткове число (для грошей).
     *
     * @param value текст, наприклад {@code "1150,50"}
     * @param label назва поля для повідомлення про помилку
     * @return число
     * @throws IllegalArgumentException якщо текст не є числом
     */
    public static BigDecimal parseDecimal(String value, String label) {
        try {
            return new BigDecimal(value.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format(
                    "%s має бути числом, отримано \"%s\"",
                    label, value.trim()), e);
        }
    }

    /**
     * Перелічує коди значень через кому.
     *
     * @param constants значення переліку
     * @return рядок на кшталт {@code "LIGHT, MEDIUM, DARK"}
     */
    public static String codes(Enum<?>[] constants) {
        List<String> names = Arrays.stream(constants)
                .map(Enum::name)
                .collect(Collectors.toList());
        return String.join(", ", names);
    }
}
