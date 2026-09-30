/*
 * SortCommandTest.java
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Команда sort")
class SortCommandTest extends CommandTestSupport {

    private final SortCommand command = new SortCommand(workspace, printer);

    @Test
    @DisplayName("типово сортує фургон за зростанням ціни кілограма")
    void sortsVanAscendingByDefault() {
        workspace.loadFromCatalog(1);
        workspace.loadFromCatalog(0);

        command.execute(args(""));

        assertEquals(List.of("Colombia Supremo", "Kenya AA"),
                names(workspace.getVan()));
        assertTrue(output().contains("--- Фургон (товарів: 2) ---"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"van desc", "desc van", "DESC"})
    @DisplayName("сортує фургон за спаданням")
    void sortsVanDescending(String text) {
        workspace.loadAllThatFit();

        command.execute(args(text));

        assertEquals(List.of("Kenya AA", "Colombia Supremo"),
                names(workspace.getVan()));
    }

    @Test
    @DisplayName("сортує каталог у будь-якому напрямку")
    void sortsCatalog() {
        command.execute(args("catalog desc"));
        assertEquals(List.of("Gold Arabica", "Kenya AA", "Colombia Supremo"),
                names(workspace.getCatalog()));

        command.execute(args("каталог asc"));
        assertEquals(List.of("Colombia Supremo", "Kenya AA", "Gold Arabica"),
                names(workspace.getCatalog()));
    }

    @Test
    @DisplayName("відхиляє невідомі слова й зайві аргументи")
    void rejectsInvalidArguments() {
        Arguments unknown = args("price");
        Arguments tooMany = args("van desc now");

        assertThrows(IllegalArgumentException.class,
                () -> command.execute(unknown));
        assertThrows(IllegalArgumentException.class,
                () -> command.execute(tooMany));
    }
}
