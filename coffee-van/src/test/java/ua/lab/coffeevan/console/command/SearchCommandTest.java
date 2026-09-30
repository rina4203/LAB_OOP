/*
 * SearchCommandTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.console.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ua.lab.coffeevan.model.Quality;

@DisplayName("Команда search")
class SearchCommandTest extends CommandTestSupport {

    private final SearchCommand command =
            new SearchCommand(workspace, printer);

    @Test
    @DisplayName("без аргументів шукає у фургоні за поточним діапазоном")
    void searchesVanWithCurrentRange() {
        workspace.loadAllThatFit();

        command.execute(args(""));

        String text = output();
        assertTrue(text.contains("--- Фургон: якість аромат 7-10, "
                + "кислотність 5-9, тіло 5-8 ---"));
        assertTrue(text.contains("  1  Colombia Supremo"));
        assertTrue(text.contains("  2  Kenya AA"));
        assertSame(RANGE, workspace.getSearchRange());
    }

    @Test
    @DisplayName("задає й запам'ятовує новий діапазон")
    void setsNewRange() {
        workspace.loadAllThatFit();

        command.execute(args("9 0-10 0-10"));

        assertEquals(new Quality(9, 0, 0), workspace.getSearchRange().getMin());
        assertEquals(new Quality(9, 10, 10),
                workspace.getSearchRange().getMax());
        assertTrue(output().contains("  2  Kenya AA"));
        assertFalse(output().contains("Colombia"));
    }

    @Test
    @DisplayName("може шукати в каталозі")
    void searchesCatalog() {
        command.execute(args("catalog 0-6 0-10 0-10"));

        assertTrue(output().contains("--- Каталог: якість аромат 0-6"));
        assertTrue(output().contains("  3  Gold Arabica"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"7-10 5-9", "catalog 7-10", "1-2-3 0-10 0-10",
            "високий 0-10 0-10", "10-7 0-10 0-10",
            "van 7-10 5-9 5-8 extra"})
    @DisplayName("відхиляє неповний чи некоректний діапазон")
    void rejectsInvalidRange(String text) {
        Arguments arguments = args(text);

        assertThrows(IllegalArgumentException.class,
                () -> command.execute(arguments));
        assertSame(RANGE, workspace.getSearchRange());
    }
}
