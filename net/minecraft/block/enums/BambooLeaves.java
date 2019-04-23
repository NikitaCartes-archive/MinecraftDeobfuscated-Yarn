/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.enums;

import net.minecraft.util.SnakeCaseIdentifiable;

public enum BambooLeaves implements SnakeCaseIdentifiable
{
    NONE("none"),
    SMALL("small"),
    LARGE("large");

    private final String name;

    private BambooLeaves(String string2) {
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

