/*
 * CoffeeVariety.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.model;

/**
 * Ботанічний сорт кавового дерева, з плодів якого отримано каву.
 *
 * @author rina4203
 * @version 1.0
 */
public enum CoffeeVariety implements DisplayNamed {

    /** Арабіка (Coffea arabica). */
    ARABICA("арабіка"),

    /** Робуста (Coffea canephora). */
    ROBUSTA("робуста"),

    /** Ліберика (Coffea liberica). */
    LIBERICA("ліберика"),

    /** Ексцельза (Coffea excelsa). */
    EXCELSA("ексцельза");

    private final String displayName;

    CoffeeVariety(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Повертає назву сорту українською мовою.
     *
     * @return назва сорту
     */
    @Override
    public String getDisplayName() {
        return displayName;
    }
}
