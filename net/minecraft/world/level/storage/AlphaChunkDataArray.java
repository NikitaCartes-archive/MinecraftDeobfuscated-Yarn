/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.level.storage;

public class AlphaChunkDataArray {
    public final byte[] data;
    private final int zOffset;
    private final int xOffset;

    public AlphaChunkDataArray(byte[] bs, int i) {
        this.data = bs;
        this.zOffset = i;
        this.xOffset = i + 4;
    }

    public int get(int i, int j, int k) {
        int l = i << this.xOffset | k << this.zOffset | j;
        int m = l >> 1;
        int n = l & 1;
        if (n == 0) {
            return this.data[m] & 0xF;
        }
        return this.data[m] >> 4 & 0xF;
    }
}

