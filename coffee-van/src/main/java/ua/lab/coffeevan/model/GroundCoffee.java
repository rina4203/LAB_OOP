/*
 * GroundCoffee.java
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
 * Мелена кава.
 *
 * <p>Постачається в паперових пакетах, вакуумній упаковці або бляшаних
 * банках. Насипна густина залежить від ступеня помелу.
 *
 * @author rina4203
 * @version 1.0
 */
public class GroundCoffee extends Coffee {

    /** Упаковки, в яких постачається мелена кава. */
    public static final Set<PackagingType> ALLOWED_PACKAGING =
            Set.of(PackagingType.PAPER_BAG,
                    PackagingType.VACUUM_PACK, PackagingType.TIN_CAN);

    private final GrindSize grindSize;

    /**
     * Створює товар - мелену каву.
     *
     * @param name           торгова назва товару
     * @param variety        сорт кави
     * @param netWeightGrams вага кави без упаковки, г
     * @param price          ціна за одиницю товару, грн
     * @param packaging      тип упаковки
     * @param quality        параметри якості
     * @param grindSize      ступінь помелу
     * @throws IllegalArgumentException якщо параметри некоректні або
     *                                  упаковка не підходить для меленої кави
     */
    public GroundCoffee(String name, CoffeeVariety variety,
            int netWeightGrams, BigDecimal price, PackagingType packaging,
            Quality quality, GrindSize grindSize) {
        super(name, variety, netWeightGrams, price, packaging, quality);
        this.grindSize = Objects.requireNonNull(grindSize,
                "Ступінь помелу не задано");
        requirePackaging(ALLOWED_PACKAGING);
    }

    /**
     * Повертає ступінь помелу.
     *
     * @return ступінь помелу
     */
    public GrindSize getGrindSize() {
        return grindSize;
    }

    @Override
    public String getPhysicalState() {
        return "мелена";
    }

    @Override
    public String getStateDetails() {
        return "помел " + grindSize.getDisplayName();
    }

    @Override
    public double getBulkDensity() {
        return grindSize.getBulkDensity();
    }
}
