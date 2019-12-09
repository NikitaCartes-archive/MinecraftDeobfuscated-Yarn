/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.container;

import net.minecraft.container.PropertyDelegate;

public class ArrayPropertyDelegate
implements PropertyDelegate {
    private final int[] data;

    public ArrayPropertyDelegate(int size) {
        this.data = new int[size];
    }

    @Override
    public int get(int key) {
        return this.data[key];
    }

    @Override
    public void set(int key, int value) {
        this.data[key] = value;
    }

    @Override
    public int size() {
        return this.data.length;
    }
}

