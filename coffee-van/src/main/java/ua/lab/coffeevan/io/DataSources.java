/*
 * DataSources.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Відкриває текстові файли та ресурси програми для читання й запису
 * у кодуванні UTF-8.
 *
 * @author rina4203
 * @version 1.0
 */
public final class DataSources {

    private DataSources() {
    }

    /**
     * Відкриває файл, шлях до якого переданий аргументом командного
     * рядка, а якщо такого аргументу немає - вбудований ресурс.
     *
     * @param args            аргументи командного рядка
     * @param index           номер аргументу зі шляхом до файлу
     * @param defaultResource ім'я ресурсу на випадок, коли аргументу немає
     * @return потік читання
     * @throws IOException якщо файл чи ресурс неможливо відкрити
     */
    public static Reader open(String[] args, int index,
            String defaultResource) throws IOException {
        if (args.length > index) {
            return openFile(Path.of(args[index]));
        }
        return openResource(defaultResource);
    }

    /**
     * Відкриває файл з файлової системи.
     *
     * @param path шлях до файлу
     * @return потік читання файлу
     * @throws IOException якщо файл неможливо відкрити
     */
    public static Reader openFile(Path path) throws IOException {
        return Files.newBufferedReader(path, StandardCharsets.UTF_8);
    }

    /**
     * Створює (або перезаписує) файл для запису.
     *
     * @param path шлях до файлу
     * @return потік запису у файл
     * @throws IOException якщо файл неможливо створити
     */
    public static Writer createFile(Path path) throws IOException {
        return Files.newBufferedWriter(path, StandardCharsets.UTF_8);
    }

    /**
     * Відкриває ресурс, вбудований у програму (з classpath).
     *
     * @param resourceName ім'я ресурсу, наприклад {@code "van.properties"}
     * @return потік читання ресурсу
     * @throws FileNotFoundException якщо ресурс не знайдено
     */
    public static Reader openResource(String resourceName)
            throws FileNotFoundException {
        InputStream stream = DataSources.class.getClassLoader()
                .getResourceAsStream(resourceName);
        if (stream == null) {
            throw new FileNotFoundException(
                    "Ресурс \"" + resourceName + "\" не знайдено");
        }
        return new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8));
    }
}
