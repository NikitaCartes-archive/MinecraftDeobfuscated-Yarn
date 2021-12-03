/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.collection;

import org.jetbrains.annotations.Nullable;

public interface IndexedIterable<T>
extends Iterable<T> {
    public static final int ABSENT_RAW_ID = -1;

    public int getRawId(T var1);

    @Nullable
    public T get(int var1);

    /**
     * {@return the value at {@code index}}
     * 
     * @throws IllegalArgumentException if the value is {@code null}
     */
    default public T getOrThrow(int index) {
        T object = this.get(index);
        if (object == null) {
            throw new IllegalArgumentException("No value with id " + index);
        }
        return object;
    }

    public int size();
}

