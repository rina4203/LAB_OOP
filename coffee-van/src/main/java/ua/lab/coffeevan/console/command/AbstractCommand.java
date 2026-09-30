/*
 * AbstractCommand.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.console.command;

/**
 * Основа команд: зберігає назву, підказку й опис команди та перевіряє
 * кількість аргументів.
 *
 * @author rina4203
 * @version 1.0
 */
public abstract class AbstractCommand implements Command {

    /** Позначення необмеженої кількості аргументів. */
    protected static final int ANY_COUNT = Integer.MAX_VALUE;

    private final String name;
    private final String usage;
    private final String description;

    /**
     * Ініціалізує опис команди.
     *
     * @param name        назва команди
     * @param usage       підказка щодо аргументів
     * @param description опис українською мовою
     */
    protected AbstractCommand(String name, String usage, String description) {
        this.name = name;
        this.usage = usage;
        this.description = description;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final String getUsage() {
        return usage;
    }

    @Override
    public final String getDescription() {
        return description;
    }

    /**
     * Робить першу літеру тексту великою - для заголовків таблиць.
     *
     * @param text текст, наприклад назва списку
     * @return текст з великої літери
     */
    protected static String capitalize(String text) {
        return text.isEmpty() ? text
                : Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    /**
     * Перевіряє, що кількість аргументів лежить у межах.
     *
     * @param arguments аргументи команди
     * @param min       найменша кількість аргументів
     * @param max       найбільша кількість аргументів
     * @throws IllegalArgumentException з підказкою щодо використання,
     *                                  якщо кількість неправильна
     */
    protected final void requireCount(Arguments arguments, int min,
            int max) {
        int count = arguments.count();
        if (count < min || count > max) {
            throw new IllegalArgumentException("використання: " + usage);
        }
    }
}
