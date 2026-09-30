/*
 * CoffeeCollection.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.stock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import ua.lab.coffeevan.model.Coffee;
import ua.lab.coffeevan.model.QualityRange;

/**
 * Упорядкований список кавових товарів.
 *
 * <p>Спільна основа каталогу товарів і вантажу фургона: додавання,
 * заміна, видалення, пошук і сортування. Підклас може обмежити, які
 * товари потрапляють до списку, перевизначивши
 * {@link #checkCanAdd(Coffee, Coffee)} (шаблонний метод).
 *
 * <p>Позиції товарів нумеруються з нуля; у повідомленнях для
 * користувача номер показується з одиниці, як у таблицях звітів.
 *
 * @author rina4203
 * @version 1.0
 */
public abstract class CoffeeCollection {

    private final List<Coffee> items = new ArrayList<>();

    /**
     * Повертає назву списку для повідомлень користувачу.
     *
     * @return назва списку, наприклад "каталог"
     */
    public abstract String getDisplayName();

    /**
     * Додає товар у кінець списку.
     *
     * @param coffee товар
     * @throws NullPointerException якщо товар не задано
     */
    public void add(Coffee coffee) {
        Objects.requireNonNull(coffee, "Товар не задано");
        checkCanAdd(coffee, null);
        items.add(coffee);
    }

    /**
     * Повертає товар за позицією.
     *
     * @param index позиція товару, починаючи з нуля
     * @return товар
     * @throws IndexOutOfBoundsException якщо такої позиції немає
     */
    public Coffee get(int index) {
        checkIndex(index);
        return items.get(index);
    }

    /**
     * Замінює товар на вказаній позиції іншим.
     *
     * @param index  позиція товару, починаючи з нуля
     * @param coffee новий товар
     * @return товар, що був на цій позиції
     * @throws IndexOutOfBoundsException якщо такої позиції немає
     */
    public Coffee replace(int index, Coffee coffee) {
        Objects.requireNonNull(coffee, "Товар не задано");
        Coffee replaced = get(index);
        checkCanAdd(coffee, replaced);
        items.set(index, coffee);
        return replaced;
    }

    /**
     * Видаляє товар за позицією.
     *
     * @param index позиція товару, починаючи з нуля
     * @return видалений товар
     * @throws IndexOutOfBoundsException якщо такої позиції немає
     */
    public Coffee remove(int index) {
        checkIndex(index);
        return items.remove(index);
    }

    /**
     * Видаляє всі товари зі списку.
     *
     * @return видалені товари в тому порядку, в якому вони були
     */
    public List<Coffee> removeAll() {
        List<Coffee> removed = new ArrayList<>(items);
        items.clear();
        return removed;
    }

    /**
     * Повертає позицію товару в списку.
     *
     * @param coffee товар
     * @return позиція, починаючи з нуля, або -1, якщо товару немає
     */
    public int indexOf(Coffee coffee) {
        return items.indexOf(coffee);
    }

    /**
     * Повертає кількість товарів у списку.
     *
     * @return кількість товарів
     */
    public int size() {
        return items.size();
    }

    /**
     * Перевіряє, чи список порожній.
     *
     * @return {@code true}, якщо товарів немає
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Повертає товари списку.
     *
     * @return незмінний список товарів
     */
    public List<Coffee> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Сортує товари в заданому порядку.
     *
     * @param order порядок сортування
     */
    public void sort(Comparator<Coffee> order) {
        Objects.requireNonNull(order, "Порядок сортування не задано");
        items.sort(order);
    }

    /**
     * Знаходить товари, назва яких містить заданий текст
     * (без урахування регістру).
     *
     * @param text частина назви
     * @return знайдені товари в порядку розміщення у списку
     */
    public List<Coffee> findByName(String text) {
        Objects.requireNonNull(text, "Текст для пошуку не задано");
        String pattern = text.trim().toLowerCase(Locale.ROOT);
        return items.stream()
                .filter(coffee -> coffee.getName().toLowerCase(Locale.ROOT)
                        .contains(pattern))
                .collect(Collectors.toList());
    }

    /**
     * Знаходить товари, параметри якості яких потрапляють у діапазон.
     *
     * @param range діапазон параметрів якості
     * @return знайдені товари в порядку розміщення у списку
     */
    public List<Coffee> findByQuality(QualityRange range) {
        Objects.requireNonNull(range, "Діапазон якості не задано");
        return items.stream()
                .filter(coffee -> range.contains(coffee.getQuality()))
                .collect(Collectors.toList());
    }

    /**
     * Перевіряє, чи можна помістити товар у список. За замовчуванням
     * дозволено будь-який товар; підкласи можуть додати обмеження.
     *
     * @param coffee   товар, що додається
     * @param replaced товар, який він замінює, або {@code null},
     *                 якщо товар додається
     */
    protected void checkCanAdd(Coffee coffee, Coffee replaced) {
        // Будь-який товар допускається; обмеження задають підкласи.
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= items.size()) {
            throw new IndexOutOfBoundsException(String.format(
                    "У списку \"%s\" немає товару №%d (усього товарів: %d)",
                    getDisplayName(), index + 1, items.size()));
        }
    }
}
