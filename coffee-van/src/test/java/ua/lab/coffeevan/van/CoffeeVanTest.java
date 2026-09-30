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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ua.lab.coffeevan.exception.CargoLoadingException;
import ua.lab.coffeevan.model.Coffee;

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

    private static Coffee coffee(String name, double volume, String price) {
        Coffee coffee = mock(Coffee.class);
        when(coffee.getName()).thenReturn(name);
        when(coffee.getVolumeLiters()).thenReturn(volume);
        when(coffee.getPrice()).thenReturn(new BigDecimal(price));
        return coffee;
    }
}
