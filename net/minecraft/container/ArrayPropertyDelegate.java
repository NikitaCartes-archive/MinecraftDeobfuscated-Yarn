/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.container;

import net.minecraft.container.PropertyDelegate;

public class ArrayPropertyDelegate
implements PropertyDelegate {
    private final int[] data;

    public ArrayPropertyDelegate(int i) {
        this.data = new int[i];
    }

    @Override
    public int get(int i) {
        return this.data[i];
    }

    @Override
    public void set(int i, int j) {
        this.data[i] = j;
    }

    @Override
    public int size() {
        return this.data.length;
    }
}

