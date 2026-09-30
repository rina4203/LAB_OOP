/*
 * ArgumentsTest.java
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
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Аргументи команди")
class ArgumentsTest extends CommandTestSupport {

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    @DisplayName("порожній текст - жодного аргументу")
    void emptyText(String text) {
        Arguments arguments = new Arguments(text);

        assertTrue(arguments.isEmpty());
        assertEquals(0, arguments.count());
    }

    @Test
    @DisplayName("розбиває текст на слова за пробілами")
    void splitsWords() {
        Arguments arguments = args("  edit   van 2 ");

        assertEquals(3, arguments.count());
        assertFalse(arguments.isEmpty());
        assertEquals("van", arguments.get(1));
    }

    @Test
    @DisplayName("пояснює, якого аргументу бракує")
    void reportsMissingArgument() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class, () -> args("one").get(1));

        assertEquals("бракує аргументу №2", e.getMessage());
    }

    @Test
    @DisplayName("повертає решту тексту разом з пробілами")
    void returnsRestOfText() {
        Arguments arguments = args("catalog 2 name Kenya  AA   Top");

        assertEquals("Kenya  AA   Top", arguments.getRest(3));
        assertEquals("catalog 2 name Kenya  AA   Top", arguments.getRest(0));
        assertThrows(IllegalArgumentException.class,
                () -> arguments.getRest(7));
    }

    @Test
    @DisplayName("розпізнає слово all англійською чи українською")
    void recognisesAll() {
        assertTrue(args("ALL").isAll(0));
        assertTrue(args("всі").isAll(0));
        assertFalse(args("2").isAll(0));
        assertFalse(args("").isAll(0));
    }

    @Test
    @DisplayName("розпізнає назви списків")
    void recognisesListNames() {
        assertTrue(args("catalog").isList(0));
        assertTrue(args("Фургон").isList(0));
        assertFalse(args("7-10").isList(0));
        assertFalse(args("").isList(0));
    }

    @Test
    @DisplayName("перетворює номер з таблиці на позицію в списку")
    void convertsItemNumber() {
        assertEquals(0, args("1").getItemIndex(0));
        assertEquals(14, args("15").getItemIndex(0));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-3", "перший"})
    @DisplayName("відхиляє некоректний номер товару")
    void rejectsInvalidItemNumber(String number) {
        assertThrows(IllegalArgumentException.class,
                () -> args(number).getItemIndex(0));
    }

    @Test
    @DisplayName("знаходить список за назвою англійською чи українською")
    void resolvesLists() {
        assertSame(workspace.getCatalog(),
                args("catalog").getList(0, workspace));
        assertSame(workspace.getCatalog(),
                args("КАТАЛОГ").getList(0, workspace));
        assertSame(workspace.getVan(), args("van").getList(0, workspace));
        assertSame(workspace.getVan(), args("фургон").getList(0, workspace));
    }

    @Test
    @DisplayName("пояснює, які списки існують")
    void rejectsUnknownList() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> args("warehouse").getList(0, workspace));

        assertTrue(e.getMessage().contains("catalog, van"));
    }
}
