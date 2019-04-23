/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.enums;

import net.minecraft.util.SnakeCaseIdentifiable;

public enum BlockHalf implements SnakeCaseIdentifiable
{
    TOP("top"),
    BOTTOM("bottom");

    private final String name;

    private BlockHalf(String string2) {
        this.name = string2;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String toSnakeCase() {
        return this.name;
    }
}

