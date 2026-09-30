/*
 * LoadCommandTest.java
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ua.lab.coffeevan.exception.CargoLoadingException;
import ua.lab.coffeevan.report.CargoReportPrinter;
import ua.lab.coffeevan.van.CoffeeVan;
import ua.lab.coffeevan.van.VanWorkspace;

@DisplayName("Команда load")
class LoadCommandTest extends CommandTestSupport {

    private final LoadCommand command =
            new LoadCommand(workspace, printer, out);

    @Test
    @DisplayName("переносить товар з каталогу у фургон за номером")
    void loadsOneItem() {
        command.execute(args("2"));

        assertEquals(List.of("Kenya AA"), names(workspace.getVan()));
        assertEquals(2, workspace.getCatalog().size());
        assertTrue(output().contains("Завантажено у фургон під №1: Kenya AA"));
        assertTrue(output().contains("=== Стан фургона ==="));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "all", "всі"})
    @DisplayName("без номера завантажує все, що вміщується")
    void loadsAllThatFit(String text) {
        command.execute(args(text));

        assertEquals(List.of("Colombia Supremo", "Kenya AA"),
                names(workspace.getVan()));
        assertTrue(output().contains("У каталозі залишилося товарів: 1"));
    }

    @Test
    @DisplayName("не завантажує товар, на який бракує бюджету")
    void refusesItemOverBudget() {
        command.execute(args("1"));
        command.execute(args("1"));
        Arguments gold = args("1");

        assertThrows(CargoLoadingException.class,
                () -> command.execute(gold));
        assertEquals(List.of("Gold Arabica"), names(workspace.getCatalog()));
    }

    @Test
    @DisplayName("передає робочому місцю позицію товару (з нуля)")
    void passesIndexToWorkspace() {
        VanWorkspace mockedWorkspace = mock(VanWorkspace.class);
        CargoReportPrinter mockedPrinter = mock(CargoReportPrinter.class);
        CoffeeVan van = mock(CoffeeVan.class);
        when(mockedWorkspace.loadFromCatalog(2)).thenReturn(gold);
        when(mockedWorkspace.getVan()).thenReturn(van);
        when(van.size()).thenReturn(1);

        new LoadCommand(mockedWorkspace, mockedPrinter, out)
                .execute(args("3"));

        verify(mockedWorkspace).loadFromCatalog(2);
        verify(mockedPrinter).printVanState(van);
    }

    @Test
    @DisplayName("приймає не більше одного аргументу")
    void rejectsExtraArguments() {
        Arguments arguments = args("1 2");

        assertThrows(IllegalArgumentException.class,
                () -> command.execute(arguments));
    }
}
