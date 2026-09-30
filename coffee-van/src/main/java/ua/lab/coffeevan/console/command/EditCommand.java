/*
 * EditCommand.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.console.command;

import java.io.PrintStream;
import java.util.Objects;

import ua.lab.coffeevan.io.CoffeeLineFormat;
import ua.lab.coffeevan.model.Coffee;
import ua.lab.coffeevan.stock.CoffeeCollection;
import ua.lab.coffeevan.van.VanWorkspace;

/**
 * Команда {@code edit}: змінює одне поле товару в каталозі чи фургоні.
 *
 * <p>Товари незмінні, тому замість старого товару в списку з'являється
 * новий зі зміненим полем. Фургон перевіряє, чи новий товар
 * вміщується за об'ємом і бюджетом.
 *
 * @author rina4203
 * @version 1.0
 */
public class EditCommand extends AbstractCommand {

    private static final int VALUE_POSITION = 3;

    private final VanWorkspace workspace;
    private final CoffeeLineFormat format;
    private final PrintStream out;

    /**
     * Створює команду.
     *
     * @param workspace робоче місце
     * @param format    формат рядка каталогу
     * @param out       потік виведення
     */
    public EditCommand(VanWorkspace workspace, CoffeeLineFormat format,
            PrintStream out) {
        super("edit", "edit <catalog|van> <№> <поле> <значення>",
                "змінити поле товару; поля: "
                        + String.join(", ", CoffeeLineFormat.FIELD_NAMES));
        this.workspace = Objects.requireNonNull(workspace);
        this.format = Objects.requireNonNull(format);
        this.out = Objects.requireNonNull(out);
    }

    @Override
    public void execute(Arguments arguments) {
        requireCount(arguments, VALUE_POSITION + 1, ANY_COUNT);
        CoffeeCollection list = arguments.getList(0, workspace);
        int index = arguments.getItemIndex(1);
        Coffee original = list.get(index);
        Coffee changed = format.change(original, arguments.get(2),
                arguments.getRest(VALUE_POSITION));
        list.replace(index, changed);
        out.println("Було:  " + original);
        out.println("Стало: " + changed);
    }
}
