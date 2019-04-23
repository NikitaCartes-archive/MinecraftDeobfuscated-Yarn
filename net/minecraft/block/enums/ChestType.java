/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.enums;

import net.minecraft.util.SnakeCaseIdentifiable;

public enum ChestType implements SnakeCaseIdentifiable
{
    SINGLE("single", 0),
    LEFT("left", 2),
    RIGHT("right", 1);

    public static final ChestType[] VALUES;
    private final String name;
    private final int opposite;

    private ChestType(String string2, int j) {
        this.name = string2;
        this.opposite = j;
    }

    @Override
    public String toSnakeCase() {
        return this.name;
    }

    public ChestType getOpposite() {
        return VALUES[this.opposite];
    }

    static {
        VALUES = ChestType.values();
    }
}

