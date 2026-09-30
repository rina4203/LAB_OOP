/*
 * ExceptionsTest.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Винятки програми")
class ExceptionsTest {

    private static final String MESSAGE = "Опис помилки";

    @Test
    @DisplayName("помилка завантаження - неперевірюваний виняток")
    void cargoLoadingExceptionIsUnchecked() {
        CargoLoadingException e = new CargoLoadingException(MESSAGE);

        assertInstanceOf(RuntimeException.class, e);
        assertEquals(MESSAGE, e.getMessage());
    }

    @Test
    @DisplayName("помилка в даних - різновид помилки введення-виведення")
    void invalidDataExceptionIsIoException() {
        InvalidDataException e = new InvalidDataException(MESSAGE);

        assertInstanceOf(IOException.class, e);
        assertEquals(MESSAGE, e.getMessage());
        assertNull(e.getCause());
    }

    @Test
    @DisplayName("помилка в даних зберігає першопричину")
    void invalidDataExceptionKeepsCause() {
        NumberFormatException cause = new NumberFormatException("abc");

        InvalidDataException e = new InvalidDataException(MESSAGE, cause);

        assertEquals(MESSAGE, e.getMessage());
        assertSame(cause, e.getCause());
    }
}
