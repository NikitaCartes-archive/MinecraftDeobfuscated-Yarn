/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.passive;

import java.util.Arrays;
import java.util.Comparator;

public enum HorseColor {
    WHITE(0),
    CREAMY(1),
    CHESTNUT(2),
    BROWN(3),
    BLACK(4),
    GRAY(5),
    DARKBROWN(6);

    private static final HorseColor[] VALUES;
    private final int index;

    private HorseColor(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    public static HorseColor byIndex(int index) {
        return VALUES[index % VALUES.length];
    }

    static {
        VALUES = (HorseColor[])Arrays.stream(HorseColor.values()).sorted(Comparator.comparingInt(HorseColor::getIndex)).toArray(HorseColor[]::new);
    }
}

