/*
 * CoffeeCatalogWriter.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.io;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import ua.lab.coffeevan.model.Coffee;

/**
 * Записує товари у файл каталогу, який потім можна прочитати
 * {@link CoffeeCatalogReader}.
 *
 * @author rina4203
 * @version 1.0
 */
public class CoffeeCatalogWriter {

    /** Рядок-коментар з назвами полів на початку файлу. */
    public static final String HEADER = "# стан;назва;сорт;вага_г;ціна_грн;"
            + "упаковка;аромат;кислотність;тіло;особливість";

    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final CoffeeLineFormat format = new CoffeeLineFormat();

    /**
     * Записує товари по одному в рядку.
     *
     * @param writer приймач даних
     * @param items  товари
     * @throws IOException якщо сталася помилка запису
     */
    public void write(Writer writer, List<Coffee> items) throws IOException {
        writer.write(HEADER + LINE_SEPARATOR);
        for (Coffee coffee : items) {
            writer.write(format.format(coffee) + LINE_SEPARATOR);
        }
        writer.flush();
    }
}
