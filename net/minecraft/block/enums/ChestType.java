/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum ChestType implements StringIdentifiable
{
    SINGLE("single", 0),
    LEFT("left", 2),
    RIGHT("right", 1);

    public static final ChestType[] VALUES;
    private final String name;
    private final int opposite;

    private ChestType(String name, int opposite) {
        this.name = name;
        this.opposite = opposite;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public ChestType getOpposite() {
        return VALUES[this.opposite];
    }

    static {
        VALUES = ChestType.values();
    }
}

