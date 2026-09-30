/*
 * InvalidDataException.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.exception;

import java.io.IOException;

/**
 * Виникає, коли файл з параметрами фургона чи каталогом кави
 * має некоректний формат або вміст.
 *
 * @author rina4203
 * @version 1.0
 */
public class InvalidDataException extends IOException {

    private static final long serialVersionUID = 1L;

    /**
     * Створює виняток з описом помилки у даних.
     *
     * @param message опис помилки
     */
    public InvalidDataException(String message) {
        super(message);
    }

    /**
     * Створює виняток з описом помилки у даних та її першопричиною.
     *
     * @param message опис помилки
     * @param cause   першопричина помилки
     */
    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
