/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.storage;

import java.util.BitSet;

public class SectorMap {
    private final BitSet bitSet = new BitSet();

    public void allocate(int start, int size) {
        this.bitSet.set(start, start + size);
    }

    public void free(int start, int size) {
        this.bitSet.clear(start, start + size);
    }

    public int allocate(int size) {
        int i = 0;
        while (true) {
            int j;
            int k;
            if ((k = this.bitSet.nextSetBit(j = this.bitSet.nextClearBit(i))) == -1 || k - j >= size) {
                this.allocate(j, size);
                return j;
            }
            i = k;
        }
    }
}

