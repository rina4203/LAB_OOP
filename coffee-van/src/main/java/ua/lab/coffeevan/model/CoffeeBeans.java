/*
 * CoffeeBeans.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

/**
 * Кава у зернах.
 *
 * <p>Постачається в паперових пакетах або вакуумній упаковці.
 * Насипна густина залежить від ступеня обсмаження.
 *
 * @author rina4203
 * @version 1.0
 */
public class CoffeeBeans extends Coffee {

    /** Упаковки, в яких постачається кава у зернах. */
    public static final Set<PackagingType> ALLOWED_PACKAGING =
            Set.of(PackagingType.PAPER_BAG, PackagingType.VACUUM_PACK);

    private final RoastLevel roastLevel;

    /**
     * Створює товар - каву у зернах.
     *
     * @param name           торгова назва товару
     * @param variety        сорт кави
     * @param netWeightGrams вага кави без упаковки, г
     * @param price          ціна за одиницю товару, грн
     * @param packaging      тип упаковки
     * @param quality        параметри якості
     * @param roastLevel     ступінь обсмаження
     * @throws IllegalArgumentException якщо параметри некоректні або
     *                                  упаковка не підходить для зерен
     */
    public CoffeeBeans(String name, CoffeeVariety variety, int netWeightGrams,
            BigDecimal price, PackagingType packaging, Quality quality,
            RoastLevel roastLevel) {
        super(name, variety, netWeightGrams, price, packaging, quality);
        this.roastLevel = Objects.requireNonNull(roastLevel,
                "Ступінь обсмаження не задано");
        requirePackaging(ALLOWED_PACKAGING);
    }

    /**
     * Повертає ступінь обсмаження зерен.
     *
     * @return ступінь обсмаження
     */
    public RoastLevel getRoastLevel() {
        return roastLevel;
    }

    @Override
    public String getPhysicalState() {
        return "у зернах";
    }

    @Override
    public String getStateDetails() {
        return "обсмаження " + roastLevel.getDisplayName();
    }

    @Override
    public double getBulkDensity() {
        return roastLevel.getBulkDensity();
    }
}
