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
import java.io.Writer;
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
    @DisplayName("відкриває файл з аргументу командного рядка")
    void opensFileFromArguments() throws IOException {
        Path file = tempDir.resolve("van.properties");
        Files.writeString(file, "з файлу", StandardCharsets.UTF_8);
        String[] args = {file.toString()};

        try (BufferedReader reader = new BufferedReader(
                DataSources.open(args, 0, "coffee-catalog.csv"))) {
            assertEquals("з файлу", reader.readLine());
        }
    }

    @Test
    @DisplayName("без аргументу відкриває вбудований ресурс")
    void opensResourceWithoutArgument() throws IOException {
        String[] args = {"інший-файл.properties"};

        try (BufferedReader reader = new BufferedReader(
                DataSources.open(args, 1, "van.properties"))) {
            assertTrue(reader.lines()
                    .anyMatch(line -> line.startsWith("van.budget")));
        }
    }

    @Test
    @DisplayName("створює файл і записує в нього текст у UTF-8")
    void createsFileAsUtf8() throws IOException {
        Path file = tempDir.resolve("saved.csv");

        try (Writer writer = DataSources.createFile(file)) {
            writer.write("Кава");
        }

        assertEquals("Кава", Files.readString(file, StandardCharsets.UTF_8));
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
