/*
 * UnloadCommandTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.console.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Команда unload")
class UnloadCommandTest extends CommandTestSupport {

    private final UnloadCommand command =
            new UnloadCommand(workspace, printer, out);

    @Test
    @DisplayName("повертає товар з фургона в кінець каталогу")
    void unloadsOneItem() {
        workspace.loadAllThatFit();

        command.execute(args("1"));

        assertEquals(List.of("Kenya AA"), names(workspace.getVan()));
        assertEquals(List.of("Gold Arabica", "Colombia Supremo"),
                names(workspace.getCatalog()));
        assertTrue(output().contains(
                "Повернуто в каталог під №2: Colombia Supremo"));
    }

    @Test
    @DisplayName("повертає весь вантаж")
    void unloadsAll() {
        workspace.loadAllThatFit();

        command.execute(args("all"));

        assertTrue(workspace.getVan().isEmpty());
        assertEquals(3, workspace.getCatalog().size());
        assertTrue(output().contains("Повернуто в каталог товарів: 2"));
    }

    @Test
    @DisplayName("вимагає номер товару або all")
    void requiresArgument() {
        Arguments empty = args("");
        Arguments missing = args("1");

        assertThrows(IllegalArgumentException.class,
                () -> command.execute(empty));
        assertThrows(IndexOutOfBoundsException.class,
                () -> command.execute(missing));
    }
}
