/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import org.jetbrains.annotations.Nullable;

/**
 * Represents an object which can be cleared.
 */
public interface Clearable {
    public void clear();

    public static void clear(@Nullable Object object) {
        if (object instanceof Clearable) {
            ((Clearable)object).clear();
        }
    }
}

