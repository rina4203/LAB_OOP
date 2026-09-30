/*
 * VanConfig.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.config;

import java.math.BigDecimal;
import java.util.Objects;

import ua.lab.coffeevan.model.QualityRange;

/**
 * Параметри ініціалізації програми: характеристики фургона
 * та діапазон якості для пошуку товарів.
 *
 * @author rina4203
 * @version 1.0
 */
public final class VanConfig {

    private final double capacityLiters;
    private final BigDecimal budget;
    private final QualityRange searchRange;

    /**
     * Створює набір параметрів.
     *
     * @param capacityLiters вантажний об'єм фургона, л
     * @param budget         бюджет на закупівлю кави, грн
     * @param searchRange    діапазон якості для пошуку товарів
     * @throws IllegalArgumentException якщо об'єм чи бюджет не додатні
     * @throws NullPointerException     якщо бюджет чи діапазон не задано
     */
    public VanConfig(double capacityLiters, BigDecimal budget,
            QualityRange searchRange) {
        Objects.requireNonNull(budget, "Бюджет не задано");
        if (!Double.isFinite(capacityLiters) || capacityLiters <= 0) {
            throw new IllegalArgumentException(
                    "Об'єм фургона має бути додатним, отримано "
                            + capacityLiters);
        }
        if (budget.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Бюджет має бути додатним, отримано " + budget);
        }
        this.capacityLiters = capacityLiters;
        this.budget = budget;
        this.searchRange = Objects.requireNonNull(searchRange,
                "Діапазон якості для пошуку не задано");
    }

    /**
     * Повертає вантажний об'єм фургона.
     *
     * @return об'єм, л
     */
    public double getCapacityLiters() {
        return capacityLiters;
    }

    /**
     * Повертає бюджет на закупівлю кави.
     *
     * @return бюджет, грн
     */
    public BigDecimal getBudget() {
        return budget;
    }

    /**
     * Повертає діапазон якості для пошуку товарів у фургоні.
     *
     * @return діапазон параметрів якості
     */
    public QualityRange getSearchRange() {
        return searchRange;
    }
}
