/*
 * InstantProcess.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.model;

/**
 * Технологія виробництва розчинної кави.
 *
 * <p>Від технології залежить форма частинок, а отже й насипна густина:
 * сублімовані кристали найлегші, порошок - найщільніший.
 *
 * @author rina4203
 * @version 1.0
 */
public enum InstantProcess implements DisplayNamed {

    /** Сублімована кава (висушена заморожуванням). */
    FREEZE_DRIED("сублімована", 0.22),

    /** Гранульована (агломерована) кава. */
    AGGLOMERATED("гранульована", 0.26),

    /** Порошкова кава (висушена розпиленням). */
    SPRAY_DRIED("порошкова", 0.30);

    private final String displayName;
    private final double bulkDensity;

    InstantProcess(String displayName, double bulkDensity) {
        this.displayName = displayName;
        this.bulkDensity = bulkDensity;
    }

    /**
     * Повертає назву технології українською мовою.
     *
     * @return назва технології
     */
    @Override
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Повертає насипну густину розчинної кави, отриманої за технологією.
     *
     * @return насипна густина, г/см³
     */
    public double getBulkDensity() {
        return bulkDensity;
    }
}
