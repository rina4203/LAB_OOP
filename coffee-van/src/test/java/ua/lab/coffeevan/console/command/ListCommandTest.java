/*
 * ListCommandTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.console.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Команда list")
class ListCommandTest extends CommandTestSupport {

    private final ListCommand command = new ListCommand(workspace, printer);

    @Test
    @DisplayName("без аргументів показує і каталог, і фургон")
    void showsBothLists() {
        workspace.loadFromCatalog(1);

        command.execute(args(""));

        String text = output();
        assertTrue(text.contains("--- Каталог (товарів: 2) ---"));
        assertTrue(text.contains("=== Стан фургона ==="));
        assertTrue(text.contains("--- Фургон (товарів: 1) ---"));
        assertTrue(text.contains("  1  Kenya AA"));
    }

    @Test
    @DisplayName("list catalog показує лише каталог")
    void showsOnlyCatalog() {
        command.execute(args("catalog"));

        assertTrue(output().contains("--- Каталог (товарів: 3) ---"));
        assertFalse(output().contains("Фургон"));
    }

    @Test
    @DisplayName("list van показує лише фургон")
    void showsOnlyVan() {
        command.execute(args("фургон"));

        assertTrue(output().contains("--- Фургон (товарів: 0) ---"));
        assertFalse(output().contains("Каталог"));
    }

    @Test
    @DisplayName("відхиляє невідомий список і зайві аргументи")
    void rejectsInvalidArguments() {
        Arguments unknown = args("warehouse");
        Arguments tooMany = args("catalog van");

        assertThrows(IllegalArgumentException.class,
                () -> command.execute(unknown));
        assertThrows(IllegalArgumentException.class,
                () -> command.execute(tooMany));
    }
}
