/*
 * QualityRange.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.model;

import java.util.Objects;

/**
 * Діапазон допустимих значень параметрів якості кави.
 *
 * <p>Нижня та верхня межі задаються об'єктами {@link Quality};
 * обидві межі включно входять до діапазону.
 *
 * @author rina4203
 * @version 1.0
 */
public final class QualityRange {

    private final Quality min;
    private final Quality max;

    /**
     * Створює діапазон параметрів якості.
     *
     * @param min нижні межі параметрів
     * @param max верхні межі параметрів
     * @throws NullPointerException     якщо одну з меж не задано
     * @throws IllegalArgumentException якщо нижня межа якогось параметра
     *                                  більша за верхню
     */
    public QualityRange(Quality min, Quality max) {
        this.min = Objects.requireNonNull(min, "Нижню межу якості не задано");
        this.max = Objects.requireNonNull(max, "Верхню межу якості не задано");
        if (min.getAroma() > max.getAroma()
                || min.getAcidity() > max.getAcidity()
                || min.getBody() > max.getBody()) {
            throw new IllegalArgumentException(
                    "Нижня межа якості не може перевищувати верхню: "
                            + min + " > " + max);
        }
    }

    /**
     * Перевіряє, чи всі параметри якості потрапляють у діапазон.
     *
     * @param quality параметри якості, що перевіряються
     * @return {@code true}, якщо кожен параметр лежить у своїх межах
     */
    public boolean contains(Quality quality) {
        Objects.requireNonNull(quality, "Параметри якості не задано");
        return isBetween(quality.getAroma(), min.getAroma(), max.getAroma())
                && isBetween(quality.getAcidity(), min.getAcidity(),
                        max.getAcidity())
                && isBetween(quality.getBody(), min.getBody(), max.getBody());
    }

    /**
     * Повертає нижні межі параметрів.
     *
     * @return нижні межі
     */
    public Quality getMin() {
        return min;
    }

    /**
     * Повертає верхні межі параметрів.
     *
     * @return верхні межі
     */
    public Quality getMax() {
        return max;
    }

    @Override
    public String toString() {
        return "аромат " + min.getAroma() + "-" + max.getAroma()
                + ", кислотність " + min.getAcidity() + "-" + max.getAcidity()
                + ", тіло " + min.getBody() + "-" + max.getBody();
    }

    private static boolean isBetween(int value, int lower, int upper) {
        return value >= lower && value <= upper;
    }
}
