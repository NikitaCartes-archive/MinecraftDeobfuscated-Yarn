/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.minecraft.util.StringIdentifiable;

public enum class_5816 implements StringIdentifiable
{
    field_28718("none", true),
    field_28719("unstable", false),
    field_28720("partial", true),
    field_28721("full", true);

    private final String field_28722;
    private final boolean field_28723;

    private class_5816(String string2, boolean bl) {
        this.field_28722 = string2;
        this.field_28723 = bl;
    }

    @Override
    public String asString() {
        return this.field_28722;
    }

    public boolean method_33636() {
        return this.field_28723;
    }
}

