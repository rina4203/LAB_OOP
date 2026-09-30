/*
 * InstantCoffeeTest.java
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

@DisplayName("Розчинна кава")
class InstantCoffeeTest {

    private static final double DELTA = 1e-9;
    private static final BigDecimal PRICE = new BigDecimal("340");
    private static final Quality QUALITY = new Quality(6, 4, 5);

    @Test
    @DisplayName("описує свій фізичний стан та технологію")
    void describesState() {
        InstantCoffee coffee = createInstant(PackagingType.GLASS_JAR,
                InstantProcess.FREEZE_DRIED);

        assertEquals("розчинна", coffee.getPhysicalState());
        assertEquals("сублімована", coffee.getStateDetails());
        assertSame(InstantProcess.FREEZE_DRIED, coffee.getProcess());
    }

    @ParameterizedTest
    @EnumSource(InstantProcess.class)
    @DisplayName("бере густину від технології виробництва")
    void densityDependsOnProcess(InstantProcess process) {
        InstantCoffee coffee = createInstant(PackagingType.SACHET, process);

        assertEquals(process.getBulkDensity(), coffee.getBulkDensity(),
                DELTA);
    }

    @Test
    @DisplayName("пакетики займають більше місця, ніж банка")
    void sachetsTakeMoreSpaceThanJar() {
        InstantCoffee inJar = createInstant(PackagingType.GLASS_JAR,
                InstantProcess.FREEZE_DRIED);
        InstantCoffee inSachets = createInstant(PackagingType.SACHET,
                InstantProcess.FREEZE_DRIED);

        assertEquals(inJar.getCoffeeVolumeLiters(),
                inSachets.getCoffeeVolumeLiters(), DELTA);
        assertTrue(inSachets.getVolumeLiters() > inJar.getVolumeLiters());
    }

    @Test
    @DisplayName("264 г сублімованої кави у скляній банці - 1,92 л")
    void calculatesPackedVolume() {
        InstantCoffee coffee = new InstantCoffee("Gold Arabica",
                CoffeeVariety.ARABICA, 264, PRICE, PackagingType.GLASS_JAR,
                QUALITY, InstantProcess.FREEZE_DRIED);

        assertEquals(1.2, coffee.getCoffeeVolumeLiters(), DELTA);
        assertEquals(1.92, coffee.getVolumeLiters(), DELTA);
    }

    @ParameterizedTest
    @EnumSource(value = PackagingType.class,
            names = {"GLASS_JAR", "TIN_CAN", "SACHET"})
    @DisplayName("постачається в банках і пакетиках-стіках")
    void acceptsJarsAndSachets(PackagingType packaging) {
        InstantCoffee coffee = createInstant(packaging,
                InstantProcess.AGGLOMERATED);

        assertSame(packaging, coffee.getPackaging());
    }

    @ParameterizedTest
    @EnumSource(value = PackagingType.class,
            names = {"PAPER_BAG", "VACUUM_PACK"})
    @DisplayName("не постачається в паперових пакетах і вакуумі")
    void rejectsBags(PackagingType packaging) {
        assertThrows(IllegalArgumentException.class,
                () -> createInstant(packaging, InstantProcess.SPRAY_DRIED));
    }

    @Test
    @DisplayName("вимагає технологію виробництва")
    void requiresProcess() {
        assertThrows(NullPointerException.class,
                () -> createInstant(PackagingType.GLASS_JAR, null));
    }

    private static InstantCoffee createInstant(PackagingType packaging,
            InstantProcess process) {
        return new InstantCoffee("Gold Arabica", CoffeeVariety.ARABICA, 200,
                PRICE, packaging, QUALITY, process);
    }
}
