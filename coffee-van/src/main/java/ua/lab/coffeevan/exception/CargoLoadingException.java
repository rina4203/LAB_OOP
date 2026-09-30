/*
 * CargoLoadingException.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.exception;

/**
 * Виникає при спробі завантажити у фургон товар, для якого не вистачає
 * вільного об'єму або залишку бюджету.
 *
 * @author rina4203
 * @version 1.0
 */
public class CargoLoadingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Створює виняток з повідомленням про причину відмови.
     *
     * @param message опис причини, через яку товар не завантажено
     */
    public CargoLoadingException(String message) {
        super(message);
    }
}
