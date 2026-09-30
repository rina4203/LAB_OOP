/*
 * CoffeeBeansTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@DisplayName("Кава у зернах")
class CoffeeBeansTest {

    private static final double DELTA = 1e-9;
    private static final BigDecimal PRICE = new BigDecimal("1150");
    private static final Quality QUALITY = new Quality(8, 6, 7);

    @Test
    @DisplayName("описує свій фізичний стан та обсмаження")
    void describesState() {
        CoffeeBeans beans = createBeans(PackagingType.VACUUM_PACK,
                RoastLevel.MEDIUM);

        assertEquals("у зернах", beans.getPhysicalState());
        assertEquals("обсмаження середнє", beans.getStateDetails());
        assertSame(RoastLevel.MEDIUM, beans.getRoastLevel());
    }

    @ParameterizedTest
    @EnumSource(RoastLevel.class)
    @DisplayName("бере густину від ступеня обсмаження")
    void densityDependsOnRoast(RoastLevel level) {
        CoffeeBeans beans = createBeans(PackagingType.PAPER_BAG, level);

        assertEquals(level.getBulkDensity(), beans.getBulkDensity(), DELTA);
    }

    @Test
    @DisplayName("кілограм зерен середнього обсмаження у вакуумі - 2,75 л")
    void calculatesPackedVolume() {
        CoffeeBeans beans = createBeans(PackagingType.VACUUM_PACK,
                RoastLevel.MEDIUM);

        assertEquals(2.5, beans.getCoffeeVolumeLiters(), DELTA);
        assertEquals(2.75, beans.getVolumeLiters(), DELTA);
    }

    @ParameterizedTest
    @EnumSource(value = PackagingType.class,
            names = {"PAPER_BAG", "VACUUM_PACK"})
    @DisplayName("постачається в пакетах")
    void acceptsBags(PackagingType packaging) {
        CoffeeBeans beans = createBeans(packaging, RoastLevel.DARK);

        assertSame(packaging, beans.getPackaging());
    }

    @ParameterizedTest
    @EnumSource(value = PackagingType.class,
            names = {"TIN_CAN", "GLASS_JAR", "SACHET"})
    @DisplayName("не постачається в банках і пакетиках-стіках")
    void rejectsOtherPackaging(PackagingType packaging) {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> createBeans(packaging, RoastLevel.DARK));

        assertTrue(e.getMessage().contains(packaging.getDisplayName()));
    }

    @Test
    @DisplayName("вимагає ступінь обсмаження")
    void requiresRoastLevel() {
        assertThrows(NullPointerException.class,
                () -> createBeans(PackagingType.PAPER_BAG, null));
    }

    private static CoffeeBeans createBeans(PackagingType packaging,
            RoastLevel roastLevel) {
        return new CoffeeBeans("Colombia Supremo", CoffeeVariety.ARABICA,
                1000, PRICE, packaging, QUALITY, roastLevel);
    }
}
