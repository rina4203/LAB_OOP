/*
 * CoffeeCatalogWriterTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ua.lab.coffeevan.TestCoffees;
import ua.lab.coffeevan.model.Coffee;

@DisplayName("Запис каталогу у файл")
class CoffeeCatalogWriterTest {

    private final CoffeeCatalogWriter writer = new CoffeeCatalogWriter();
    private final CoffeeLineFormat format = new CoffeeLineFormat();

    @Test
    @DisplayName("записує заголовок і по одному товару в рядку")
    void writesHeaderAndLines() throws IOException {
        StringWriter out = new StringWriter();

        writer.write(out, List.of(TestCoffees.ground("Kenya AA", "420")));

        String[] lines = out.toString().split("\\R");
        assertEquals(CoffeeCatalogWriter.HEADER, lines[0]);
        assertEquals("GROUND;Kenya AA;ARABICA;250;420.00;PAPER_BAG;9;9;6;"
                + "MEDIUM", lines[1]);
        assertEquals(2, lines.length);
    }

    @Test
    @DisplayName("записаний каталог читається назад без втрат")
    void writtenCatalogCanBeRead() throws IOException {
        List<Coffee> items = List.of(TestCoffees.beans("Colombia", "1150"),
                TestCoffees.ground("Kenya AA", "420"),
                TestCoffees.instant("Gold Arabica", "340"));
        StringWriter out = new StringWriter();

        writer.write(out, items);
        List<Coffee> read = new CoffeeCatalogReader()
                .read(new StringReader(out.toString()));

        assertEquals(lines(items), lines(read));
    }

    @Test
    @DisplayName("для порожнього списку записує лише заголовок")
    void writesOnlyHeaderForEmptyList() throws IOException {
        StringWriter out = new StringWriter();

        writer.write(out, List.of());

        assertEquals(CoffeeCatalogWriter.HEADER, out.toString().strip());
    }

    private List<String> lines(List<Coffee> coffees) {
        return coffees.stream().map(format::format)
                .collect(Collectors.toList());
    }
}
