/*
 * DataSourcesTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@DisplayName("Джерела даних")
class DataSourcesTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("відкриває файл у кодуванні UTF-8")
    void opensFileAsUtf8() throws IOException {
        Path file = tempDir.resolve("catalog.csv");
        Files.writeString(file, "Кава в зернах", StandardCharsets.UTF_8);

        try (BufferedReader reader =
                new BufferedReader(DataSources.openFile(file))) {
            assertEquals("Кава в зернах", reader.readLine());
        }
    }

    @Test
    @DisplayName("повідомляє про відсутній файл")
    void failsOnMissingFile() {
        Path missing = tempDir.resolve("missing.csv");

        assertThrows(NoSuchFileException.class,
                () -> DataSources.openFile(missing));
    }

    @Test
    @DisplayName("відкриває вбудований ресурс")
    void opensBundledResource() throws IOException {
        try (BufferedReader reader = new BufferedReader(
                DataSources.openResource("van.properties"))) {
            assertTrue(reader.lines()
                    .anyMatch(line -> line.startsWith("van.capacity.liters")));
        }
    }

    @Test
    @DisplayName("повідомляє про відсутній ресурс")
    void failsOnMissingResource() {
        FileNotFoundException e = assertThrows(FileNotFoundException.class,
                () -> {
                    try (Reader reader =
                            DataSources.openResource("missing.csv")) {
                        reader.read();
                    }
                });
        assertTrue(e.getMessage().contains("missing.csv"));
    }
}
