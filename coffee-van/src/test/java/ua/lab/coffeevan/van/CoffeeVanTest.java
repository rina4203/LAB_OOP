/*
 * CoffeeVanTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.van;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ua.lab.coffeevan.exception.CargoLoadingException;
import ua.lab.coffeevan.model.Coffee;
import ua.lab.coffeevan.model.Quality;
import ua.lab.coffeevan.model.QualityRange;

@DisplayName("Фургон кави")
class CoffeeVanTest {

    private static final double DELTA = 1e-9;
    private static final double CAPACITY = 10.0;
    private static final BigDecimal BUDGET = new BigDecimal("1000.00");

    private final CoffeeVan van = new CoffeeVan(CAPACITY, BUDGET);

    @Test
    @DisplayName("новий фургон порожній")
    void newVanIsEmpty() {
        assertTrue(van.getCargo().isEmpty());
        assertEquals(CAPACITY, van.getCapacityLiters(), DELTA);
        assertEquals(BUDGET, van.getBudget());
        assertEquals(0.0, van.getLoadedVolumeLiters(), DELTA);
        assertEquals(CAPACITY, van.getFreeVolumeLiters(), DELTA);
        assertEquals(0, BigDecimal.ZERO.compareTo(van.getCargoCost()));
        assertEquals(BUDGET, van.getRemainingBudget());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -5.0, Double.NaN,
            Double.POSITIVE_INFINITY})
    @DisplayName("відхиляє некоректний об'єм")
    void rejectsInvalidCapacity(double capacity) {
        assertThrows(IllegalArgumentException.class,
                () -> new CoffeeVan(capacity, BUDGET));
    }

    @Test
    @DisplayName("відхиляє відсутній чи недодатний бюджет")
    void rejectsInvalidBudget() {
        assertThrows(IllegalArgumentException.class,
                () -> new CoffeeVan(CAPACITY, null));
        assertThrows(IllegalArgumentException.class,
                () -> new CoffeeVan(CAPACITY, BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> new CoffeeVan(CAPACITY, new BigDecimal("-1")));
    }

    @Test
    @DisplayName("завантажує товар і враховує його об'єм та вартість")
    void loadsCoffee() {
        Coffee first = coffee("First", 2.5, "300");
        Coffee second = coffee("Second", 1.5, "200");

        van.load(first);
        van.load(second);

        assertEquals(List.of(first, second), van.getCargo());
        assertEquals(4.0, van.getLoadedVolumeLiters(), DELTA);
        assertEquals(6.0, van.getFreeVolumeLiters(), DELTA);
        assertEquals(new BigDecimal("500"), van.getCargoCost());
        assertEquals(new BigDecimal("500.00"), van.getRemainingBudget());
    }

    @Test
    @DisplayName("дозволяє заповнити фургон повністю")
    void allowsExactFit() {
        Coffee coffee = coffee("Exact", CAPACITY, "1000");

        assertTrue(van.canLoad(coffee));
        van.load(coffee);

        assertEquals(0.0, van.getFreeVolumeLiters(), DELTA);
        assertEquals(0, BigDecimal.ZERO.compareTo(van.getRemainingBudget()));
    }

    @Test
    @DisplayName("не завантажує товар, для якого бракує місця")
    void rejectsCoffeeExceedingVolume() {
        van.load(coffee("Big", 9.0, "100"));
        Coffee tooBig = coffee("Too big", 1.5, "100");

        assertFalse(van.canLoad(tooBig));
        CargoLoadingException e = assertThrows(CargoLoadingException.class,
                () -> van.load(tooBig));
        assertTrue(e.getMessage().contains("Недостатньо місця"));
        assertEquals(1, van.getCargo().size());
    }

    @Test
    @DisplayName("не завантажує товар, для якого бракує коштів")
    void rejectsCoffeeExceedingBudget() {
        van.load(coffee("Expensive", 1.0, "900"));
        Coffee tooExpensive = coffee("Too expensive", 1.0, "100.01");

        assertFalse(van.canLoad(tooExpensive));
        CargoLoadingException e = assertThrows(CargoLoadingException.class,
                () -> van.load(tooExpensive));
        assertTrue(e.getMessage().contains("Недостатньо коштів"));
        assertEquals(1, van.getCargo().size());
    }

    @Test
    @DisplayName("не приймає порожній товар")
    void rejectsNullCoffee() {
        assertThrows(NullPointerException.class, () -> van.load(null));
    }

    @Test
    @DisplayName("не дозволяє змінювати вантаж в обхід фургона")
    void cargoIsUnmodifiable() {
        List<Coffee> cargo = van.getCargo();
        Coffee coffee = coffee("Sneaky", 1.0, "1");

        assertThrows(UnsupportedOperationException.class,
                () -> cargo.add(coffee));
    }

    @Test
    @DisplayName("сортує вантаж за зростанням ціни кілограма")
    void sortsCargoByPricePerKilogram() {
        Coffee expensive = coffeeWithPricePerKilogram("1860");
        Coffee cheap = coffeeWithPricePerKilogram("560");
        Coffee middle = coffeeWithPricePerKilogram("1150");
        van.load(expensive);
        van.load(cheap);
        van.load(middle);

        van.sortCargoByPricePerKilogram();

        assertEquals(List.of(cheap, middle, expensive), van.getCargo());
    }

    @Test
    @DisplayName("знаходить товари із заданим діапазоном якості")
    void findsCoffeeByQuality() {
        Coffee aromatic = coffeeWithQuality(new Quality(9, 8, 5));
        Coffee bitter = coffeeWithQuality(new Quality(4, 2, 9));
        Coffee balanced = coffeeWithQuality(new Quality(8, 6, 7));
        van.load(aromatic);
        van.load(bitter);
        van.load(balanced);
        QualityRange range = new QualityRange(new Quality(7, 5, 5),
                new Quality(10, 9, 8));

        assertEquals(List.of(aromatic, balanced), van.findByQuality(range));
    }

    @Test
    @DisplayName("повертає порожній список, якщо нічого не знайдено")
    void findsNothingInEmptyVan() {
        QualityRange range = new QualityRange(new Quality(0, 0, 0),
                new Quality(10, 10, 10));

        assertTrue(van.findByQuality(range).isEmpty());
    }

    @Test
    @DisplayName("вимагає діапазон якості для пошуку")
    void rejectsNullRange() {
        assertThrows(NullPointerException.class,
                () -> van.findByQuality(null));
    }

    @Test
    @DisplayName("у повідомленнях називається \"фургон\"")
    void hasDisplayName() {
        assertEquals("фургон", van.getDisplayName());
        IndexOutOfBoundsException e = assertThrows(
                IndexOutOfBoundsException.class, () -> van.get(0));
        assertTrue(e.getMessage().contains("фургон"));
    }

    @Test
    @DisplayName("під час заміни враховує місце й гроші старого товару")
    void replacesWithinReleasedLimits() {
        van.load(coffee("Other", 2.0, "200"));
        van.load(coffee("Old", 8.0, "800"));
        Coffee bigger = coffee("New", 8.0, "800");

        Coffee replaced = van.replace(1, bigger);

        assertEquals("Old", replaced.getName());
        assertEquals(List.of("Other", "New"), names(van));
        assertEquals(0.0, van.getFreeVolumeLiters(), DELTA);
    }

    @Test
    @DisplayName("не замінює товар, якому бракує місця")
    void rejectsReplacementExceedingVolume() {
        van.load(coffee("Other", 2.0, "100"));
        Coffee old = coffee("Old", 3.0, "100");
        van.load(old);

        CargoLoadingException e = assertThrows(CargoLoadingException.class,
                () -> van.replace(1, coffee("Huge", 8.5, "100")));

        assertTrue(e.getMessage().contains("вільно 8.00 л"));
        assertEquals(List.of("Other", "Old"), names(van));
    }

    @Test
    @DisplayName("не замінює товар, якому бракує коштів")
    void rejectsReplacementExceedingBudget() {
        van.load(coffee("Other", 1.0, "600"));
        van.load(coffee("Old", 1.0, "300"));

        CargoLoadingException e = assertThrows(CargoLoadingException.class,
                () -> van.replace(1, coffee("Pricey", 1.0, "400.01")));

        assertTrue(e.getMessage().contains("залишок 400.00 грн"));
        assertEquals(List.of("Other", "Old"), names(van));
    }

    @Test
    @DisplayName("після видалення товару звільняє місце й гроші")
    void releasesSpaceOnRemove() {
        Coffee first = coffee("First", 4.0, "400");
        van.load(first);
        van.load(coffee("Second", 1.0, "100"));

        Coffee removed = van.remove(0);

        assertEquals(first, removed);
        assertEquals(9.0, van.getFreeVolumeLiters(), DELTA);
        assertEquals(new BigDecimal("900.00"), van.getRemainingBudget());
        assertEquals(1, van.size());
    }

    @Test
    @DisplayName("вивантажує все й стає порожнім")
    void removesAllCargo() {
        van.load(coffee("First", 4.0, "400"));
        van.load(coffee("Second", 1.0, "100"));

        List<Coffee> removed = van.removeAll();

        assertEquals(2, removed.size());
        assertTrue(van.isEmpty());
        assertEquals(CAPACITY, van.getFreeVolumeLiters(), DELTA);
    }

    private static List<String> names(CoffeeVan van) {
        return van.getCargo().stream()
                .map(Coffee::getName)
                .collect(Collectors.toList());
    }

    private static Coffee coffee(String name, double volume, String price) {
        Coffee coffee = mock(Coffee.class);
        when(coffee.getName()).thenReturn(name);
        when(coffee.getVolumeLiters()).thenReturn(volume);
        when(coffee.getPrice()).thenReturn(new BigDecimal(price));
        return coffee;
    }

    private static Coffee coffeeWithPricePerKilogram(String pricePerKg) {
        Coffee coffee = coffee("Coffee", 1.0, "10");
        when(coffee.getPricePerKilogram())
                .thenReturn(new BigDecimal(pricePerKg));
        return coffee;
    }

    private static Coffee coffeeWithQuality(Quality quality) {
        Coffee coffee = coffee("Coffee", 1.0, "10");
        when(coffee.getQuality()).thenReturn(quality);
        return coffee;
    }
}
