/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.enums;

import net.minecraft.util.SnakeCaseIdentifiable;

public enum SlabType implements SnakeCaseIdentifiable
{
    TOP("top"),
    BOTTOM("bottom"),
    DOUBLE("double");

    private final String name;

    private SlabType(String string2) {
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

