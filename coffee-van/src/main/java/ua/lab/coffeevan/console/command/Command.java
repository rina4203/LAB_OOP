/*
 * Command.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.console.command;

import java.io.IOException;

/**
 * Команда інтерактивного режиму, наприклад {@code add} чи {@code load}.
 *
 * <p>Командна оболонка знаходить команду за назвою і викликає
 * {@link #execute(Arguments)}, не знаючи, що саме вона робить
 * (патерн "Команда").
 *
 * @author rina4203
 * @version 1.0
 */
public interface Command {

    /**
     * Повертає назву, за якою команду викликають.
     *
     * @return назва команди, наприклад {@code "load"}
     */
    String getName();

    /**
     * Повертає підказку щодо аргументів команди.
     *
     * @return приклад виклику, наприклад {@code "load [<№>|all]"}
     */
    String getUsage();

    /**
     * Повертає опис команди українською мовою.
     *
     * @return опис команди
     */
    String getDescription();

    /**
     * Виконує команду.
     *
     * @param arguments аргументи, введені після назви команди
     * @throws IOException якщо сталася помилка введення-виведення
     */
    void execute(Arguments arguments) throws IOException;
}
