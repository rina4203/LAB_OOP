/*
 * AddCommandTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.console.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ua.lab.coffeevan.io.CoffeeLineFormat;
import ua.lab.coffeevan.model.Coffee;
import ua.lab.coffeevan.model.GrindSize;
import ua.lab.coffeevan.model.GroundCoffee;

@DisplayName("Команда add")
class AddCommandTest extends CommandTestSupport {

    private static final String GROUND_ANSWERS = String.join("\n",
            "ground", "My Ground", "robusta", "250", "199,90", "tin_can",
            "5", "3", "8", "fine");

    @Test
    @DisplayName("додає товар, введений одним рядком")
    void addsLine() throws IOException {
        command("").execute(args("instant; Моя кава ;arabica;100;150;"
                + "glass_jar;7;5;6;freeze_dried"));

        assertEquals(4, workspace.getCatalog().size());
        assertEquals("Моя кава", workspace.getCatalog().get(3).getName());
        assertTrue(output().contains("Додано в каталог під №4: Моя кава"));
    }

    @Test
    @DisplayName("без рядка запитує поля по черзі з підказками")
    void asksFieldsStepByStep() throws IOException {
        command(GROUND_ANSWERS).execute(args(""));

        Coffee added = workspace.getCatalog().get(3);
        GroundCoffee ground = assertInstanceOf(GroundCoffee.class, added);
        assertEquals("My Ground", ground.getName());
        assertEquals(new BigDecimal("199.90"), ground.getPrice());
        assertEquals(GrindSize.FINE, ground.getGrindSize());
        String text = output();
        assertTrue(text.contains("Фізичний стан [BEANS - у зернах, "
                + "GROUND - мелена, INSTANT - розчинна]: "));
        assertTrue(text.contains("Упаковка [PAPER_BAG - паперовий пакет, "
                + "VACUUM_PACK - вакуумна упаковка, "
                + "TIN_CAN - бляшана банка]: "));
        assertTrue(text.contains("Помел [FINE - дрібний"));
        assertTrue(text.contains("Аромат [0-10]: "));
        assertTrue(text.contains("Назва: "));
    }

    @Test
    @DisplayName("одразу зупиняється на невідомому фізичному стані")
    void stopsOnUnknownState() {
        AddCommand command = command("liquid\nName");
        Arguments empty = args("");

        assertThrows(IllegalArgumentException.class,
                () -> command.execute(empty));
        assertEquals(3, workspace.getCatalog().size());
    }

    @Test
    @DisplayName("повідомляє, якщо введення перервалося")
    void reportsInterruptedInput() {
        AddCommand command = command("beans\nName");
        Arguments empty = args("");

        IOException e = assertThrows(IOException.class,
                () -> command.execute(empty));

        assertTrue(e.getMessage().contains("товар не додано"));
        assertEquals(3, workspace.getCatalog().size());
    }

    @Test
    @DisplayName("не приймає відповідь з крапкою з комою")
    void rejectsSeparatorInAnswer() {
        AddCommand command = command("beans\nName;Other");
        Arguments empty = args("");

        assertThrows(IllegalArgumentException.class,
                () -> command.execute(empty));
    }

    @Test
    @DisplayName("не додає некоректний товар")
    void rejectsInvalidLine() {
        AddCommand command = command("");
        Arguments line = args("BEANS;Name;ARABICA;0;100;PAPER_BAG;5;5;5;DARK");

        assertThrows(IllegalArgumentException.class,
                () -> command.execute(line));
        assertEquals(3, workspace.getCatalog().size());
    }

    private AddCommand command(String answers) {
        return new AddCommand(workspace, new CoffeeLineFormat(),
                new BufferedReader(new StringReader(answers)), out);
    }
}
