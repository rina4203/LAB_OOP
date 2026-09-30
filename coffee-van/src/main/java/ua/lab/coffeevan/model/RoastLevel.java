/*
 * RoastLevel.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.model;

/**
 * Ступінь обсмаження кавових зерен.
 *
 * <p>Під час обсмаження зерна збільшуються в розмірі та втрачають вологу,
 * тому чим темніше обсмаження, тим менша насипна густина зерен.
 *
 * @author rina4203
 * @version 1.0
 */
public enum RoastLevel implements DisplayNamed {

    /** Світле обсмаження. */
    LIGHT("світле", 0.43),

    /** Середнє обсмаження. */
    MEDIUM("середнє", 0.40),

    /** Темне обсмаження. */
    DARK("темне", 0.36);

    private final String displayName;
    private final double bulkDensity;

    RoastLevel(String displayName, double bulkDensity) {
        this.displayName = displayName;
        this.bulkDensity = bulkDensity;
    }

    /**
     * Повертає назву ступеня обсмаження українською мовою.
     *
     * @return назва ступеня обсмаження
     */
    @Override
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Повертає насипну густину зерен такого обсмаження.
     *
     * @return насипна густина, г/см³
     */
    public double getBulkDensity() {
        return bulkDensity;
    }
}
