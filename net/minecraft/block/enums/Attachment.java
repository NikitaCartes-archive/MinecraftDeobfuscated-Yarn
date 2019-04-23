/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.enums;

import net.minecraft.util.SnakeCaseIdentifiable;

public enum Attachment implements SnakeCaseIdentifiable
{
    FLOOR("floor"),
    CEILING("ceiling"),
    SINGLE_WALL("single_wall"),
    DOUBLE_WALL("double_wall");

    private final String name;

    private Attachment(String string2) {
        this.name = string2;
    }

    @Override
    public String toSnakeCase() {
        return this.name;
    }
}

