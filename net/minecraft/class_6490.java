/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.function.IntConsumer;

public interface class_6490 {
    public int setAndGetOldValue(int var1, int var2);

    public void set(int var1, int var2);

    public int get(int var1);

    public long[] getStorage();

    public int getSize();

    public int getElementBits();

    public void forEach(IntConsumer var1);
}

