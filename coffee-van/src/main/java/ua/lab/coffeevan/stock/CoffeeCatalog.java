/*
 * CoffeeCatalog.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.stock;

import java.util.List;
import java.util.Objects;

import ua.lab.coffeevan.model.Coffee;

/**
 * Каталог - товари, доступні для завантаження у фургон.
 *
 * <p>На відміну від фургона, каталог не обмежує ні кількість,
 * ні об'єм, ні вартість товарів.
 *
 * @author rina4203
 * @version 1.0
 */
public final class CoffeeCatalog extends CoffeeCollection {

    /**
     * Створює порожній каталог.
     */
    public CoffeeCatalog() {
        super();
    }

    /**
     * Створює каталог із заданими товарами.
     *
     * @param coffees товари каталогу
     */
    public CoffeeCatalog(List<Coffee> coffees) {
        Objects.requireNonNull(coffees, "Товари каталогу не задано");
        coffees.forEach(this::add);
    }

    @Override
    public String getDisplayName() {
        return "каталог";
    }
}
