/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum ComparatorMode implements StringIdentifiable
{
    COMPARE("compare"),
    SUBTRACT("subtract");

    private final String name;

    private ComparatorMode(String string2) {
        this.name = string2;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}

