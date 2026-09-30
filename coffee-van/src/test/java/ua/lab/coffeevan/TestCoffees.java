/*
 * TestCoffees.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan;

import java.math.BigDecimal;

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

/**
 * Готові товари для тестів з наперед відомими об'ємом і ціною.
 */
public final class TestCoffees {

    private TestCoffees() {
    }

    /**
     * Кава у зернах: 1000 г, середнє обсмаження, вакуум - 2,75 л;
     * якість 8/6/7.
     *
     * @param name  назва
     * @param price ціна, грн (вона ж ціна кілограма)
     * @return товар
     */
    public static Coffee beans(String name, String price) {
        return new CoffeeBeans(name, CoffeeVariety.ARABICA, 1000,
                new BigDecimal(price), PackagingType.VACUUM_PACK,
                new Quality(8, 6, 7), RoastLevel.MEDIUM);
    }

    /**
     * Мелена кава: 250 г, середній помел, паперовий пакет - 0,78 л;
     * якість 9/9/6.
     *
     * @param name  назва
     * @param price ціна, грн
     * @return товар
     */
    public static Coffee ground(String name, String price) {
        return new GroundCoffee(name, CoffeeVariety.ARABICA, 250,
                new BigDecimal(price), PackagingType.PAPER_BAG,
                new Quality(9, 9, 6), GrindSize.MEDIUM);
    }

    /**
     * Розчинна кава: 200 г, сублімована, скляна банка - 1,45 л;
     * якість 6/4/5.
     *
     * @param name  назва
     * @param price ціна, грн
     * @return товар
     */
    public static Coffee instant(String name, String price) {
        return new InstantCoffee(name, CoffeeVariety.ROBUSTA, 200,
                new BigDecimal(price), PackagingType.GLASS_JAR,
                new Quality(6, 4, 5), InstantProcess.FREEZE_DRIED);
    }
}
