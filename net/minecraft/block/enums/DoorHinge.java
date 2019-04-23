/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.enums;

import net.minecraft.util.SnakeCaseIdentifiable;

public enum DoorHinge implements SnakeCaseIdentifiable
{
    LEFT,
    RIGHT;


    public String toString() {
        return this.toSnakeCase();
    }

    @Override
    public String toSnakeCase() {
        return this == LEFT ? "left" : "right";
    }
}

