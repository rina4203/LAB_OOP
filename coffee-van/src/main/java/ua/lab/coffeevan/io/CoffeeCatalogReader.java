/*
 * CoffeeCatalogReader.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ua.lab.coffeevan.exception.InvalidDataException;
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
 * Зчитує каталог кавових товарів з текстового файлу.
 *
 * <p>Кожен непорожній рядок, що не починається з {@code #}, описує один
 * товар полями, розділеними крапкою з комою:
 * <pre>
 * стан;назва;сорт;вага_г;ціна_грн;упаковка;аромат;кислотність;тіло;особливість
 * </pre>
 * Стан - {@code BEANS}, {@code GROUND} або {@code INSTANT}; особливість -
 * відповідно {@link RoastLevel}, {@link GrindSize} або
 * {@link InstantProcess}.
 *
 * @author rina4203
 * @version 1.0
 */
public class CoffeeCatalogReader {

    /** Позначення кави у зернах у файлі каталогу. */
    public static final String BEANS = "BEANS";

    /** Позначення меленої кави у файлі каталогу. */
    public static final String GROUND = "GROUND";

    /** Позначення розчинної кави у файлі каталогу. */
    public static final String INSTANT = "INSTANT";

    private static final String COMMENT_PREFIX = "#";
    private static final String SEPARATOR = ";";
    private static final int FIELD_COUNT = 10;

    private static final int STATE_FIELD = 0;
    private static final int NAME_FIELD = 1;
    private static final int VARIETY_FIELD = 2;
    private static final int WEIGHT_FIELD = 3;
    private static final int PRICE_FIELD = 4;
    private static final int PACKAGING_FIELD = 5;
    private static final int AROMA_FIELD = 6;
    private static final int ACIDITY_FIELD = 7;
    private static final int BODY_FIELD = 8;
    private static final int FEATURE_FIELD = 9;

    /**
     * Зчитує всі товари каталогу.
     *
     * @param reader джерело даних каталогу
     * @return товари у порядку їх переліку в каталозі
     * @throws InvalidDataException якщо рядок каталогу некоректний
     * @throws IOException          якщо сталася помилка читання
     */
    public List<Coffee> read(Reader reader) throws IOException {
        BufferedReader lines = new BufferedReader(reader);
        List<Coffee> catalog = new ArrayList<>();
        int lineNumber = 0;
        String line = lines.readLine();
        while (line != null) {
            lineNumber++;
            String content = line.trim();
            if (!content.isEmpty() && !content.startsWith(COMMENT_PREFIX)) {
                catalog.add(parseLine(content, lineNumber));
            }
            line = lines.readLine();
        }
        return catalog;
    }

    private Coffee parseLine(String line, int lineNumber)
            throws InvalidDataException {
        String[] fields = line.split(SEPARATOR, -1);
        if (fields.length != FIELD_COUNT) {
            throw new InvalidDataException(String.format(
                    "Рядок %d: очікується %d полів, знайдено %d",
                    lineNumber, FIELD_COUNT, fields.length));
        }
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
        }
        try {
            return createCoffee(fields);
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException(
                    "Рядок " + lineNumber + ": " + e.getMessage(), e);
        }
    }

    private Coffee createCoffee(String[] fields) {
        String state = fields[STATE_FIELD];
        String name = fields[NAME_FIELD];
        CoffeeVariety variety = CoffeeVariety.valueOf(fields[VARIETY_FIELD]);
        int weight = Integer.parseInt(fields[WEIGHT_FIELD]);
        BigDecimal price = new BigDecimal(fields[PRICE_FIELD]);
        PackagingType packaging =
                PackagingType.valueOf(fields[PACKAGING_FIELD]);
        Quality quality = new Quality(
                Integer.parseInt(fields[AROMA_FIELD]),
                Integer.parseInt(fields[ACIDITY_FIELD]),
                Integer.parseInt(fields[BODY_FIELD]));
        String feature = fields[FEATURE_FIELD];

        if (BEANS.equals(state)) {
            return new CoffeeBeans(name, variety, weight, price, packaging,
                    quality, RoastLevel.valueOf(feature));
        }
        if (GROUND.equals(state)) {
            return new GroundCoffee(name, variety, weight, price, packaging,
                    quality, GrindSize.valueOf(feature));
        }
        if (INSTANT.equals(state)) {
            return new InstantCoffee(name, variety, weight, price, packaging,
                    quality, InstantProcess.valueOf(feature));
        }
        throw new IllegalArgumentException(
                "невідомий фізичний стан кави \"" + state + "\"");
    }
}
