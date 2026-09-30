/*
 * CoffeeForm.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.io;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ua.lab.coffeevan.model.Coffee;
import ua.lab.coffeevan.model.CoffeeBeans;
import ua.lab.coffeevan.model.CoffeeVariety;
import ua.lab.coffeevan.model.DisplayNamed;
import ua.lab.coffeevan.model.GrindSize;
import ua.lab.coffeevan.model.GroundCoffee;
import ua.lab.coffeevan.model.InstantCoffee;
import ua.lab.coffeevan.model.InstantProcess;
import ua.lab.coffeevan.model.PackagingType;
import ua.lab.coffeevan.model.Quality;
import ua.lab.coffeevan.model.RoastLevel;

/**
 * Фізичний стан кави так, як він записується в каталозі
 * ({@code BEANS}, {@code GROUND}, {@code INSTANT}).
 *
 * <p>Кожен стан знає свій клас моделі: створює об'єкт потрібного
 * підкласу {@link Coffee} і повертає код його особливості (обсмаження,
 * помелу чи технології). Так читання й запис каталогу обходяться без
 * розгалужень за типом кави.
 *
 * @author rina4203
 * @version 1.0
 */
public enum CoffeeForm implements DisplayNamed {

    /** Кава у зернах; особливість - ступінь обсмаження. */
    BEANS("у зернах", "обсмаження", CoffeeBeans.ALLOWED_PACKAGING,
            RoastLevel.values()) {
        @Override
        public Coffee create(String name, CoffeeVariety variety, int weight,
                BigDecimal price, PackagingType packaging, Quality quality,
                String feature) {
            return new CoffeeBeans(name, variety, weight, price, packaging,
                    quality, ValueParser.parseEnum(RoastLevel.class, feature,
                            getFeatureLabel()));
        }

        @Override
        public boolean describes(Coffee coffee) {
            return coffee instanceof CoffeeBeans;
        }

        @Override
        public String getFeature(Coffee coffee) {
            return ((CoffeeBeans) coffee).getRoastLevel().name();
        }
    },

    /** Мелена кава; особливість - ступінь помелу. */
    GROUND("мелена", "помел", GroundCoffee.ALLOWED_PACKAGING,
            GrindSize.values()) {
        @Override
        public Coffee create(String name, CoffeeVariety variety, int weight,
                BigDecimal price, PackagingType packaging, Quality quality,
                String feature) {
            return new GroundCoffee(name, variety, weight, price, packaging,
                    quality, ValueParser.parseEnum(GrindSize.class, feature,
                            getFeatureLabel()));
        }

        @Override
        public boolean describes(Coffee coffee) {
            return coffee instanceof GroundCoffee;
        }

        @Override
        public String getFeature(Coffee coffee) {
            return ((GroundCoffee) coffee).getGrindSize().name();
        }
    },

    /** Розчинна кава; особливість - технологія виробництва. */
    INSTANT("розчинна", "технологія", InstantCoffee.ALLOWED_PACKAGING,
            InstantProcess.values()) {
        @Override
        public Coffee create(String name, CoffeeVariety variety, int weight,
                BigDecimal price, PackagingType packaging, Quality quality,
                String feature) {
            return new InstantCoffee(name, variety, weight, price, packaging,
                    quality, ValueParser.parseEnum(InstantProcess.class,
                            feature, getFeatureLabel()));
        }

        @Override
        public boolean describes(Coffee coffee) {
            return coffee instanceof InstantCoffee;
        }

        @Override
        public String getFeature(Coffee coffee) {
            return ((InstantCoffee) coffee).getProcess().name();
        }
    };

    private final String displayName;
    private final String featureLabel;
    private final List<PackagingType> allowedPackaging;
    private final List<DisplayNamed> featureValues;

    CoffeeForm(String displayName, String featureLabel,
            Set<PackagingType> allowedPackaging, DisplayNamed[] features) {
        this.displayName = displayName;
        this.featureLabel = featureLabel;
        this.allowedPackaging = allowedPackaging.stream()
                .sorted()
                .collect(Collectors.toUnmodifiableList());
        this.featureValues = List.of(features);
    }

    /**
     * Створює каву в цьому фізичному стані.
     *
     * @param name      торгова назва
     * @param variety   сорт
     * @param weight    вага без упаковки, г
     * @param price     ціна, грн
     * @param packaging упаковка
     * @param quality   параметри якості
     * @param feature   код особливості стану, наприклад {@code "DARK"}
     * @return створений товар
     * @throws IllegalArgumentException якщо дані некоректні
     */
    public abstract Coffee create(String name, CoffeeVariety variety,
            int weight, BigDecimal price, PackagingType packaging,
            Quality quality, String feature);

    /**
     * Перевіряє, чи товар перебуває в цьому фізичному стані.
     *
     * @param coffee товар
     * @return {@code true}, якщо товар має цей стан
     */
    public abstract boolean describes(Coffee coffee);

    /**
     * Повертає код особливості стану товару.
     *
     * @param coffee товар у цьому фізичному стані
     * @return код, наприклад {@code "MEDIUM"}
     */
    public abstract String getFeature(Coffee coffee);

    /**
     * Визначає фізичний стан товару.
     *
     * @param coffee товар
     * @return фізичний стан
     * @throws IllegalArgumentException якщо стан товару невідомий
     */
    public static CoffeeForm of(Coffee coffee) {
        for (CoffeeForm form : values()) {
            if (form.describes(coffee)) {
                return form;
            }
        }
        throw new IllegalArgumentException("Невідомий фізичний стан кави: "
                + coffee.getClass().getSimpleName());
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Повертає назву особливості стану.
     *
     * @return назва особливості, наприклад "обсмаження"
     */
    public String getFeatureLabel() {
        return featureLabel;
    }

    /**
     * Повертає упаковки, в яких постачається кава в цьому стані.
     *
     * @return упаковки в порядку оголошення
     */
    public List<PackagingType> getAllowedPackaging() {
        return allowedPackaging;
    }

    /**
     * Повертає можливі значення особливості стану.
     *
     * @return значення особливості
     */
    public List<DisplayNamed> getFeatureValues() {
        return featureValues;
    }
}
