/*
 * EnumPropertiesTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@DisplayName("Довідники моделі кави")
class EnumPropertiesTest {

    private static final double DELTA = 1e-9;

    @ParameterizedTest
    @EnumSource(CoffeeVariety.class)
    @DisplayName("кожен сорт має українську назву")
    void everyVarietyHasDisplayName(CoffeeVariety variety) {
        assertFalse(variety.getDisplayName().isBlank());
    }

    @Test
    @DisplayName("назви сортів відповідають ботанічним")
    void varietyNames() {
        assertEquals("арабіка", CoffeeVariety.ARABICA.getDisplayName());
        assertEquals("робуста", CoffeeVariety.ROBUSTA.getDisplayName());
        assertEquals("ліберика", CoffeeVariety.LIBERICA.getDisplayName());
        assertEquals("ексцельза", CoffeeVariety.EXCELSA.getDisplayName());
    }

    @ParameterizedTest
    @EnumSource(RoastLevel.class)
    @DisplayName("кожен ступінь обсмаження має назву та густину")
    void everyRoastLevelIsDescribed(RoastLevel level) {
        assertFalse(level.getDisplayName().isBlank());
        assertTrue(level.getBulkDensity() > 0);
    }

    @Test
    @DisplayName("темніше обсмаження - менша густина зерен")
    void darkerRoastIsLighter() {
        assertTrue(RoastLevel.LIGHT.getBulkDensity()
                > RoastLevel.MEDIUM.getBulkDensity());
        assertTrue(RoastLevel.MEDIUM.getBulkDensity()
                > RoastLevel.DARK.getBulkDensity());
    }

    @ParameterizedTest
    @EnumSource(GrindSize.class)
    @DisplayName("кожен ступінь помелу має назву та густину")
    void everyGrindSizeIsDescribed(GrindSize size) {
        assertFalse(size.getDisplayName().isBlank());
        assertTrue(size.getBulkDensity() > 0);
    }

    @Test
    @DisplayName("дрібніший помел - більша густина")
    void finerGrindIsDenser() {
        assertTrue(GrindSize.FINE.getBulkDensity()
                > GrindSize.MEDIUM.getBulkDensity());
        assertTrue(GrindSize.MEDIUM.getBulkDensity()
                > GrindSize.COARSE.getBulkDensity());
    }

    @ParameterizedTest
    @EnumSource(InstantProcess.class)
    @DisplayName("кожна технологія розчинної кави має назву та густину")
    void everyInstantProcessIsDescribed(InstantProcess process) {
        assertFalse(process.getDisplayName().isBlank());
        assertTrue(process.getBulkDensity() > 0);
    }

    @Test
    @DisplayName("сублімована кава найлегша, порошкова - найщільніша")
    void freezeDriedIsLightest() {
        assertTrue(InstantProcess.FREEZE_DRIED.getBulkDensity()
                < InstantProcess.AGGLOMERATED.getBulkDensity());
        assertTrue(InstantProcess.AGGLOMERATED.getBulkDensity()
                < InstantProcess.SPRAY_DRIED.getBulkDensity());
    }

    @ParameterizedTest
    @EnumSource(PackagingType.class)
    @DisplayName("упаковка завжди збільшує об'єм товару")
    void packagingAddsVolume(PackagingType packaging) {
        assertFalse(packaging.getDisplayName().isBlank());
        assertTrue(packaging.getVolumeFactor() > 1.0);
        assertEquals(2.0 * packaging.getVolumeFactor(),
                packaging.calculatePackedVolume(2.0), DELTA);
    }

    @Test
    @DisplayName("об'єм у скляній банці обчислюється з її коефіцієнтом")
    void glassJarPackedVolume() {
        assertEquals(1.6, PackagingType.GLASS_JAR.calculatePackedVolume(1.0),
                DELTA);
    }
}
