/*
 * CoffeeVanApplicationMainTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@DisplayName("Запуск програми з консолі")
class CoffeeVanApplicationMainTest {

    private static final String CONFIG = String.join("\n",
            "van.capacity.liters=3",
            "van.budget=1000",
            "search.aroma.min=0",
            "search.aroma.max=10",
            "search.acidity.min=0",
            "search.acidity.max=10",
            "search.body.min=0",
            "search.body.max=10");
    private static final String CATALOG = String.join("\n",
            "BEANS;Colombia;ARABICA;1000;900;VACUUM_PACK;8;6;7;MEDIUM",
            "INSTANT;Sticks;ARABICA;50;95;SACHET;5;3;4;AGGLOMERATED");

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    private PrintStream originalOut;
    private PrintStream originalErr;

    @TempDir
    Path tempDir;

    @BeforeEach
    void redirectConsole() {
        originalOut = System.out;
        originalErr = System.err;
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(err, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreConsole() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    @DisplayName("без аргументів використовує вбудовані файли")
    void runsWithBundledData() {
        CoffeeVanApplication.main(new String[0]);

        String report = out.toString(StandardCharsets.UTF_8);
        assertTrue(report.contains("=== Фургон кави ==="));
        assertTrue(report.contains("завантажено 9, не вмістилося 6"));
        assertTrue(report.contains(CoffeeVanApplication.SORTED_TITLE));
        assertTrue(report.contains(CoffeeVanApplication.SEARCH_TITLE));
        assertEquals("", err.toString(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("читає параметри та каталог з файлів, заданих аргументами")
    void runsWithFilesFromArguments() throws IOException {
        Path config = writeFile("van.properties", CONFIG);
        Path catalog = writeFile("catalog.csv", CATALOG);

        CoffeeVanApplication.main(
                new String[] {config.toString(), catalog.toString()});

        String report = out.toString(StandardCharsets.UTF_8);
        assertTrue(report.contains("завантажено 1, не вмістилося 1"));
        assertTrue(report.contains("Sticks"));
        assertEquals("", err.toString(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("з одним аргументом бере вбудований каталог")
    void usesBundledCatalogWithConfigArgument() throws IOException {
        Path config = writeFile("van.properties", CONFIG);

        CoffeeVanApplication.main(new String[] {config.toString()});

        String report = out.toString(StandardCharsets.UTF_8);
        assertTrue(report.contains("з 3,00 л"));
        assertTrue(report.contains("Ethiopia Yirgacheffe"));
    }

    @Test
    @DisplayName("повідомляє про відсутній файл")
    void reportsMissingFile() {
        String missing = tempDir.resolve("missing.properties").toString();

        CoffeeVanApplication.main(new String[] {missing});

        assertTrue(err.toString(StandardCharsets.UTF_8).startsWith("Помилка"));
        assertEquals("", out.toString(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("повідомляє про некоректні дані")
    void reportsInvalidData() throws IOException {
        Path config = writeFile("van.properties", "van.budget=1000");

        CoffeeVanApplication.main(new String[] {config.toString()});

        String errors = err.toString(StandardCharsets.UTF_8);
        assertTrue(errors.contains("Відсутній параметр"));
        assertEquals("", out.toString(StandardCharsets.UTF_8));
    }

    private Path writeFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content, StandardCharsets.UTF_8);
        return file;
    }
}
