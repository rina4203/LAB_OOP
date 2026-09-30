/*
 * Arguments.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.console.command;

import java.util.List;
import java.util.Locale;

import ua.lab.coffeevan.io.ValueParser;
import ua.lab.coffeevan.stock.CoffeeCollection;
import ua.lab.coffeevan.van.VanWorkspace;

/**
 * Аргументи команди - слова, введені після її назви.
 *
 * @author rina4203
 * @version 1.0
 */
public final class Arguments {

    private static final List<String> CATALOG_WORDS =
            List.of("catalog", "каталог");
    private static final List<String> VAN_WORDS = List.of("van", "фургон");
    private static final List<String> ALL_WORDS = List.of("all", "всі");
    private static final String WHITESPACE = "\\s+";

    private final String text;
    private final List<String> words;

    /**
     * Розбиває текст на слова.
     *
     * @param text текст після назви команди
     */
    public Arguments(String text) {
        this.text = (text == null) ? "" : text.trim();
        this.words = this.text.isEmpty() ? List.of()
                : List.of(this.text.split(WHITESPACE));
    }

    /**
     * Повертає кількість слів.
     *
     * @return кількість аргументів
     */
    public int count() {
        return words.size();
    }

    /**
     * Перевіряє, чи аргументів немає.
     *
     * @return {@code true}, якщо нічого не введено
     */
    public boolean isEmpty() {
        return words.isEmpty();
    }

    /**
     * Повертає слово за позицією.
     *
     * @param position позиція, починаючи з нуля
     * @return слово
     * @throws IllegalArgumentException якщо слова немає
     */
    public String get(int position) {
        if (position >= words.size()) {
            throw new IllegalArgumentException(
                    "бракує аргументу №" + (position + 1));
        }
        return words.get(position);
    }

    /**
     * Повертає весь текст, починаючи зі слова на позиції, разом
     * з пробілами між словами (для назв і шляхів з пробілами).
     *
     * @param position позиція першого слова
     * @return решта тексту
     * @throws IllegalArgumentException якщо слова немає
     */
    public String getRest(int position) {
        get(position);
        return text.split(WHITESPACE, position + 1)[position];
    }

    /**
     * Перевіряє, чи на позиції стоїть слово {@code all} ({@code всі}).
     *
     * @param position позиція
     * @return {@code true}, якщо введено {@code all}
     */
    public boolean isAll(int position) {
        return position < words.size() && ALL_WORDS.contains(
                words.get(position).toLowerCase(Locale.ROOT));
    }

    /**
     * Перевіряє, чи на позиції стоїть назва списку.
     *
     * @param position позиція
     * @return {@code true}, якщо введено {@code catalog} або {@code van}
     */
    public boolean isList(int position) {
        if (position >= words.size()) {
            return false;
        }
        String word = words.get(position).toLowerCase(Locale.ROOT);
        return CATALOG_WORDS.contains(word) || VAN_WORDS.contains(word);
    }

    /**
     * Перетворює номер товару, як його показують таблиці (з одиниці),
     * на позицію в списку (з нуля).
     *
     * @param position позиція аргументу з номером
     * @return позиція товару в списку
     * @throws IllegalArgumentException якщо номер некоректний
     */
    public int getItemIndex(int position) {
        int number = ValueParser.parseInt(get(position), "номер товару");
        if (number < 1) {
            throw new IllegalArgumentException(
                    "номер товару має бути від 1, отримано " + number);
        }
        return number - 1;
    }

    /**
     * Повертає список, названий аргументом.
     *
     * @param position  позиція аргументу з назвою списку
     * @param workspace робоче місце з каталогом і фургоном
     * @return каталог або фургон
     * @throws IllegalArgumentException якщо назва невідома
     */
    public CoffeeCollection getList(int position, VanWorkspace workspace) {
        String word = get(position).toLowerCase(Locale.ROOT);
        if (CATALOG_WORDS.contains(word)) {
            return workspace.getCatalog();
        }
        if (VAN_WORDS.contains(word)) {
            return workspace.getVan();
        }
        throw new IllegalArgumentException("невідомий список \""
                + get(position) + "\", допустимі: catalog, van");
    }
}
