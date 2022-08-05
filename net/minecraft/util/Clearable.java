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

    /**
     * Clears {@code o} if it is {@link Clearable}.
     */
    public static void clear(@Nullable Object o) {
        if (o instanceof Clearable) {
            ((Clearable)o).clear();
        }
    }
}

