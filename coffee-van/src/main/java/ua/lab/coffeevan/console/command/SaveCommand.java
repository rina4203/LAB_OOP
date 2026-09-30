/*
 * SaveCommand.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.console.command;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Objects;

import ua.lab.coffeevan.io.CoffeeCatalogWriter;
import ua.lab.coffeevan.io.DataSources;
import ua.lab.coffeevan.stock.CoffeeCollection;
import ua.lab.coffeevan.van.VanWorkspace;

/**
 * Команда {@code save}: зберігає каталог або вантаж фургона у файл
 * формату каталогу, щоб наступного разу запустити програму з ним.
 *
 * @author rina4203
 * @version 1.0
 */
public class SaveCommand extends AbstractCommand {

    private final VanWorkspace workspace;
    private final CoffeeCatalogWriter writer;
    private final PrintStream out;

    /**
     * Створює команду.
     *
     * @param workspace робоче місце
     * @param writer    засіб запису каталогу
     * @param out       потік виведення
     */
    public SaveCommand(VanWorkspace workspace, CoffeeCatalogWriter writer,
            PrintStream out) {
        super("save", "save <catalog|van> <файл>",
                "зберегти список у файл формату каталогу (.csv)");
        this.workspace = Objects.requireNonNull(workspace);
        this.writer = Objects.requireNonNull(writer);
        this.out = Objects.requireNonNull(out);
    }

    @Override
    public void execute(Arguments arguments) throws IOException {
        requireCount(arguments, 2, ANY_COUNT);
        CoffeeCollection list = arguments.getList(0, workspace);
        Path path = Path.of(arguments.getRest(1));
        try (Writer file = DataSources.createFile(path)) {
            writer.write(file, list.getItems());
        }
        out.println("Збережено товарів: " + list.size() + " у файл "
                + path.toAbsolutePath());
    }
}
