/*
 * CoffeeFormTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ua.lab.coffeevan.TestCoffees;
import ua.lab.coffeevan.model.Coffee;
import ua.lab.coffeevan.model.CoffeeBeans;
import ua.lab.coffeevan.model.CoffeeVariety;
import ua.lab.coffeevan.model.GrindSize;
import ua.lab.coffeevan.model.GroundCoffee;
import ua.lab.coffeevan.model.InstantCoffee;
import ua.lab.coffeevan.model.InstantProcess;
import ua.lab.coffeevan.model.PackagingType;
import ua.lab.coffeevan.model.Quality;
import ua.lab.coffeevan.model.RoastLevel;

@DisplayName("Фізичний стан кави в каталозі")
class CoffeeFormTest {

    private static final BigDecimal PRICE = new BigDecimal("300");
    private static final Quality QUALITY = new Quality(7, 5, 6);

    @Test
    @DisplayName("зерно створює CoffeeBeans з обсмаженням")
    void beansCreateCoffeeBeans() {
        Coffee coffee = CoffeeForm.BEANS.create("Beans",
                CoffeeVariety.ARABICA, 500, PRICE, PackagingType.PAPER_BAG,
                QUALITY, "dark");

        CoffeeBeans beans = assertInstanceOf(CoffeeBeans.class, coffee);
        assertSame(RoastLevel.DARK, beans.getRoastLevel());
        assertEquals("DARK", CoffeeForm.BEANS.getFeature(beans));
    }

    @Test
    @DisplayName("мелена кава створює GroundCoffee з помелом")
    void groundCreatesGroundCoffee() {
        Coffee coffee = CoffeeForm.GROUND.create("Ground",
                CoffeeVariety.ROBUSTA, 250, PRICE, PackagingType.TIN_CAN,
                QUALITY, "FINE");

        GroundCoffee ground = assertInstanceOf(GroundCoffee.class, coffee);
        assertSame(GrindSize.FINE, ground.getGrindSize());
        assertEquals("FINE", CoffeeForm.GROUND.getFeature(ground));
    }

    @Test
    @DisplayName("розчинна кава створює InstantCoffee з технологією")
    void instantCreatesInstantCoffee() {
        Coffee coffee = CoffeeForm.INSTANT.create("Instant",
                CoffeeVariety.ROBUSTA, 50, PRICE, PackagingType.SACHET,
                QUALITY, "spray_dried");

        InstantCoffee instant = assertInstanceOf(InstantCoffee.class, coffee);
        assertSame(InstantProcess.SPRAY_DRIED, instant.getProcess());
        assertEquals("SPRAY_DRIED", CoffeeForm.INSTANT.getFeature(instant));
    }

    @Test
    @DisplayName("пояснює, яка особливість стану невідома")
    void reportsUnknownFeature() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> CoffeeForm.GROUND.create("Ground",
                        CoffeeVariety.ARABICA, 250, PRICE,
                        PackagingType.PAPER_BAG, QUALITY, "DARK"));

        assertTrue(e.getMessage().startsWith("помел \"DARK\" не існує"));
    }

    @Test
    @DisplayName("визначає стан товару за його класом")
    void detectsFormOfCoffee() {
        assertSame(CoffeeForm.BEANS,
                CoffeeForm.of(TestCoffees.beans("B", "100")));
        assertSame(CoffeeForm.GROUND,
                CoffeeForm.of(TestCoffees.ground("G", "100")));
        assertSame(CoffeeForm.INSTANT,
                CoffeeForm.of(TestCoffees.instant("I", "100")));
        assertFalse(CoffeeForm.BEANS.describes(
                TestCoffees.ground("G", "100")));
    }

    @Test
    @DisplayName("не знає товарів інших класів")
    void rejectsUnknownCoffeeClass() {
        Coffee unknown = mock(Coffee.class);

        assertThrows(IllegalArgumentException.class,
                () -> CoffeeForm.of(unknown));
    }

    @ParameterizedTest
    @EnumSource(CoffeeForm.class)
    @DisplayName("має назву, особливість і підказки для введення")
    void describesItself(CoffeeForm form) {
        assertFalse(form.getDisplayName().isBlank());
        assertFalse(form.getFeatureLabel().isBlank());
        assertFalse(form.getFeatureValues().isEmpty());
        assertFalse(form.getAllowedPackaging().isEmpty());
    }

    @Test
    @DisplayName("перелічує упаковки в порядку оголошення")
    void listsPackagingInDeclarationOrder() {
        assertEquals(List.of(PackagingType.TIN_CAN, PackagingType.GLASS_JAR,
                PackagingType.SACHET),
                CoffeeForm.INSTANT.getAllowedPackaging());
        assertEquals(List.of(RoastLevel.values()),
                CoffeeForm.BEANS.getFeatureValues());
    }
}
