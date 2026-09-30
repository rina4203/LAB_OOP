/*
 * SaveCommandTest.java
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

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ua.lab.coffeevan.io.CoffeeCatalogReader;
import ua.lab.coffeevan.io.CoffeeCatalogWriter;
import ua.lab.coffeevan.io.DataSources;
import ua.lab.coffeevan.model.Coffee;

@DisplayName("Команда save")
class SaveCommandTest extends CommandTestSupport {

    @TempDir
    Path tempDir;

    private final SaveCommand command =
            new SaveCommand(workspace, new CoffeeCatalogWriter(), out);

    @Test
    @DisplayName("зберігає каталог у файл, який потім можна прочитати")
    void savesCatalog() throws IOException {
        Path file = tempDir.resolve("мій каталог.csv");

        command.execute(args("catalog " + file));

        List<Coffee> read;
        try (Reader reader = DataSources.openFile(file)) {
            read = new CoffeeCatalogReader().read(reader);
        }
        assertEquals(3, read.size());
        assertEquals("Gold Arabica", read.get(2).getName());
        assertTrue(output().contains("Збережено товарів: 3 у файл "));
    }

    @Test
    @DisplayName("зберігає вантаж фургона")
    void savesVan() throws IOException {
        workspace.loadFromCatalog(1);
        Path file = tempDir.resolve("van.csv");

        command.execute(args("van " + file));

        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        assertEquals(2, lines.size());
        assertTrue(lines.get(1).startsWith("GROUND;Kenya AA;"));
    }

    @Test
    @DisplayName("вимагає список і шлях до файлу")
    void requiresListAndPath() {
        Arguments missingPath = args("catalog");

        assertThrows(IllegalArgumentException.class,
                () -> command.execute(missingPath));
    }

    @Test
    @DisplayName("повідомляє, якщо файл неможливо створити")
    void reportsUnwritablePath() {
        Arguments folderMissing = args("catalog "
                + tempDir.resolve("немає/такої/теки/file.csv"));

        assertThrows(IOException.class,
                () -> command.execute(folderMissing));
    }
}
