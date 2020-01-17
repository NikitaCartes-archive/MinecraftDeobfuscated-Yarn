/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.container;

import net.minecraft.container.PropertyDelegate;

/**
 * A {@link PropertyDelegate} that is implemented using an int array.
 */
public class ArrayPropertyDelegate
implements PropertyDelegate {
    private final int[] data;

    public ArrayPropertyDelegate(int size) {
        this.data = new int[size];
    }

    @Override
    public int get(int index) {
        return this.data[index];
    }

    @Override
    public void set(int index, int value) {
        this.data[index] = value;
    }

    @Override
    public int size() {
        return this.data.length;
    }
}

