/*
 * RemoveCommand.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.console.command;

import java.io.PrintStream;
import java.util.List;
import java.util.Objects;

import ua.lab.coffeevan.model.Coffee;
import ua.lab.coffeevan.stock.CoffeeCollection;
import ua.lab.coffeevan.van.VanWorkspace;

/**
 * Команда {@code remove}: остаточно видаляє товар чи всі товари
 * з каталогу або фургона.
 *
 * @author rina4203
 * @version 1.0
 */
public class RemoveCommand extends AbstractCommand {

    private final VanWorkspace workspace;
    private final PrintStream out;

    /**
     * Створює команду.
     *
     * @param workspace робоче місце
     * @param out       потік виведення
     */
    public RemoveCommand(VanWorkspace workspace, PrintStream out) {
        super("remove", "remove <catalog|van> <№|all>",
                "видалити товар (або всі товари) зі списку");
        this.workspace = Objects.requireNonNull(workspace);
        this.out = Objects.requireNonNull(out);
    }

    @Override
    public void execute(Arguments arguments) {
        requireCount(arguments, 2, 2);
        CoffeeCollection list = arguments.getList(0, workspace);
        if (arguments.isAll(1)) {
            List<Coffee> removed = list.removeAll();
            out.println("Видалено всі товари зі списку \""
                    + list.getDisplayName() + "\": " + removed.size());
        } else {
            Coffee removed = list.remove(arguments.getItemIndex(1));
            out.println("Видалено: " + removed);
        }
    }
}
