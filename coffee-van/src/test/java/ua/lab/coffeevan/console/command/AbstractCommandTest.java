/*
 * AbstractCommandTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.console.command;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Основа команд")
class AbstractCommandTest {

    private final AbstractCommand command = new AbstractCommand("demo",
            "demo <a> [b]", "демонстраційна команда") {
        @Override
        public void execute(Arguments arguments) {
            requireCount(arguments, 1, 2);
        }
    };

    @Test
    @DisplayName("зберігає назву, підказку й опис")
    void storesDescription() {
        assertEquals("demo", command.getName());
        assertEquals("demo <a> [b]", command.getUsage());
        assertEquals("демонстраційна команда", command.getDescription());
    }

    @Test
    @DisplayName("приймає кількість аргументів у межах")
    void acceptsCountWithinLimits() {
        assertDoesNotThrow(() -> command.execute(new Arguments("a")));
        assertDoesNotThrow(() -> command.execute(new Arguments("a b")));
    }

    @Test
    @DisplayName("за неправильної кількості аргументів показує підказку")
    void showsUsageOnWrongCount() {
        IllegalArgumentException tooFew = assertThrows(
                IllegalArgumentException.class,
                () -> command.execute(new Arguments("")));
        IllegalArgumentException tooMany = assertThrows(
                IllegalArgumentException.class,
                () -> command.execute(new Arguments("a b c")));

        assertEquals("використання: demo <a> [b]", tooFew.getMessage());
        assertEquals(tooFew.getMessage(), tooMany.getMessage());
    }

    @Test
    @DisplayName("робить першу літеру великою")
    void capitalizes() {
        assertEquals("Фургон", AbstractCommand.capitalize("фургон"));
        assertEquals("", AbstractCommand.capitalize(""));
    }
}
