/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.storage;

import java.util.BitSet;

public class SectorMap {
    private final BitSet field_20433 = new BitSet();

    public void allocate(int i, int j) {
        this.field_20433.set(i, i + j);
    }

    public void free(int i, int j) {
        this.field_20433.clear(i, i + j);
    }

    public int allocate(int i) {
        int j = 0;
        while (true) {
            int k;
            int l;
            if ((l = this.field_20433.nextSetBit(k = this.field_20433.nextClearBit(j))) == -1 || l - k >= i) {
                this.allocate(k, i);
                return k;
            }
            j = l;
        }
    }
}

