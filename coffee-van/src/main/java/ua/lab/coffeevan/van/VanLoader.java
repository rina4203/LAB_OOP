/*
 * VanLoader.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.van;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ua.lab.coffeevan.model.Coffee;

/**
 * Вантажник, що заповнює фургон товарами з каталогу.
 *
 * <p>Товари завантажуються у порядку їх переліку в каталозі. Якщо товар
 * не вміщується за об'ємом або бюджетом, він пропускається,
 * а завантаження продовжується з наступного товару - так фургон
 * заповнюється якомога щільніше.
 *
 * @author rina4203
 * @version 1.0
 */
public class VanLoader {

    /**
     * Завантажує товари з каталогу у фургон.
     *
     * @param van     фургон
     * @param catalog товари, доступні для завантаження
     * @return товари, які не вдалося завантажити
     * @throws NullPointerException якщо фургон чи каталог не задано
     */
    public List<Coffee> load(CoffeeVan van, List<Coffee> catalog) {
        Objects.requireNonNull(van, "Фургон не задано");
        Objects.requireNonNull(catalog, "Каталог не задано");
        List<Coffee> rejected = new ArrayList<>();
        for (Coffee coffee : catalog) {
            if (van.canLoad(coffee)) {
                van.load(coffee);
            } else {
                rejected.add(coffee);
            }
        }
        return rejected;
    }
}
