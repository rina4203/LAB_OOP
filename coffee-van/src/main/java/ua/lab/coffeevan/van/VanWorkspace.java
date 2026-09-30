/*
 * VanWorkspace.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.van;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ua.lab.coffeevan.config.VanConfig;
import ua.lab.coffeevan.model.Coffee;
import ua.lab.coffeevan.model.QualityRange;
import ua.lab.coffeevan.stock.CoffeeCatalog;

/**
 * Робоче місце для ручної роботи з фургоном: каталог товарів,
 * фургон і діапазон якості для пошуку.
 *
 * <p>Товари переміщуються між каталогом і фургоном: завантажений товар
 * зникає з каталогу, а вивантажений - повертається до нього.
 *
 * @author rina4203
 * @version 1.0
 */
public class VanWorkspace {

    private final CoffeeCatalog catalog;
    private final VanLoader loader;
    private CoffeeVan van;
    private QualityRange searchRange;

    /**
     * Створює робоче місце з порожнім фургоном.
     *
     * @param config       параметри фургона та діапазон пошуку
     * @param catalogItems товари каталогу
     * @param loader       вантажник для автоматичного завантаження
     */
    public VanWorkspace(VanConfig config, List<Coffee> catalogItems,
            VanLoader loader) {
        Objects.requireNonNull(config, "Параметри фургона не задано");
        this.catalog = new CoffeeCatalog(catalogItems);
        this.loader = Objects.requireNonNull(loader, "Вантажника не задано");
        this.van = new CoffeeVan(config.getCapacityLiters(),
                config.getBudget());
        this.searchRange = config.getSearchRange();
    }

    /**
     * Переносить товар з каталогу у фургон.
     *
     * @param index позиція товару в каталозі, починаючи з нуля
     * @return завантажений товар
     * @throws ua.lab.coffeevan.exception.CargoLoadingException
     *         якщо товар не вміщується; каталог тоді не змінюється
     */
    public Coffee loadFromCatalog(int index) {
        Coffee coffee = catalog.get(index);
        van.load(coffee);
        catalog.remove(index);
        return coffee;
    }

    /**
     * Завантажує у фургон усі товари каталогу, що вміщуються,
     * у порядку каталогу. Решта товарів залишається в каталозі.
     *
     * @return завантажені товари
     */
    public List<Coffee> loadAllThatFit() {
        List<Coffee> candidates = catalog.removeAll();
        List<Coffee> rejected = loader.load(van, candidates);
        rejected.forEach(catalog::add);
        List<Coffee> loaded = new ArrayList<>(candidates);
        loaded.removeAll(rejected);
        return loaded;
    }

    /**
     * Повертає товар з фургона в каталог.
     *
     * @param index позиція товару у фургоні, починаючи з нуля
     * @return вивантажений товар
     */
    public Coffee unload(int index) {
        Coffee coffee = van.remove(index);
        catalog.add(coffee);
        return coffee;
    }

    /**
     * Повертає в каталог увесь вантаж фургона.
     *
     * @return вивантажені товари
     */
    public List<Coffee> unloadAll() {
        List<Coffee> unloaded = van.removeAll();
        unloaded.forEach(catalog::add);
        return unloaded;
    }

    /**
     * Замінює фургон новим з іншим об'ємом і бюджетом. Вантаж
     * переноситься в новий фургон; те, що не вміщується, повертається
     * в каталог.
     *
     * @param capacityLiters новий вантажний об'єм, л
     * @param budget         новий бюджет, грн
     * @return товари, повернуті в каталог
     * @throws IllegalArgumentException якщо об'єм чи бюджет не додатні;
     *                                  фургон тоді не змінюється
     */
    public List<Coffee> resizeVan(double capacityLiters, BigDecimal budget) {
        CoffeeVan resized = new CoffeeVan(capacityLiters, budget);
        List<Coffee> rejected = loader.load(resized, van.getCargo());
        rejected.forEach(catalog::add);
        van = resized;
        return rejected;
    }

    /**
     * Повертає каталог товарів.
     *
     * @return каталог
     */
    public CoffeeCatalog getCatalog() {
        return catalog;
    }

    /**
     * Повертає фургон.
     *
     * @return фургон
     */
    public CoffeeVan getVan() {
        return van;
    }

    /**
     * Повертає поточний діапазон якості для пошуку.
     *
     * @return діапазон параметрів якості
     */
    public QualityRange getSearchRange() {
        return searchRange;
    }

    /**
     * Змінює діапазон якості для пошуку.
     *
     * @param searchRange новий діапазон параметрів якості
     */
    public void setSearchRange(QualityRange searchRange) {
        this.searchRange = Objects.requireNonNull(searchRange,
                "Діапазон якості не задано");
    }
}
