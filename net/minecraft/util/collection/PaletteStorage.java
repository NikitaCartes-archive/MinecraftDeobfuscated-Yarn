/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.collection;

import java.util.function.IntConsumer;

/**
 * A storage whose values are raw IDs held by palettes.
 */
public interface PaletteStorage {
    /**
     * Sets {@code value} to {@code index} and returns the previous value in
     * this storage.
     * 
     * @return the previous value
     * 
     * @param index the index
     * @param value the value to set
     */
    public int swap(int var1, int var2);

    /**
     * Sets {@code value} to {@code index} in this storage.
     * 
     * @param value the value to set
     * @param index the index
     */
    public void set(int var1, int var2);

    /**
     * {@return the value at {@code index} in this storage}
     * 
     * @param index the index
     */
    public int get(int var1);

    /**
     * {@return the backing data of this storage}
     */
    public long[] getData();

    /**
     * {@return the size of, or the number of elements in, this storage}
     */
    public int getSize();

    /**
     * {@return the number of bits each element in this storage uses}
     */
    public int getElementBits();

    /**
     * Executes an {@code action} on all values in this storage, sequentially.
     */
    public void forEach(IntConsumer var1);
}

