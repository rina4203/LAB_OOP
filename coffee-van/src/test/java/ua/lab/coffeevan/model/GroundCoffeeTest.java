/*
 * GroundCoffeeTest.java
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

@DisplayName("Мелена кава")
class GroundCoffeeTest {

    private static final double DELTA = 1e-9;
    private static final BigDecimal PRICE = new BigDecimal("330");
    private static final Quality QUALITY = new Quality(8, 5, 8);

    @Test
    @DisplayName("описує свій фізичний стан та помел")
    void describesState() {
        GroundCoffee coffee = createGround(PackagingType.TIN_CAN,
                GrindSize.FINE);

        assertEquals("мелена", coffee.getPhysicalState());
        assertEquals("помел дрібний", coffee.getStateDetails());
        assertSame(GrindSize.FINE, coffee.getGrindSize());
    }

    @ParameterizedTest
    @EnumSource(GrindSize.class)
    @DisplayName("бере густину від ступеня помелу")
    void densityDependsOnGrind(GrindSize size) {
        GroundCoffee coffee = createGround(PackagingType.PAPER_BAG, size);

        assertEquals(size.getBulkDensity(), coffee.getBulkDensity(), DELTA);
    }

    @Test
    @DisplayName("225 г дрібного помелу в бляшанці - 0,675 л")
    void calculatesPackedVolume() {
        GroundCoffee coffee = new GroundCoffee("Espresso Italiano",
                CoffeeVariety.ARABICA, 225, PRICE, PackagingType.TIN_CAN,
                QUALITY, GrindSize.FINE);

        assertEquals(0.5, coffee.getCoffeeVolumeLiters(), DELTA);
        assertEquals(0.675, coffee.getVolumeLiters(), DELTA);
    }

    @ParameterizedTest
    @EnumSource(value = PackagingType.class,
            names = {"PAPER_BAG", "VACUUM_PACK", "TIN_CAN"})
    @DisplayName("постачається в пакетах і бляшанках")
    void acceptsBagsAndCans(PackagingType packaging) {
        GroundCoffee coffee = createGround(packaging, GrindSize.MEDIUM);

        assertSame(packaging, coffee.getPackaging());
    }

    @ParameterizedTest
    @EnumSource(value = PackagingType.class, names = {"GLASS_JAR", "SACHET"})
    @DisplayName("не постачається у скляних банках і пакетиках-стіках")
    void rejectsOtherPackaging(PackagingType packaging) {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> createGround(packaging, GrindSize.MEDIUM));

        assertTrue(e.getMessage().contains("мелена"));
    }

    @Test
    @DisplayName("вимагає ступінь помелу")
    void requiresGrindSize() {
        assertThrows(NullPointerException.class,
                () -> createGround(PackagingType.PAPER_BAG, null));
    }

    private static GroundCoffee createGround(PackagingType packaging,
            GrindSize grindSize) {
        return new GroundCoffee("Espresso Italiano", CoffeeVariety.ARABICA,
                250, PRICE, packaging, QUALITY, grindSize);
    }
}
