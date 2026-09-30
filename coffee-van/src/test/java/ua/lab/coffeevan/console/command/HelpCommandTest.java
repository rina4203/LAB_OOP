/*
 * HelpCommandTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.console.command;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Команда help")
class HelpCommandTest extends CommandTestSupport {

    @Mock
    private Command other;

    @Test
    @DisplayName("перелічує всі команди з описами та команду exit")
    void listsCommands() {
        when(other.getUsage()).thenReturn("other <x>");
        when(other.getDescription()).thenReturn("інша команда");
        List<Command> commands = new ArrayList<>();
        HelpCommand help = new HelpCommand(commands, out);
        commands.add(help);
        commands.add(other);

        help.execute(args(""));

        String text = output();
        assertTrue(text.contains("  help       показати перелік команд"));
        assertTrue(text.contains("  other <x>  інша команда"));
        assertTrue(text.contains("  exit       вийти з програми"));
    }

    @Test
    @DisplayName("працює навіть без інших команд")
    void worksWithoutCommands() {
        new HelpCommand(List.of(), out).execute(args(""));

        assertTrue(output().contains("  exit  вийти з програми"));
    }

    @Test
    @DisplayName("не приймає аргументів")
    void rejectsArguments() {
        HelpCommand help = new HelpCommand(List.of(), out);
        Arguments arguments = args("load");

        assertThrows(IllegalArgumentException.class,
                () -> help.execute(arguments));
    }
}
