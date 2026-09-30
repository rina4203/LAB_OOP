/*
 * FindCommandTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.console.command;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Команда find")
class FindCommandTest extends CommandTestSupport {

    private final FindCommand command = new FindCommand(workspace, printer);

    @Test
    @DisplayName("шукає в обох списках і показує номери товарів")
    void findsInBothLists() {
        workspace.loadFromCatalog(0);

        command.execute(args("a"));

        String text = output();
        assertTrue(text.contains("--- Каталог: знайдено за \"a\" ---"));
        assertTrue(text.contains("  1  Kenya AA"));
        assertTrue(text.contains("  2  Gold Arabica"));
        assertTrue(text.contains("--- Фургон: знайдено за \"a\" ---"));
        assertTrue(text.contains("  1  Colombia Supremo"));
    }

    @Test
    @DisplayName("шукає за кількома словами назви")
    void findsByPhrase() {
        command.execute(args("gold arabica"));

        assertTrue(output().contains("  3  Gold Arabica"));
        assertTrue(output().contains("(товарів немає)"));
    }

    @Test
    @DisplayName("вимагає текст для пошуку")
    void requiresText() {
        Arguments empty = args("");

        assertThrows(IllegalArgumentException.class,
                () -> command.execute(empty));
    }
}
