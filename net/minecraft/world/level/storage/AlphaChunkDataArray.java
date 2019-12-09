/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.level.storage;

public class AlphaChunkDataArray {
    public final byte[] data;
    private final int zOffset;
    private final int xOffset;

    public AlphaChunkDataArray(byte[] data, int yCoordinateBits) {
        this.data = data;
        this.zOffset = yCoordinateBits;
        this.xOffset = yCoordinateBits + 4;
    }

    public int get(int x, int y, int z) {
        int i = x << this.xOffset | z << this.zOffset | y;
        int j = i >> 1;
        int k = i & 1;
        if (k == 0) {
            return this.data[j] & 0xF;
        }
        return this.data[j] >> 4 & 0xF;
    }
}

