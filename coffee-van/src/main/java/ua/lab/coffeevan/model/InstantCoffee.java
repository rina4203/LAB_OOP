/*
 * InstantCoffee.java
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
 * Розчинна кава.
 *
 * <p>Постачається в банках (скляних чи бляшаних) або в порційних
 * пакетиках-стіках. Насипна густина залежить від технології виробництва.
 *
 * @author rina4203
 * @version 1.0
 */
public class InstantCoffee extends Coffee {

    /** Упаковки, в яких постачається розчинна кава. */
    public static final Set<PackagingType> ALLOWED_PACKAGING =
            Set.of(PackagingType.GLASS_JAR,
                    PackagingType.TIN_CAN, PackagingType.SACHET);

    private final InstantProcess process;

    /**
     * Створює товар - розчинну каву.
     *
     * @param name           торгова назва товару
     * @param variety        сорт кави
     * @param netWeightGrams вага кави без упаковки, г
     * @param price          ціна за одиницю товару, грн
     * @param packaging      тип упаковки
     * @param quality        параметри якості
     * @param process        технологія виробництва
     * @throws IllegalArgumentException якщо параметри некоректні або
     *                                  упаковка не підходить для розчинної
     *                                  кави
     */
    public InstantCoffee(String name, CoffeeVariety variety,
            int netWeightGrams, BigDecimal price, PackagingType packaging,
            Quality quality, InstantProcess process) {
        super(name, variety, netWeightGrams, price, packaging, quality);
        this.process = Objects.requireNonNull(process,
                "Технологію виробництва не задано");
        requirePackaging(ALLOWED_PACKAGING);
    }

    /**
     * Повертає технологію виробництва розчинної кави.
     *
     * @return технологія виробництва
     */
    public InstantProcess getProcess() {
        return process;
    }

    @Override
    public String getPhysicalState() {
        return "розчинна";
    }

    @Override
    public String getStateDetails() {
        return process.getDisplayName();
    }

    @Override
    public double getBulkDensity() {
        return process.getBulkDensity();
    }
}
