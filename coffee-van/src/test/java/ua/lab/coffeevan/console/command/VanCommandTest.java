/*
 * VanCommandTest.java
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Команда van")
class VanCommandTest extends CommandTestSupport {

    private static final double DELTA = 1e-9;

    private final VanCommand command = new VanCommand(workspace, printer);

    @Test
    @DisplayName("без аргументів показує стан і вантаж фургона")
    void showsVan() {
        command.execute(args(""));

        assertTrue(output().contains("зайнято 0,00 л з 5,00 л"));
        assertTrue(output().contains("--- Фургон (товарів: 0) ---"));
    }

    @Test
    @DisplayName("задає новий об'єм і бюджет (дробові числа з комою)")
    void resizesVan() {
        command.execute(args("12,5 3000"));

        assertEquals(12.5, workspace.getVan().getCapacityLiters(), DELTA);
        assertEquals(new BigDecimal("3000"), workspace.getVan().getBudget());
        assertTrue(output().contains("зайнято 0,00 л з 12,50 л"));
        assertFalse(output().contains("Не вмістилися"));
    }

    @Test
    @DisplayName("показує товари, що не вмістилися в менший фургон")
    void showsReturnedItems() {
        workspace.loadAllThatFit();

        command.execute(args("3 2000"));

        assertTrue(output().contains(
                "--- Не вмістилися в новий фургон і повернулися в каталог"));
        assertEquals(List.of("Gold Arabica", "Kenya AA"),
                names(workspace.getCatalog()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"5", "багато 100", "0 100", "5 -1", "1 2 3"})
    @DisplayName("відхиляє неповні чи некоректні параметри")
    void rejectsInvalidArguments(String text) {
        Arguments arguments = args(text);

        assertThrows(IllegalArgumentException.class,
                () -> command.execute(arguments));
        assertEquals(5.0, workspace.getVan().getCapacityLiters(), DELTA);
    }
}
