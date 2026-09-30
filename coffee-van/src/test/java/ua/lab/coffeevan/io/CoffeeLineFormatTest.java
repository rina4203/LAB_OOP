/*
 * CoffeeLineFormatTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import ua.lab.coffeevan.TestCoffees;
import ua.lab.coffeevan.model.Coffee;
import ua.lab.coffeevan.model.GroundCoffee;
import ua.lab.coffeevan.model.Quality;

@DisplayName("Текстовий формат товару")
class CoffeeLineFormatTest {

    private static final String KENYA_LINE =
            "GROUND;Kenya AA;ARABICA;250;420.00;PAPER_BAG;9;9;6;MEDIUM";

    private final CoffeeLineFormat format = new CoffeeLineFormat();

    @Test
    @DisplayName("розбирає рядок каталогу")
    void parsesLine() {
        Coffee coffee = format.parse(" ground ; Kenya AA ; arabica ; 250 ;"
                + " 420,00 ; paper_bag ; 9 ; 9 ; 6 ; medium ");

        assertInstanceOf(GroundCoffee.class, coffee);
        assertEquals("Kenya AA", coffee.getName());
        assertEquals(new BigDecimal("420.00"), coffee.getPrice());
        assertEquals(new Quality(9, 9, 6), coffee.getQuality());
    }

    @Test
    @DisplayName("записує товар у рядок каталогу")
    void formatsCoffee() {
        assertEquals(KENYA_LINE,
                format.format(TestCoffees.ground("Kenya AA", "420")));
    }

    @Test
    @DisplayName("записаний рядок розбирається в такий самий товар")
    void roundTrips() {
        Coffee original = TestCoffees.instant("Gold Arabica", "340");

        Coffee copy = format.parse(format.format(original));

        assertEquals(format.format(original), format.format(copy));
    }

    @Test
    @DisplayName("повідомляє про неправильну кількість полів")
    void reportsFieldCount() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> format.parse("BEANS;Short;ARABICA"));

        assertEquals("очікується 10 полів, знайдено 3", e.getMessage());
    }

    @Test
    @DisplayName("вимагає рядок")
    void requiresLine() {
        assertThrows(NullPointerException.class, () -> format.parse(null));
    }

    @ParameterizedTest
    @CsvSource({
            "price, 500, 500",
            "ціна, 499.90, 499.90",
            "PRICE, '12,5', 12.5"
    })
    @DisplayName("змінює ціну за англійською чи українською назвою поля")
    void changesPrice(String field, String value, String expected) {
        Coffee changed = format.change(TestCoffees.ground("Kenya AA", "420"),
                field, value);

        assertEquals(new BigDecimal(expected).setScale(2),
                changed.getPrice());
        assertEquals("Kenya AA", changed.getName());
    }

    @Test
    @DisplayName("змінює назву з пробілами")
    void changesName() {
        Coffee changed = format.change(TestCoffees.ground("Kenya AA", "420"),
                "name", "Kenya AA Top Lot");

        assertEquals("Kenya AA Top Lot", changed.getName());
    }

    @Test
    @DisplayName("не приймає значення з крапкою з комою")
    void rejectsSeparatorInValue() {
        Coffee coffee = TestCoffees.ground("Kenya AA", "420");

        assertThrows(IllegalArgumentException.class,
                () -> format.change(coffee, "name", "Kenya;AA"));
    }

    @Test
    @DisplayName("перевіряє нове значення так само, як під час читання")
    void validatesChangedValue() {
        Coffee coffee = TestCoffees.ground("Kenya AA", "420");

        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> format.change(coffee, "packaging", "GLASS_JAR"));

        assertTrue(e.getMessage().contains("скляна банка"));
    }

    @ParameterizedTest
    @CsvSource({"state, 0", "особливість, 9", "  Body , 8"})
    @DisplayName("знаходить поле за назвою")
    void findsFieldIndex(String field, int expected) {
        assertEquals(expected, CoffeeLineFormat.fieldIndex(field));
    }

    @ParameterizedTest
    @ValueSource(strings = {"colour", "колір"})
    @DisplayName("пояснює, які поля існують")
    void rejectsUnknownField(String field) {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> CoffeeLineFormat.fieldIndex(field));

        assertTrue(e.getMessage().contains("state, name, variety"));
    }
}
