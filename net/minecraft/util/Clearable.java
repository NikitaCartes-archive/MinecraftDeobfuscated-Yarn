/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import org.jetbrains.annotations.Nullable;

public interface Clearable {
    public void clear();

    public static void clear(@Nullable Object object) {
        if (object instanceof Clearable) {
            ((Clearable)object).clear();
        }
    }
}

