/*
 * PackagingType.java
 *
 * Version 1.0
 *
 * 01.10.2026
 *
 * Copyright (c) 2026 rina4203
 */

package ua.lab.coffeevan.model;

/**
 * Тип упаковки кави.
 *
 * <p>Кожна упаковка займає більше місця, ніж кава всередині неї:
 * враховуються стінки, повітряний проміжок, шви пакетиків тощо.
 * Цю різницю описує коефіцієнт об'єму - відношення об'єму упакованого
 * товару до об'єму самої кави.
 *
 * @author rina4203
 * @version 1.0
 */
public enum PackagingType {

    /** Паперовий пакет з клапаном дегазації. */
    PAPER_BAG("паперовий пакет", 1.25),

    /** Вакуумна упаковка (цеглинка). */
    VACUUM_PACK("вакуумна упаковка", 1.10),

    /** Бляшана банка. */
    TIN_CAN("бляшана банка", 1.35),

    /** Скляна банка. */
    GLASS_JAR("скляна банка", 1.60),

    /** Коробка з порційними пакетиками-стіками. */
    SACHET("пакетики-стіки", 1.80);

    private final String displayName;
    private final double volumeFactor;

    PackagingType(String displayName, double volumeFactor) {
        this.displayName = displayName;
        this.volumeFactor = volumeFactor;
    }

    /**
     * Повертає назву упаковки українською мовою.
     *
     * @return назва упаковки
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Повертає коефіцієнт об'єму упаковки.
     *
     * @return відношення об'єму упакованого товару до об'єму кави
     */
    public double getVolumeFactor() {
        return volumeFactor;
    }

    /**
     * Обчислює об'єм, який займе кава разом з цією упаковкою.
     *
     * @param contentVolume об'єм самої кави
     * @return об'єм упакованого товару в тих самих одиницях
     */
    public double calculatePackedVolume(double contentVolume) {
        return contentVolume * volumeFactor;
    }
}
