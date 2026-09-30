/*
 * CoffeeCollectionTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.stock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ua.lab.coffeevan.TestCoffees;
import ua.lab.coffeevan.model.Coffee;
import ua.lab.coffeevan.model.Quality;
import ua.lab.coffeevan.model.QualityRange;

@DisplayName("Список товарів (на прикладі каталогу)")
class CoffeeCollectionTest {

    private final Coffee colombia = TestCoffees.beans("Colombia Supremo",
            "1150");
    private final Coffee kenya = TestCoffees.ground("Kenya AA", "420");
    private final Coffee gold = TestCoffees.instant("Gold Arabica", "340");
    private final CoffeeCatalog catalog =
            new CoffeeCatalog(List.of(colombia, kenya, gold));

    @Test
    @DisplayName("новий каталог порожній і називається \"каталог\"")
    void newCatalogIsEmpty() {
        CoffeeCatalog empty = new CoffeeCatalog();

        assertTrue(empty.isEmpty());
        assertEquals(0, empty.size());
        assertEquals("каталог", empty.getDisplayName());
    }

    @Test
    @DisplayName("зберігає товари в заданому порядку")
    void keepsOrder() {
        assertEquals(List.of(colombia, kenya, gold), catalog.getItems());
        assertEquals(3, catalog.size());
        assertFalse(catalog.isEmpty());
        assertSame(kenya, catalog.get(1));
    }

    @Test
    @DisplayName("вимагає список товарів для каталогу")
    void requiresItems() {
        assertThrows(NullPointerException.class,
                () -> new CoffeeCatalog(null));
    }

    @Test
    @DisplayName("додає товар у кінець")
    void addsToEnd() {
        Coffee extra = TestCoffees.ground("Extra", "100");

        catalog.add(extra);

        assertSame(extra, catalog.get(3));
        assertEquals(3, catalog.indexOf(extra));
    }

    @Test
    @DisplayName("не додає порожній товар")
    void rejectsNullOnAdd() {
        assertThrows(NullPointerException.class, () -> catalog.add(null));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 3})
    @DisplayName("пояснює, що товару з таким номером немає")
    void reportsMissingPosition(int index) {
        IndexOutOfBoundsException e = assertThrows(
                IndexOutOfBoundsException.class, () -> catalog.get(index));

        assertTrue(e.getMessage().contains("каталог"));
        assertTrue(e.getMessage().contains("№" + (index + 1)));
        assertTrue(e.getMessage().contains("усього товарів: 3"));
    }

    @Test
    @DisplayName("замінює товар і повертає попередній")
    void replacesItem() {
        Coffee replacement = TestCoffees.ground("Kenya AA Top", "500");

        Coffee previous = catalog.replace(1, replacement);

        assertSame(kenya, previous);
        assertSame(replacement, catalog.get(1));
        assertEquals(3, catalog.size());
    }

    @Test
    @DisplayName("не замінює на порожній товар чи неіснуючу позицію")
    void rejectsInvalidReplace() {
        assertThrows(NullPointerException.class,
                () -> catalog.replace(0, null));
        assertThrows(IndexOutOfBoundsException.class,
                () -> catalog.replace(5, kenya));
    }

    @Test
    @DisplayName("видаляє товар за позицією")
    void removesItem() {
        Coffee removed = catalog.remove(0);

        assertSame(colombia, removed);
        assertEquals(List.of(kenya, gold), catalog.getItems());
        assertThrows(IndexOutOfBoundsException.class,
                () -> catalog.remove(2));
    }

    @Test
    @DisplayName("видаляє всі товари й повертає їх")
    void removesAll() {
        List<Coffee> removed = catalog.removeAll();

        assertEquals(List.of(colombia, kenya, gold), removed);
        assertTrue(catalog.isEmpty());
    }

    @Test
    @DisplayName("повертає -1 для товару, якого немає")
    void indexOfMissing() {
        assertEquals(-1, catalog.indexOf(TestCoffees.beans("Other", "1")));
    }

    @Test
    @DisplayName("не дозволяє змінювати товари в обхід списку")
    void itemsAreUnmodifiable() {
        List<Coffee> items = catalog.getItems();

        assertThrows(UnsupportedOperationException.class, items::clear);
    }

    @Test
    @DisplayName("сортує в заданому порядку")
    void sorts() {
        catalog.sort(Coffee.BY_PRICE_PER_KILOGRAM.reversed());

        assertEquals(List.of(gold, kenya, colombia), catalog.getItems());
        assertThrows(NullPointerException.class, () -> catalog.sort(null));
    }

    @Test
    @DisplayName("сортує за назвою, якщо так задано")
    void sortsByName() {
        catalog.sort(Comparator.comparing(Coffee::getName));

        assertEquals(List.of(colombia, gold, kenya), catalog.getItems());
    }

    @Test
    @DisplayName("знаходить товари за частиною назви без урахування регістру")
    void findsByName() {
        assertEquals(List.of(kenya), catalog.findByName("  KENYA "));
        assertEquals(List.of(colombia, gold), catalog.findByName("o"));
        assertTrue(catalog.findByName("Ефіопія").isEmpty());
        assertThrows(NullPointerException.class,
                () -> catalog.findByName(null));
    }

    @Test
    @DisplayName("знаходить товари за діапазоном якості")
    void findsByQuality() {
        QualityRange aromatic = new QualityRange(new Quality(8, 0, 0),
                new Quality(10, 10, 10));

        assertEquals(List.of(colombia, kenya),
                catalog.findByQuality(aromatic));
        assertThrows(NullPointerException.class,
                () -> catalog.findByQuality(null));
    }
}
