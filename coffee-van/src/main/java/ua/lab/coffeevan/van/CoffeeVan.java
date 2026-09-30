/*
 * CoffeeVan.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.van;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import ua.lab.coffeevan.exception.CargoLoadingException;
import ua.lab.coffeevan.model.Coffee;
import ua.lab.coffeevan.stock.CoffeeCollection;

/**
 * Фургон для перевезення кави.
 *
 * <p>Має обмежений вантажний об'єм і бюджет на закупівлю товару.
 * Фургон не дозволяє завантажити товар, якщо той не вміщується
 * за об'ємом (з урахуванням упаковки) або коштує більше, ніж залишок
 * бюджету. Ті самі обмеження діють і під час заміни товару у вантажі.
 * Вантаж можна відсортувати за ціною кілограма кави та відібрати товари
 * з заданими параметрами якості.
 *
 * @author rina4203
 * @version 1.0
 */
public class CoffeeVan extends CoffeeCollection {

    /** Допустима похибка при порівнянні об'ємів, л. */
    private static final double VOLUME_TOLERANCE = 1e-9;

    private final double capacityLiters;
    private final BigDecimal budget;

    /**
     * Створює порожній фургон.
     *
     * @param capacityLiters вантажний об'єм фургона, л
     * @param budget         сума, на яку можна закупити товар, грн
     * @throws IllegalArgumentException якщо об'єм чи бюджет не додатні
     */
    public CoffeeVan(double capacityLiters, BigDecimal budget) {
        if (!Double.isFinite(capacityLiters) || capacityLiters <= 0) {
            throw new IllegalArgumentException(
                    "Об'єм фургона має бути додатним, отримано "
                            + capacityLiters);
        }
        if (budget == null || budget.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Бюджет має бути додатним, отримано " + budget);
        }
        this.capacityLiters = capacityLiters;
        this.budget = budget;
    }

    @Override
    public String getDisplayName() {
        return "фургон";
    }

    /**
     * Перевіряє, чи можна завантажити товар у фургон.
     *
     * @param coffee товар
     * @return {@code true}, якщо вистачає і вільного об'єму,
     *         і залишку бюджету
     */
    public boolean canLoad(Coffee coffee) {
        return fitsVolume(coffee, null) && fitsBudget(coffee, null);
    }

    /**
     * Завантажує товар у фургон.
     *
     * @param coffee товар
     * @throws NullPointerException  якщо товар не задано
     * @throws CargoLoadingException якщо товар не вміщується за об'ємом
     *                               або бюджетом
     */
    public void load(Coffee coffee) {
        add(coffee);
    }

    /**
     * Сортує вантаж за зростанням співвідношення ціни та ваги,
     * тобто ціни одного кілограма кави.
     */
    public void sortCargoByPricePerKilogram() {
        sort(Coffee.BY_PRICE_PER_KILOGRAM);
    }

    /**
     * Повертає вантаж фургона.
     *
     * @return незмінний список завантажених товарів
     */
    public List<Coffee> getCargo() {
        return getItems();
    }

    /**
     * Повертає вантажний об'єм фургона.
     *
     * @return об'єм, л
     */
    public double getCapacityLiters() {
        return capacityLiters;
    }

    /**
     * Повертає бюджет на закупівлю товару.
     *
     * @return бюджет, грн
     */
    public BigDecimal getBudget() {
        return budget;
    }

    /**
     * Обчислює об'єм, зайнятий товарами разом з упаковкою.
     *
     * @return зайнятий об'єм, л
     */
    public double getLoadedVolumeLiters() {
        return getItems().stream()
                .mapToDouble(Coffee::getVolumeLiters)
                .sum();
    }

    /**
     * Обчислює вільний об'єм фургона.
     *
     * @return вільний об'єм, л
     */
    public double getFreeVolumeLiters() {
        return capacityLiters - getLoadedVolumeLiters();
    }

    /**
     * Обчислює загальну вартість вантажу.
     *
     * @return вартість вантажу, грн
     */
    public BigDecimal getCargoCost() {
        return getItems().stream()
                .map(Coffee::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Обчислює залишок бюджету після закупівлі вантажу.
     *
     * @return залишок бюджету, грн
     */
    public BigDecimal getRemainingBudget() {
        return budget.subtract(getCargoCost());
    }

    /**
     * Не дозволяє товару потрапити у фургон, якщо йому бракує
     * вільного об'єму чи залишку бюджету. Під час заміни враховуються
     * об'єм і вартість товару, що замінюється.
     *
     * @param coffee   товар, що додається
     * @param replaced товар, який він замінює, або {@code null}
     * @throws CargoLoadingException якщо товар не вміщується
     */
    @Override
    protected void checkCanAdd(Coffee coffee, Coffee replaced) {
        if (!fitsVolume(coffee, replaced)) {
            throw new CargoLoadingException(String.format(Locale.ROOT,
                    "Недостатньо місця для \"%s\": потрібно %.2f л, "
                            + "вільно %.2f л",
                    coffee.getName(), coffee.getVolumeLiters(),
                    getAvailableVolume(replaced)));
        }
        if (!fitsBudget(coffee, replaced)) {
            throw new CargoLoadingException(String.format(Locale.ROOT,
                    "Недостатньо коштів для \"%s\": потрібно %s грн, "
                            + "залишок %s грн",
                    coffee.getName(), coffee.getPrice().toPlainString(),
                    getAvailableBudget(replaced).toPlainString()));
        }
    }

    private boolean fitsVolume(Coffee coffee, Coffee replaced) {
        return coffee.getVolumeLiters()
                <= getAvailableVolume(replaced) + VOLUME_TOLERANCE;
    }

    private boolean fitsBudget(Coffee coffee, Coffee replaced) {
        return coffee.getPrice().compareTo(getAvailableBudget(replaced)) <= 0;
    }

    private double getAvailableVolume(Coffee replaced) {
        return (replaced == null) ? getFreeVolumeLiters()
                : getFreeVolumeLiters() + replaced.getVolumeLiters();
    }

    private BigDecimal getAvailableBudget(Coffee replaced) {
        return (replaced == null) ? getRemainingBudget()
                : getRemainingBudget().add(replaced.getPrice());
    }
}
