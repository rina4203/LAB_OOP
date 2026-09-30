/*
 * VanLoaderTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.van;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
@DisplayName("Вантажник фургона")
class VanLoaderTest {

    private static final Quality QUALITY = new Quality(7, 5, 6);

    private final VanLoader loader = new VanLoader();

    @Mock
    private CoffeeVan van;

    @Mock
    private Coffee first;

    @Mock
    private Coffee second;

    @Mock
    private Coffee third;

    @Test
    @DisplayName("завантажує товари, що вміщуються, у порядку каталогу")
    void loadsFittingCoffeeInCatalogOrder() {
        when(van.canLoad(first)).thenReturn(true);
        when(van.canLoad(second)).thenReturn(false);
        when(van.canLoad(third)).thenReturn(true);

        List<Coffee> rejected = loader.load(van,
                List.of(first, second, third));

        assertEquals(List.of(second), rejected);
        InOrder order = inOrder(van);
        order.verify(van).load(first);
        order.verify(van).load(third);
        verify(van, never()).load(second);
    }

    @Test
    @DisplayName("нічого не робить з порожнім каталогом")
    void doesNothingWithEmptyCatalog() {
        List<Coffee> rejected = loader.load(van, List.of());

        assertTrue(rejected.isEmpty());
        verifyNoInteractions(van);
    }

    @Test
    @DisplayName("вимагає фургон і каталог")
    void requiresVanAndCatalog() {
        List<Coffee> catalog = List.of(first);

        assertThrows(NullPointerException.class,
                () -> loader.load(null, catalog));
        assertThrows(NullPointerException.class,
                () -> loader.load(van, null));
    }

    @Test
    @DisplayName("заповнює справжній фургон у межах об'єму та бюджету")
    void fillsRealVanWithinLimits() {
        CoffeeVan realVan = new CoffeeVan(5.0, new BigDecimal("1500"));
        Coffee beans = new CoffeeBeans("Colombia Supremo",
                CoffeeVariety.ARABICA, 1000, new BigDecimal("1150"),
                PackagingType.VACUUM_PACK, QUALITY, RoastLevel.MEDIUM);
        Coffee bulkyGround = new GroundCoffee("India Cherry Robusta",
                CoffeeVariety.ROBUSTA, 1000, new BigDecimal("300"),
                PackagingType.VACUUM_PACK, QUALITY, GrindSize.FINE);
        Coffee pricyJar = new InstantCoffee("Gold Arabica",
                CoffeeVariety.ARABICA, 200, new BigDecimal("400"),
                PackagingType.GLASS_JAR, QUALITY,
                InstantProcess.FREEZE_DRIED);
        Coffee sticks = new InstantCoffee("Crema Sticks",
                CoffeeVariety.ARABICA, 50, new BigDecimal("95"),
                PackagingType.SACHET, QUALITY, InstantProcess.AGGLOMERATED);

        List<Coffee> rejected = loader.load(realVan,
                List.of(beans, bulkyGround, pricyJar, sticks));

        assertEquals(List.of(beans, sticks), realVan.getCargo());
        assertEquals(List.of(bulkyGround, pricyJar), rejected);
    }
}
