/*
 * GrindSize.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.model;

/**
 * Ступінь помелу меленої кави.
 *
 * <p>Дрібний помел щільніше заповнює упаковку, ніж крупний,
 * тому має більшу насипну густину.
 *
 * @author rina4203
 * @version 1.0
 */
public enum GrindSize {

    /** Дрібний помел (еспресо, турка). */
    FINE("дрібний", 0.45),

    /** Середній помел (фільтр-кава, пуровер). */
    MEDIUM("середній", 0.40),

    /** Крупний помел (френч-прес, колд-брю). */
    COARSE("крупний", 0.35);

    private final String displayName;
    private final double bulkDensity;

    GrindSize(String displayName, double bulkDensity) {
        this.displayName = displayName;
        this.bulkDensity = bulkDensity;
    }

    /**
     * Повертає назву ступеня помелу українською мовою.
     *
     * @return назва ступеня помелу
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Повертає насипну густину кави такого помелу.
     *
     * @return насипна густина, г/см³
     */
    public double getBulkDensity() {
        return bulkDensity;
    }
}
