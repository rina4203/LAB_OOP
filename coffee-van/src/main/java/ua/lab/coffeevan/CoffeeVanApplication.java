/*
 * CoffeeVanApplication.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import ua.lab.coffeevan.config.VanConfig;
import ua.lab.coffeevan.config.VanConfigReader;
import ua.lab.coffeevan.io.CoffeeCatalogReader;
import ua.lab.coffeevan.io.DataSources;
import ua.lab.coffeevan.model.Coffee;
import ua.lab.coffeevan.model.QualityRange;
import ua.lab.coffeevan.report.CargoReportPrinter;
import ua.lab.coffeevan.van.CoffeeVan;
import ua.lab.coffeevan.van.VanLoader;

/**
 * Консольна програма "Фургон кави".
 *
 * <p>Зчитує параметри фургона та каталог кави з файлів, завантажує фургон
 * товарами в межах об'єму та бюджету, сортує вантаж за співвідношенням
 * ціни та ваги й знаходить товари із заданим діапазоном якості.
 *
 * <p>Запуск: {@code java -jar coffee-van.jar [файл_параметрів [каталог]]}.
 * Якщо шляхи до файлів не вказано, використовуються вбудовані
 * {@value #DEFAULT_CONFIG} та {@value #DEFAULT_CATALOG}.
 *
 * @author rina4203
 * @version 1.0
 */
public class CoffeeVanApplication {

    /** Ім'я вбудованого файлу з параметрами фургона. */
    public static final String DEFAULT_CONFIG = "van.properties";

    /** Ім'я вбудованого файлу з каталогом кави. */
    public static final String DEFAULT_CATALOG = "coffee-catalog.csv";

    /** Заголовок таблиці товарів, що не вмістилися у фургон. */
    public static final String REJECTED_TITLE =
            "Товари, що не вмістилися у фургон";

    /** Заголовок таблиці відсортованого вантажу. */
    public static final String SORTED_TITLE =
            "Вантаж, відсортований за ціною кілограма кави";

    /** Початок заголовка таблиці результатів пошуку. */
    public static final String SEARCH_TITLE = "Товари з якістю: ";

    private static final int CONFIG_ARGUMENT = 0;
    private static final int CATALOG_ARGUMENT = 1;

    private final VanConfigReader configReader;
    private final CoffeeCatalogReader catalogReader;
    private final VanLoader vanLoader;
    private final CargoReportPrinter printer;

    /**
     * Створює програму з усіма її складовими.
     *
     * @param configReader  засіб читання параметрів фургона
     * @param catalogReader засіб читання каталогу кави
     * @param vanLoader     вантажник фургона
     * @param printer       засіб виведення звітів
     */
    public CoffeeVanApplication(VanConfigReader configReader,
            CoffeeCatalogReader catalogReader, VanLoader vanLoader,
            CargoReportPrinter printer) {
        this.configReader = Objects.requireNonNull(configReader);
        this.catalogReader = Objects.requireNonNull(catalogReader);
        this.vanLoader = Objects.requireNonNull(vanLoader);
        this.printer = Objects.requireNonNull(printer);
    }

    /**
     * Точка входу в програму.
     *
     * @param args необов'язкові шляхи до файлу параметрів та каталогу
     */
    public static void main(String[] args) {
        CoffeeVanApplication application = new CoffeeVanApplication(
                new VanConfigReader(), new CoffeeCatalogReader(),
                new VanLoader(), new CargoReportPrinter(System.out));
        try (Reader config = openSource(args, CONFIG_ARGUMENT, DEFAULT_CONFIG);
                Reader catalog =
                        openSource(args, CATALOG_ARGUMENT, DEFAULT_CATALOG)) {
            application.run(config, catalog);
        } catch (IOException e) {
            System.err.println("Помилка: " + e.getMessage());
        }
    }

    /**
     * Виконує повний сценарій: завантаження фургона, сортування вантажу
     * та пошук товарів за якістю з виведенням результатів.
     *
     * @param configSource  джерело параметрів фургона
     * @param catalogSource джерело каталогу кави
     * @return завантажений фургон
     * @throws IOException якщо дані неможливо прочитати або вони некоректні
     */
    public CoffeeVan run(Reader configSource, Reader catalogSource)
            throws IOException {
        VanConfig config = configReader.read(configSource);
        List<Coffee> catalog = catalogReader.read(catalogSource);

        CoffeeVan van = new CoffeeVan(config.getCapacityLiters(),
                config.getBudget());
        List<Coffee> rejected = vanLoader.load(van, catalog);
        printer.printLoadingSummary(van, rejected);
        printer.printItems(REJECTED_TITLE, rejected);

        van.sortCargoByPricePerKilogram();
        printer.printItems(SORTED_TITLE, van.getCargo());

        QualityRange range = config.getSearchRange();
        printer.printItems(SEARCH_TITLE + range, van.findByQuality(range));
        return van;
    }

    private static Reader openSource(String[] args, int index,
            String defaultResource) throws IOException {
        if (args.length > index) {
            return DataSources.openFile(Path.of(args[index]));
        }
        return DataSources.openResource(defaultResource);
    }
}
