/*
 * Coffee.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Set;

/**
 * Одиниця кавового товару - упакована кава певного сорту.
 *
 * <p>Базовий клас для кави в різних фізичних станах (зерно, мелена,
 * розчинна). Підкласи визначають насипну густину кави та перелік
 * допустимих упаковок, а базовий клас на цій основі обчислює об'єм
 * товару разом з упаковкою та співвідношення ціни й ваги.
 *
 * @author rina4203
 * @version 1.0
 */
public abstract class Coffee {

    private static final int GRAMS_PER_KILOGRAM = 1000;
    private static final double CUBIC_CM_PER_LITER = 1000.0;
    private static final int MONEY_SCALE = 2;

    private final String name;
    private final CoffeeVariety variety;
    private final int netWeightGrams;
    private final BigDecimal price;
    private final PackagingType packaging;
    private final Quality quality;

    /**
     * Ініціалізує спільні характеристики кавового товару.
     *
     * @param name           торгова назва товару
     * @param variety        сорт кави
     * @param netWeightGrams вага кави без упаковки, г
     * @param price          ціна за одиницю товару, грн
     * @param packaging      тип упаковки
     * @param quality        параметри якості
     * @throws IllegalArgumentException якщо назва порожня, вага чи ціна
     *                                  не додатні
     * @throws NullPointerException     якщо сорт, упаковку чи якість
     *                                  не задано
     */
    protected Coffee(String name, CoffeeVariety variety, int netWeightGrams,
            BigDecimal price, PackagingType packaging, Quality quality) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Назва кави не може бути порожньою");
        }
        if (netWeightGrams <= 0) {
            throw new IllegalArgumentException(
                    "Вага кави має бути додатною, отримано " + netWeightGrams);
        }
        if (price == null || price.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Ціна кави має бути додатною, отримано " + price);
        }
        this.name = name.trim();
        this.variety = Objects.requireNonNull(variety, "Сорт кави не задано");
        this.netWeightGrams = netWeightGrams;
        this.price = price.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        this.packaging = Objects.requireNonNull(packaging,
                "Упаковку кави не задано");
        this.quality = Objects.requireNonNull(quality,
                "Параметри якості кави не задано");
    }

    /**
     * Повертає назву фізичного стану кави.
     *
     * @return фізичний стан, наприклад "у зернах"
     */
    public abstract String getPhysicalState();

    /**
     * Повертає характеристику, специфічну для фізичного стану кави
     * (ступінь обсмаження, помелу чи технологію виробництва).
     *
     * @return опис особливості стану
     */
    public abstract String getStateDetails();

    /**
     * Повертає насипну густину кави у поточному фізичному стані.
     *
     * @return насипна густина, г/см³
     */
    public abstract double getBulkDensity();

    /**
     * Обчислює об'єм самої кави без упаковки.
     *
     * @return об'єм кави, л
     */
    public double getCoffeeVolumeLiters() {
        return netWeightGrams / getBulkDensity() / CUBIC_CM_PER_LITER;
    }

    /**
     * Обчислює об'єм, який товар займає у фургоні разом з упаковкою.
     *
     * @return об'єм упакованого товару, л
     */
    public double getVolumeLiters() {
        return packaging.calculatePackedVolume(getCoffeeVolumeLiters());
    }

    /**
     * Обчислює співвідношення ціни та ваги - ціну одного кілограма кави.
     *
     * @return ціна за кілограм, грн
     */
    public BigDecimal getPricePerKilogram() {
        return price.multiply(BigDecimal.valueOf(GRAMS_PER_KILOGRAM))
                .divide(BigDecimal.valueOf(netWeightGrams), MONEY_SCALE,
                        RoundingMode.HALF_UP);
    }

    /**
     * Повертає торгову назву товару.
     *
     * @return назва товару
     */
    public String getName() {
        return name;
    }

    /**
     * Повертає сорт кави.
     *
     * @return сорт кави
     */
    public CoffeeVariety getVariety() {
        return variety;
    }

    /**
     * Повертає вагу кави без упаковки.
     *
     * @return вага нетто, г
     */
    public int getNetWeightGrams() {
        return netWeightGrams;
    }

    /**
     * Повертає ціну одиниці товару.
     *
     * @return ціна, грн
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Повертає тип упаковки.
     *
     * @return тип упаковки
     */
    public PackagingType getPackaging() {
        return packaging;
    }

    /**
     * Повертає параметри якості кави.
     *
     * @return параметри якості
     */
    public Quality getQuality() {
        return quality;
    }

    @Override
    public String toString() {
        return name + " (" + variety.getDisplayName() + ", "
                + getPhysicalState() + ", " + getStateDetails() + ", "
                + packaging.getDisplayName() + ", " + netWeightGrams + " г, "
                + price.toPlainString() + " грн)";
    }

    /**
     * Перевіряє, що кава у своєму фізичному стані постачається
     * в обраній упаковці. Викликається конструкторами підкласів.
     *
     * @param allowedPackaging упаковки, допустимі для фізичного стану
     * @throws IllegalArgumentException якщо упаковка недопустима
     */
    protected final void requirePackaging(
            Set<PackagingType> allowedPackaging) {
        if (!allowedPackaging.contains(packaging)) {
            throw new IllegalArgumentException(String.format(
                    "Кава \"%s\" (%s) не постачається в упаковці \"%s\"",
                    name, getPhysicalState(), packaging.getDisplayName()));
        }
    }
}
