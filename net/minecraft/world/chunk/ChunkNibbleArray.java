/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

public class ChunkNibbleArray {
    @Nullable
    protected byte[] byteArray;

    public ChunkNibbleArray() {
    }

    public ChunkNibbleArray(byte[] bs) {
        this.byteArray = bs;
        if (bs.length != 2048) {
            throw Util.throwOrPause(new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + bs.length));
        }
    }

    protected ChunkNibbleArray(int i) {
        this.byteArray = new byte[i];
    }

    public int get(int x, int y, int z) {
        return this.get(this.getIndex(x, y, z));
    }

    public void set(int x, int y, int z, int value) {
        this.set(this.getIndex(x, y, z), value);
    }

    protected int getIndex(int x, int y, int z) {
        return y << 8 | z << 4 | x;
    }

    private int get(int i) {
        if (this.byteArray == null) {
            return 0;
        }
        int j = this.divideByTwo(i);
        if (this.isEven(i)) {
            return this.byteArray[j] & 0xF;
        }
        return this.byteArray[j] >> 4 & 0xF;
    }

    private void set(int index, int value) {
        if (this.byteArray == null) {
            this.byteArray = new byte[2048];
        }
        int i = this.divideByTwo(index);
        this.byteArray[i] = this.isEven(index) ? (byte)(this.byteArray[i] & 0xF0 | value & 0xF) : (byte)(this.byteArray[i] & 0xF | (value & 0xF) << 4);
    }

    private boolean isEven(int n) {
        return (n & 1) == 0;
    }

    private int divideByTwo(int n) {
        return n >> 1;
    }

    public byte[] asByteArray() {
        if (this.byteArray == null) {
            this.byteArray = new byte[2048];
        }
        return this.byteArray;
    }

    public ChunkNibbleArray copy() {
        if (this.byteArray == null) {
            return new ChunkNibbleArray();
        }
        return new ChunkNibbleArray((byte[])this.byteArray.clone());
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 4096; ++i) {
            stringBuilder.append(Integer.toHexString(this.get(i)));
            if ((i & 0xF) == 15) {
                stringBuilder.append("\n");
            }
            if ((i & 0xFF) != 255) continue;
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public boolean isUninitialized() {
        return this.byteArray == null;
    }
}

