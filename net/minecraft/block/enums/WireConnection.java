/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.enums;

import net.minecraft.util.SnakeCaseIdentifiable;

public enum WireConnection implements SnakeCaseIdentifiable
{
    UP("up"),
    SIDE("side"),
    NONE("none");

    private final String name;

    private WireConnection(String string2) {
        this.name = string2;
    }

    public String toString() {
        return this.toSnakeCase();
    }

    @Override
    public String toSnakeCase() {
        return this.name;
    }
}

