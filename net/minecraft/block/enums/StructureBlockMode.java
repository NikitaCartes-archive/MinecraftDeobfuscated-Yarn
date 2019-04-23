/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.enums;

import net.minecraft.util.SnakeCaseIdentifiable;

public enum StructureBlockMode implements SnakeCaseIdentifiable
{
    SAVE("save"),
    LOAD("load"),
    CORNER("corner"),
    DATA("data");

    private final String name;

    private StructureBlockMode(String string2) {
        this.name = string2;
    }

    @Override
    public String toSnakeCase() {
        return this.name;
    }
}

