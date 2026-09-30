/*
 * RemoveCommandTest.java
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

@DisplayName("Команда remove")
class RemoveCommandTest extends CommandTestSupport {

    private final RemoveCommand command = new RemoveCommand(workspace, out);

    @Test
    @DisplayName("видаляє товар з каталогу за номером")
    void removesFromCatalog() {
        command.execute(args("catalog 2"));

        assertEquals(List.of("Colombia Supremo", "Gold Arabica"),
                names(workspace.getCatalog()));
        assertTrue(output().contains("Видалено: Kenya AA"));
    }

    @Test
    @DisplayName("видаляє товар з фургона - він не повертається в каталог")
    void removesFromVan() {
        workspace.loadFromCatalog(0);

        command.execute(args("van 1"));

        assertTrue(workspace.getVan().isEmpty());
        assertEquals(2, workspace.getCatalog().size());
    }

    @Test
    @DisplayName("видаляє всі товари зі списку")
    void removesAll() {
        command.execute(args("catalog all"));

        assertTrue(workspace.getCatalog().isEmpty());
        assertTrue(output().contains(
                "Видалено всі товари зі списку \"каталог\": 3"));
    }

    @Test
    @DisplayName("відхиляє неправильні аргументи")
    void rejectsInvalidArguments() {
        Arguments missingNumber = args("catalog");
        Arguments emptyVan = args("van 1");

        assertThrows(IllegalArgumentException.class,
                () -> command.execute(missingNumber));
        assertThrows(IndexOutOfBoundsException.class,
                () -> command.execute(emptyVan));
    }
}
