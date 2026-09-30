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
import java.util.ArrayList;
import java.util.List;

import ua.lab.coffeevan.exception.InvalidDataException;
import ua.lab.coffeevan.model.Coffee;

/**
 * Зчитує каталог кавових товарів з текстового файлу.
 *
 * <p>Кожен непорожній рядок, що не починається з {@code #}, описує один
 * товар у форматі {@link CoffeeLineFormat}:
 * <pre>
 * стан;назва;сорт;вага;ціна;упаковка;аромат;кислотність;тіло;особливість
 * </pre>
 * Стан - {@code BEANS}, {@code GROUND} або {@code INSTANT}
 * (див. {@link CoffeeForm}).
 *
 * @author rina4203
 * @version 1.0
 */
public class CoffeeCatalogReader {

    private static final String COMMENT_PREFIX = "#";

    private final CoffeeLineFormat format = new CoffeeLineFormat();

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
        try {
            return format.parse(line);
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException(
                    "Рядок " + lineNumber + ": " + e.getMessage(), e);
        }
    }
}
