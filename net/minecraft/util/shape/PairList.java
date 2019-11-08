/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;

interface PairList {
    public DoubleList getPairs();

    public boolean forEachPair(Consumer var1);

    public static interface Consumer {
        public boolean merge(int var1, int var2, int var3);
    }
}

