/*
 * EditCommandTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.console.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ua.lab.coffeevan.exception.CargoLoadingException;
import ua.lab.coffeevan.io.CoffeeLineFormat;

@DisplayName("Команда edit")
class EditCommandTest extends CommandTestSupport {

    private final EditCommand command =
            new EditCommand(workspace, new CoffeeLineFormat(), out);

    @Test
    @DisplayName("змінює поле товару в каталозі й показує, що було і стало")
    void editsCatalogItem() {
        command.execute(args("catalog 2 price 450"));

        assertEquals(new BigDecimal("450.00"),
                workspace.getCatalog().get(1).getPrice());
        String text = output();
        assertTrue(text.contains("Було:  Kenya AA"));
        assertTrue(text.contains("420.00 грн"));
        assertTrue(text.contains("Стало: Kenya AA"));
        assertTrue(text.contains("450.00 грн"));
    }

    @Test
    @DisplayName("змінює назву з пробілами українською назвою поля")
    void editsNameWithSpaces() {
        command.execute(args("каталог 1 назва Colombia Supremo Excelso"));

        assertEquals("Colombia Supremo Excelso",
                workspace.getCatalog().get(0).getName());
    }

    @Test
    @DisplayName("у фургоні не дозволяє перевищити бюджет")
    void keepsVanWithinBudget() {
        workspace.loadAllThatFit();
        Arguments tooExpensive = args("van 1 price 1200");

        assertThrows(CargoLoadingException.class,
                () -> command.execute(tooExpensive));
        assertSame(colombia, workspace.getVan().get(0));
    }

    @Test
    @DisplayName("відхиляє неповну команду й неіснуючий номер")
    void rejectsInvalidArguments() {
        Arguments incomplete = args("catalog 1 price");
        Arguments missing = args("catalog 9 price 100");

        assertThrows(IllegalArgumentException.class,
                () -> command.execute(incomplete));
        assertThrows(IndexOutOfBoundsException.class,
                () -> command.execute(missing));
    }
}
