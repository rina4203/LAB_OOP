/*
 * CoffeeLineFormat.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.io;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ua.lab.coffeevan.model.Coffee;
import ua.lab.coffeevan.model.CoffeeVariety;
import ua.lab.coffeevan.model.PackagingType;
import ua.lab.coffeevan.model.Quality;

/**
 * Текстовий формат одного товару - рядок каталогу з полями,
 * розділеними крапкою з комою:
 * <pre>
 * стан;назва;сорт;вага;ціна;упаковка;аромат;кислотність;тіло;особливість
 * </pre>
 *
 * <p>Уміє розібрати рядок у товар, записати товар у рядок і змінити
 * одне поле товару, створивши новий товар.
 *
 * @author rina4203
 * @version 1.0
 */
public class CoffeeLineFormat {

    /** Роздільник полів. */
    public static final String SEPARATOR = ";";

    /** Назви полів у порядку їх запису. */
    public static final List<String> FIELD_NAMES = List.of("state", "name",
            "variety", "weight", "price", "packaging", "aroma", "acidity",
            "body", "feature");

    /** Українські назви полів у тому самому порядку. */
    public static final List<String> FIELD_ALIASES = List.of("стан", "назва",
            "сорт", "вага", "ціна", "упаковка", "аромат", "кислотність",
            "тіло", "особливість");

    private static final int STATE = 0;
    private static final int NAME = 1;
    private static final int VARIETY = 2;
    private static final int WEIGHT = 3;
    private static final int PRICE = 4;
    private static final int PACKAGING = 5;
    private static final int AROMA = 6;
    private static final int ACIDITY = 7;
    private static final int BODY = 8;
    private static final int FEATURE = 9;

    /**
     * Розбирає рядок каталогу.
     *
     * @param line рядок з полями через {@value #SEPARATOR}
     * @return товар
     * @throws IllegalArgumentException якщо рядок некоректний
     */
    public Coffee parse(String line) {
        Objects.requireNonNull(line, "Рядок каталогу не задано");
        return fromFields(line.split(SEPARATOR, -1));
    }

    /**
     * Записує товар у рядок каталогу.
     *
     * @param coffee товар
     * @return рядок з полями через {@value #SEPARATOR}
     */
    public String format(Coffee coffee) {
        return String.join(SEPARATOR, toFields(coffee));
    }

    /**
     * Створює копію товару зі зміненим полем.
     *
     * @param coffee товар
     * @param field  назва поля англійською чи українською
     * @param value  нове значення
     * @return новий товар
     * @throws IllegalArgumentException якщо поле невідоме
     *                                  або значення некоректне
     */
    public Coffee change(Coffee coffee, String field, String value) {
        if (value.contains(SEPARATOR)) {
            throw new IllegalArgumentException(
                    "Значення не може містити символ \"" + SEPARATOR + "\"");
        }
        String[] fields = toFields(coffee);
        fields[fieldIndex(field)] = value;
        return fromFields(fields);
    }

    /**
     * Знаходить позицію поля за назвою.
     *
     * @param field назва поля англійською чи українською
     * @return позиція поля, починаючи з нуля
     * @throws IllegalArgumentException якщо поле невідоме
     */
    public static int fieldIndex(String field) {
        String key = field.trim().toLowerCase(Locale.ROOT);
        int index = FIELD_NAMES.indexOf(key);
        if (index < 0) {
            index = FIELD_ALIASES.indexOf(key);
        }
        if (index < 0) {
            throw new IllegalArgumentException(String.format(
                    "Невідоме поле \"%s\", допустимі: %s",
                    field.trim(), String.join(", ", FIELD_NAMES)));
        }
        return index;
    }

    private String[] toFields(Coffee coffee) {
        CoffeeForm form = CoffeeForm.of(coffee);
        Quality quality = coffee.getQuality();
        return new String[] {
            form.name(),
            coffee.getName(),
            coffee.getVariety().name(),
            String.valueOf(coffee.getNetWeightGrams()),
            coffee.getPrice().toPlainString(),
            coffee.getPackaging().name(),
            String.valueOf(quality.getAroma()),
            String.valueOf(quality.getAcidity()),
            String.valueOf(quality.getBody()),
            form.getFeature(coffee)
        };
    }

    private Coffee fromFields(String[] rawFields) {
        if (rawFields.length != FIELD_NAMES.size()) {
            throw new IllegalArgumentException(String.format(
                    "очікується %d полів, знайдено %d",
                    FIELD_NAMES.size(), rawFields.length));
        }
        String[] fields = Arrays.stream(rawFields)
                .map(String::trim)
                .toArray(String[]::new);
        CoffeeForm form = ValueParser.parseEnum(CoffeeForm.class,
                fields[STATE], "фізичний стан");
        Quality quality = new Quality(
                ValueParser.parseInt(fields[AROMA], "аромат"),
                ValueParser.parseInt(fields[ACIDITY], "кислотність"),
                ValueParser.parseInt(fields[BODY], "тіло"));
        return form.create(fields[NAME],
                ValueParser.parseEnum(CoffeeVariety.class, fields[VARIETY],
                        "сорт"),
                ValueParser.parseInt(fields[WEIGHT], "вага"),
                ValueParser.parseDecimal(fields[PRICE], "ціна"),
                ValueParser.parseEnum(PackagingType.class, fields[PACKAGING],
                        "упаковка"),
                quality, fields[FEATURE]);
    }
}
