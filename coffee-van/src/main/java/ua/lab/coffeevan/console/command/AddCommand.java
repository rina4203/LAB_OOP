/*
 * AddCommand.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.console.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ua.lab.coffeevan.io.CoffeeForm;
import ua.lab.coffeevan.io.CoffeeLineFormat;
import ua.lab.coffeevan.io.ValueParser;
import ua.lab.coffeevan.model.Coffee;
import ua.lab.coffeevan.model.CoffeeVariety;
import ua.lab.coffeevan.model.DisplayNamed;
import ua.lab.coffeevan.model.Quality;
import ua.lab.coffeevan.van.VanWorkspace;

/**
 * Команда {@code add}: додає товар у каталог.
 *
 * <p>Товар можна ввести одним рядком у форматі каталогу або,
 * якщо рядок не вказано, покроково - програма запитає кожне поле
 * й підкаже допустимі значення.
 *
 * @author rina4203
 * @version 1.0
 */
public class AddCommand extends AbstractCommand {

    private static final String SCORE_HINT =
            Quality.MIN_SCORE + "-" + Quality.MAX_SCORE;

    private final VanWorkspace workspace;
    private final CoffeeLineFormat format;
    private final BufferedReader input;
    private final PrintStream out;

    /**
     * Створює команду.
     *
     * @param workspace робоче місце
     * @param format    формат рядка каталогу
     * @param input     джерело відповідей для покрокового введення
     * @param out       потік виведення
     */
    public AddCommand(VanWorkspace workspace, CoffeeLineFormat format,
            BufferedReader input, PrintStream out) {
        super("add", "add [<рядок каталогу>]",
                "додати товар у каталог: рядок стан;назва;сорт;вага;ціна;"
                        + "упаковка;аромат;кислотність;тіло;особливість "
                        + "або без рядка - покроково");
        this.workspace = Objects.requireNonNull(workspace);
        this.format = Objects.requireNonNull(format);
        this.input = Objects.requireNonNull(input);
        this.out = Objects.requireNonNull(out);
    }

    @Override
    public void execute(Arguments arguments) throws IOException {
        Coffee coffee = arguments.isEmpty() ? askFields()
                : format.parse(arguments.getRest(0));
        workspace.getCatalog().add(coffee);
        out.println("Додано в каталог під №" + workspace.getCatalog().size()
                + ": " + coffee);
    }

    private Coffee askFields() throws IOException {
        out.println("Введіть характеристики товару "
                + "(у квадратних дужках - допустимі значення):");
        CoffeeForm form = ValueParser.parseEnum(CoffeeForm.class,
                ask("Фізичний стан", hint(List.of(CoffeeForm.values()))),
                "фізичний стан");
        String name = ask("Назва", null);
        String variety = ask("Сорт", hint(List.of(CoffeeVariety.values())));
        String weight = ask("Вага без упаковки, г", null);
        String price = ask("Ціна, грн", null);
        String packaging = ask("Упаковка", hint(form.getAllowedPackaging()));
        String aroma = ask("Аромат", SCORE_HINT);
        String acidity = ask("Кислотність", SCORE_HINT);
        String body = ask("Тіло", SCORE_HINT);
        String feature = ask(capitalize(form.getFeatureLabel()),
                hint(form.getFeatureValues()));
        return format.parse(String.join(CoffeeLineFormat.SEPARATOR,
                form.name(), name, variety, weight, price, packaging,
                aroma, acidity, body, feature));
    }

    private String ask(String label, String hint) throws IOException {
        out.print((hint == null) ? label + ": "
                : label + " [" + hint + "]: ");
        out.flush();
        String answer = input.readLine();
        if (answer == null) {
            throw new IOException("введення перервано, товар не додано");
        }
        if (answer.contains(CoffeeLineFormat.SEPARATOR)) {
            throw new IllegalArgumentException("значення не може містити "
                    + "символ \"" + CoffeeLineFormat.SEPARATOR + "\"");
        }
        return answer.trim();
    }

    private static String hint(List<? extends DisplayNamed> values) {
        return values.stream()
                .map(value -> value.name() + " - " + value.getDisplayName())
                .collect(Collectors.joining(", "));
    }
}
