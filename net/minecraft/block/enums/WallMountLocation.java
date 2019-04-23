/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.enums;

import net.minecraft.util.SnakeCaseIdentifiable;

public enum WallMountLocation implements SnakeCaseIdentifiable
{
    FLOOR("floor"),
    WALL("wall"),
    CEILING("ceiling");

    private final String name;

    private WallMountLocation(String string2) {
        this.name = string2;
    }

    @Override
    public String toSnakeCase() {
        return this.name;
    }
}

