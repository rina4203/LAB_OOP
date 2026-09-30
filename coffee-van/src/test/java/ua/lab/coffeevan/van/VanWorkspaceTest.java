/*
 * VanWorkspaceTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.van;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ua.lab.coffeevan.TestCoffees;
import ua.lab.coffeevan.config.VanConfig;
import ua.lab.coffeevan.exception.CargoLoadingException;
import ua.lab.coffeevan.model.Coffee;
import ua.lab.coffeevan.model.Quality;
import ua.lab.coffeevan.model.QualityRange;

@DisplayName("Робоче місце: каталог і фургон")
class VanWorkspaceTest {

    private static final double DELTA = 1e-9;
    private static final QualityRange RANGE = new QualityRange(
            new Quality(7, 5, 5), new Quality(10, 9, 8));
    private static final VanConfig CONFIG =
            new VanConfig(5.0, new BigDecimal("1600"), RANGE);

    private final Coffee colombia = TestCoffees.beans("Colombia Supremo",
            "1150");
    private final Coffee kenya = TestCoffees.ground("Kenya AA", "420");
    private final Coffee gold = TestCoffees.instant("Gold Arabica", "340");
    private final VanWorkspace workspace = new VanWorkspace(CONFIG,
            List.of(colombia, kenya, gold), new VanLoader());

    @Test
    @DisplayName("починає з повним каталогом і порожнім фургоном")
    void startsWithFullCatalogAndEmptyVan() {
        assertEquals(List.of(colombia, kenya, gold),
                workspace.getCatalog().getItems());
        assertTrue(workspace.getVan().isEmpty());
        assertEquals(5.0, workspace.getVan().getCapacityLiters(), DELTA);
        assertEquals(new BigDecimal("1600"), workspace.getVan().getBudget());
        assertSame(RANGE, workspace.getSearchRange());
    }

    @Test
    @DisplayName("вимагає параметри фургона, каталог і вантажника")
    void requiresDependencies() {
        List<Coffee> items = List.of();
        VanLoader loader = new VanLoader();

        assertThrows(NullPointerException.class,
                () -> new VanWorkspace(null, items, loader));
        assertThrows(NullPointerException.class,
                () -> new VanWorkspace(CONFIG, null, loader));
        assertThrows(NullPointerException.class,
                () -> new VanWorkspace(CONFIG, items, null));
    }

    @Test
    @DisplayName("переносить товар з каталогу у фургон")
    void loadsFromCatalog() {
        Coffee loaded = workspace.loadFromCatalog(1);

        assertSame(kenya, loaded);
        assertEquals(List.of(kenya), workspace.getVan().getCargo());
        assertEquals(List.of(colombia, gold),
                workspace.getCatalog().getItems());
    }

    @Test
    @DisplayName("залишає товар у каталозі, якщо він не вміщується")
    void keepsItemWhenItDoesNotFit() {
        workspace.loadFromCatalog(0);
        workspace.loadFromCatalog(0);

        assertThrows(CargoLoadingException.class,
                () -> workspace.loadFromCatalog(0));
        assertEquals(List.of(gold), workspace.getCatalog().getItems());
        assertEquals(2, workspace.getVan().size());
    }

    @Test
    @DisplayName("завантажує все, що вміщується, решта лишається в каталозі")
    void loadsAllThatFit() {
        List<Coffee> loaded = workspace.loadAllThatFit();

        assertEquals(List.of(colombia, kenya), loaded);
        assertEquals(List.of(colombia, kenya),
                workspace.getVan().getCargo());
        assertEquals(List.of(gold), workspace.getCatalog().getItems());
    }

    @Test
    @DisplayName("для автоматичного завантаження використовує вантажника")
    void delegatesToLoader() {
        VanLoader loader = mock(VanLoader.class);
        when(loader.load(any(CoffeeVan.class), any())).thenReturn(List.of());
        VanWorkspace mocked = new VanWorkspace(CONFIG, List.of(kenya), loader);

        List<Coffee> loaded = mocked.loadAllThatFit();

        verify(loader).load(mocked.getVan(), List.of(kenya));
        assertEquals(List.of(kenya), loaded);
        assertTrue(mocked.getCatalog().isEmpty());
    }

    @Test
    @DisplayName("повертає товар з фургона в кінець каталогу")
    void unloadsItem() {
        workspace.loadAllThatFit();

        Coffee unloaded = workspace.unload(0);

        assertSame(colombia, unloaded);
        assertEquals(List.of(kenya), workspace.getVan().getCargo());
        assertEquals(List.of(gold, colombia),
                workspace.getCatalog().getItems());
    }

    @Test
    @DisplayName("повертає в каталог увесь вантаж")
    void unloadsAll() {
        workspace.loadAllThatFit();

        List<Coffee> unloaded = workspace.unloadAll();

        assertEquals(List.of(colombia, kenya), unloaded);
        assertTrue(workspace.getVan().isEmpty());
        assertEquals(3, workspace.getCatalog().size());
    }

    @Test
    @DisplayName("новий фургон приймає вантаж, а зайве повертає в каталог")
    void resizesVan() {
        workspace.loadAllThatFit();

        List<Coffee> returned =
                workspace.resizeVan(3.0, new BigDecimal("2000"));

        assertEquals(List.of(kenya), returned);
        assertEquals(List.of(colombia), workspace.getVan().getCargo());
        assertEquals(3.0, workspace.getVan().getCapacityLiters(), DELTA);
        assertEquals(List.of(gold, kenya),
                workspace.getCatalog().getItems());
    }

    @Test
    @DisplayName("не змінює фургон, якщо нові параметри некоректні")
    void keepsVanOnInvalidResize() {
        workspace.loadFromCatalog(0);
        CoffeeVan before = workspace.getVan();
        BigDecimal budget = new BigDecimal("100");

        assertThrows(IllegalArgumentException.class,
                () -> workspace.resizeVan(0, budget));
        assertSame(before, workspace.getVan());
        assertEquals(1, before.size());
    }

    @Test
    @DisplayName("змінює діапазон пошуку")
    void changesSearchRange() {
        QualityRange range = new QualityRange(new Quality(0, 0, 0),
                new Quality(5, 5, 5));

        workspace.setSearchRange(range);

        assertSame(range, workspace.getSearchRange());
        assertThrows(NullPointerException.class,
                () -> workspace.setSearchRange(null));
    }
}
