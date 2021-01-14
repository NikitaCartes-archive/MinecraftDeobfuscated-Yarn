/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.storage;

import java.util.BitSet;

public class SectorMap {
    private final BitSet field_20433 = new BitSet();

    public void allocate(int start, int size) {
        this.field_20433.set(start, start + size);
    }

    public void free(int start, int size) {
        this.field_20433.clear(start, start + size);
    }

    public int allocate(int size) {
        int i = 0;
        while (true) {
            int j;
            int k;
            if ((k = this.field_20433.nextSetBit(j = this.field_20433.nextClearBit(i))) == -1 || k - j >= size) {
                this.allocate(j, size);
                return j;
            }
            i = k;
        }
    }
}

