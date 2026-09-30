/*
 * CoffeeVanShellTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.console;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ua.lab.coffeevan.TestCoffees;
import ua.lab.coffeevan.config.VanConfig;
import ua.lab.coffeevan.model.Quality;
import ua.lab.coffeevan.model.QualityRange;
import ua.lab.coffeevan.van.VanLoader;
import ua.lab.coffeevan.van.VanWorkspace;

@DisplayName("Інтерактивна оболонка")
class CoffeeVanShellTest {

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private final PrintStream out =
            new PrintStream(buffer, true, StandardCharsets.UTF_8);
    private final VanWorkspace workspace = new VanWorkspace(
            new VanConfig(5.0, new BigDecimal("1600"), new QualityRange(
                    new Quality(7, 5, 5), new Quality(10, 9, 8))),
            List.of(TestCoffees.beans("Colombia Supremo", "1150"),
                    TestCoffees.ground("Kenya AA", "420"),
                    TestCoffees.instant("Gold Arabica", "340")),
            new VanLoader());

    @Test
    @DisplayName("вітає, показує довідку й прощається")
    void greetsAndSaysGoodbye() throws IOException {
        String text = run("exit\n");

        assertTrue(text.startsWith("=== Фургон кави: інтерактивний режим"));
        assertTrue(text.contains("  load [<№>|all]"));
        assertTrue(text.contains("\n> "));
        assertTrue(text.strip().endsWith("До побачення!"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"exit", "  QUIT ", "вихід"})
    @DisplayName("завершується командою виходу")
    void stopsOnExitWord(String word) throws IOException {
        String text = run(word + "\nlist\n");

        assertFalse(text.contains("--- Каталог"));
    }

    @Test
    @DisplayName("завершується, коли закінчилося введення")
    void stopsAtEndOfInput() throws IOException {
        assertTrue(run("list van").strip().endsWith("До побачення!"));
    }

    @Test
    @DisplayName("виконує команди по черзі: сценарій завантаження фургона")
    void runsLoadingScenario() throws IOException {
        String text = run(String.join("\n",
                "load",
                "sort desc",
                "search 9 0-10 0-10",
                "exit"));

        assertEquals(2, workspace.getVan().size());
        assertEquals("Kenya AA", workspace.getVan().get(0).getName());
        assertTrue(text.contains("У каталозі залишилося товарів: 1"));
        assertTrue(text.contains("--- Фургон: якість аромат 9-9"));
    }

    @Test
    @DisplayName("назва команди не залежить від регістру")
    void ignoresCommandCase() throws IOException {
        String text = run("LIST Catalog\nexit\n");

        assertTrue(text.contains("--- Каталог (товарів: 3) ---"));
    }

    @Test
    @DisplayName("пропускає порожні рядки")
    void skipsEmptyLines() {
        new CoffeeVanShell(workspace, reader(""), out).execute("   ");

        assertEquals("", output());
    }

    @Test
    @DisplayName("пояснює, що команда невідома")
    void reportsUnknownCommand() throws IOException {
        String text = run("bogus stuff\nexit\n");

        assertTrue(text.contains(
                "Невідома команда \"bogus\". Перелік команд: help"));
    }

    @Test
    @DisplayName("після помилки продовжує роботу")
    void continuesAfterErrors() throws IOException {
        String text = run(String.join("\n",
                "load 9",
                "list warehouse",
                "load 1",
                "load 1",
                "load 1",
                "add",
                "beans"));

        assertTrue(text.contains("Помилка: У списку \"каталог\" "
                + "немає товару №9"));
        assertTrue(text.contains("Помилка: невідомий список \"warehouse\""));
        assertTrue(text.contains("Помилка: Недостатньо коштів для "
                + "\"Gold Arabica\""));
        assertTrue(text.contains("Помилка: введення перервано"));
        assertTrue(text.strip().endsWith("До побачення!"));
    }

    @Test
    @DisplayName("вимагає робоче місце, введення й виведення")
    void requiresDependencies() {
        BufferedReader input = reader("");

        assertThrows(NullPointerException.class,
                () -> new CoffeeVanShell(null, input, out));
        assertThrows(NullPointerException.class,
                () -> new CoffeeVanShell(workspace, null, out));
        assertThrows(NullPointerException.class,
                () -> new CoffeeVanShell(workspace, input, null));
    }

    private String run(String script) throws IOException {
        new CoffeeVanShell(workspace, reader(script), out).run();
        return output();
    }

    private String output() {
        return buffer.toString(StandardCharsets.UTF_8);
    }

    private static BufferedReader reader(String text) {
        return new BufferedReader(new StringReader(text));
    }
}
