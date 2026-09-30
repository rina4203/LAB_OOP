/*
 * ScenariosTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import ua.lab.coffeevan.config.VanConfigReader;
import ua.lab.coffeevan.exception.InvalidDataException;
import ua.lab.coffeevan.io.CoffeeCatalogReader;
import ua.lab.coffeevan.io.DataSources;
import ua.lab.coffeevan.report.CargoReportPrinter;
import ua.lab.coffeevan.van.CoffeeVan;
import ua.lab.coffeevan.van.VanLoader;

@DisplayName("Готові сценарії запуску (теки scenarios)")
class ScenariosTest {

    private static final Path MODULE_DIR =
            Files.isDirectory(Path.of("scenarios")) ? Path.of("")
                    : Path.of("coffee-van");
    private static final Path SCENARIOS = MODULE_DIR.resolve("scenarios");
    private static final Path DEFAULT_DATA =
            MODULE_DIR.resolve("src/main/resources");
    private static final String ERROR_PREFIX = "error-";
    private static final String CONFIG_FILE = "van.properties";
    private static final String CATALOG_FILE = "coffee-catalog.csv";
    private static final String ABOUT_FILE = "about.txt";

    private final CoffeeVanApplication application = new CoffeeVanApplication(
            new VanConfigReader(), new CoffeeCatalogReader(), new VanLoader(),
            new CargoReportPrinter(new PrintStream(
                    new ByteArrayOutputStream(), true,
                    StandardCharsets.UTF_8)));

    static List<String> scenarios() throws IOException {
        try (Stream<Path> entries = Files.list(SCENARIOS)) {
            return entries.filter(Files::isDirectory)
                    .map(path -> path.getFileName().toString())
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("scenarios")
    @DisplayName("сценарій має опис")
    void hasDescription(String scenario) {
        assertTrue(Files.isRegularFile(
                SCENARIOS.resolve(scenario).resolve(ABOUT_FILE)));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("scenarios")
    @DisplayName("звичайний сценарій завантажує фургон, "
            + "сценарій error-* повідомляє про помилку")
    void behavesAsDescribed(String scenario) throws IOException {
        try (Reader config = DataSources.openFile(
                        resolve(scenario, CONFIG_FILE));
                Reader catalog = DataSources.openFile(
                        resolve(scenario, CATALOG_FILE))) {
            if (scenario.startsWith(ERROR_PREFIX)) {
                assertThrows(InvalidDataException.class,
                        () -> application.run(config, catalog));
            } else {
                CoffeeVan van = application.run(config, catalog);
                assertFalse(van.getCargo().isEmpty());
            }
        }
    }

    private static Path resolve(String scenario, String fileName) {
        Path own = SCENARIOS.resolve(scenario).resolve(fileName);
        return Files.exists(own) ? own : DEFAULT_DATA.resolve(fileName);
    }
}
