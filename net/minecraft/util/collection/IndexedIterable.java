/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.collection;

import org.jetbrains.annotations.Nullable;

public interface IndexedIterable<T>
extends Iterable<T> {
    public int getRawId(T var1);

    @Nullable
    public T get(int var1);
}

