/*
 * CoffeeVanShellMainTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.console;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@DisplayName("Запуск інтерактивного режиму")
class CoffeeVanShellMainTest {

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    private InputStream originalIn;
    private PrintStream originalOut;
    private PrintStream originalErr;

    @TempDir
    Path tempDir;

    @BeforeEach
    void redirectConsole() {
        originalIn = System.in;
        originalOut = System.out;
        originalErr = System.err;
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(err, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreConsole() {
        System.setIn(originalIn);
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    @DisplayName("без аргументів працює з вбудованими даними")
    void runsWithBundledData() {
        input("list catalog\nexit\n");

        CoffeeVanShell.main(new String[0]);

        String text = out.toString(StandardCharsets.UTF_8);
        assertTrue(text.contains("--- Каталог (товарів: 15) ---"));
        assertTrue(text.contains("До побачення!"));
        assertEquals("", err.toString(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("читає параметри й каталог з файлів і кирилицю з клавіатури")
    void runsWithFilesFromArguments() throws IOException {
        Path config = tempDir.resolve("van.properties");
        Files.writeString(config, String.join("\n", "van.capacity.liters=3",
                "van.budget=1000", "search.aroma.min=0", "search.aroma.max=10",
                "search.acidity.min=0", "search.acidity.max=10",
                "search.body.min=0", "search.body.max=10"),
                StandardCharsets.UTF_8);
        Path catalog = tempDir.resolve("catalog.csv");
        Files.writeString(catalog,
                "GROUND;Kenya AA;ARABICA;250;420;PAPER_BAG;9;9;6;MEDIUM",
                StandardCharsets.UTF_8);
        input("find кенія\nedit catalog 1 назва Кенія\nfind кенія\nexit\n");

        CoffeeVanShell.main(new String[] {config.toString(),
            catalog.toString()});

        String text = out.toString(StandardCharsets.UTF_8);
        assertTrue(text.contains("Стало: Кенія (арабіка"));
        assertTrue(text.contains("  1  Кенія"));
    }

    @Test
    @DisplayName("повідомляє, якщо файл даних не знайдено")
    void reportsMissingFile() {
        input("exit\n");

        CoffeeVanShell.main(new String[] {
            tempDir.resolve("missing.properties").toString()});

        assertTrue(err.toString(StandardCharsets.UTF_8)
                .startsWith("Помилка"));
        assertEquals("", out.toString(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("повідомляє, якщо введення неможливо прочитати")
    void reportsBrokenInput() {
        System.setIn(new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("клавіатура недоступна");
            }
        });

        CoffeeVanShell.main(new String[0]);

        assertTrue(err.toString(StandardCharsets.UTF_8)
                .contains("Помилка: клавіатура недоступна"));
    }

    private static void input(String text) {
        System.setIn(new ByteArrayInputStream(
                text.getBytes(StandardCharsets.UTF_8)));
    }
}
