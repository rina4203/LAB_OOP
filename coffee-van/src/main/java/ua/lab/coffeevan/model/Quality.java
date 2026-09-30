/*
 * Quality.java
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
 * Параметри якості кави за результатами дегустації (капінгу).
 *
 * <p>Кожен параметр оцінюється цілим числом від {@link #MIN_SCORE}
 * до {@link #MAX_SCORE}. Об'єкт незмінний.
 *
 * @author rina4203
 * @version 1.0
 */
public final class Quality {

    /** Мінімальна оцінка параметра якості. */
    public static final int MIN_SCORE = 0;

    /** Максимальна оцінка параметра якості. */
    public static final int MAX_SCORE = 10;

    private final int aroma;
    private final int acidity;
    private final int body;

    /**
     * Створює набір параметрів якості.
     *
     * @param aroma   оцінка аромату
     * @param acidity оцінка кислотності
     * @param body    оцінка тіла (щільності смаку)
     * @throws IllegalArgumentException якщо оцінка поза межами шкали
     */
    public Quality(int aroma, int acidity, int body) {
        this.aroma = requireScore(aroma, "аромат");
        this.acidity = requireScore(acidity, "кислотність");
        this.body = requireScore(body, "тіло");
    }

    /**
     * Повертає оцінку аромату.
     *
     * @return оцінка аромату
     */
    public int getAroma() {
        return aroma;
    }

    /**
     * Повертає оцінку кислотності.
     *
     * @return оцінка кислотності
     */
    public int getAcidity() {
        return acidity;
    }

    /**
     * Повертає оцінку тіла (щільності смаку).
     *
     * @return оцінка тіла
     */
    public int getBody() {
        return body;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Quality)) {
            return false;
        }
        Quality quality = (Quality) other;
        return aroma == quality.aroma
                && acidity == quality.acidity
                && body == quality.body;
    }

    @Override
    public int hashCode() {
        return Objects.hash(aroma, acidity, body);
    }

    @Override
    public String toString() {
        return "аромат " + aroma + ", кислотність " + acidity
                + ", тіло " + body;
    }

    private static int requireScore(int score, String parameter) {
        if (score < MIN_SCORE || score > MAX_SCORE) {
            throw new IllegalArgumentException(String.format(
                    "Оцінка параметра \"%s\" має бути від %d до %d, "
                            + "отримано %d",
                    parameter, MIN_SCORE, MAX_SCORE, score));
        }
        return score;
    }
}
