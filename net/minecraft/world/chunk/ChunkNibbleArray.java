/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

import net.minecraft.util.SystemUtil;
import org.jetbrains.annotations.Nullable;

public class ChunkNibbleArray {
    @Nullable
    protected byte[] byteArray;

    public ChunkNibbleArray() {
    }

    public ChunkNibbleArray(byte[] bs) {
        this.byteArray = bs;
        if (bs.length != 2048) {
            throw SystemUtil.throwOrPause(new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + bs.length));
        }
    }

    protected ChunkNibbleArray(int i) {
        this.byteArray = new byte[i];
    }

    public int get(int i, int j, int k) {
        return this.get(this.getIndex(i, j, k));
    }

    public void set(int i, int j, int k, int l) {
        this.set(this.getIndex(i, j, k), l);
    }

    protected int getIndex(int i, int j, int k) {
        return j << 8 | k << 4 | i;
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

    private void set(int i, int j) {
        if (this.byteArray == null) {
            this.byteArray = new byte[2048];
        }
        int k = this.divideByTwo(i);
        this.byteArray[k] = this.isEven(i) ? (byte)(this.byteArray[k] & 0xF0 | j & 0xF) : (byte)(this.byteArray[k] & 0xF | (j & 0xF) << 4);
    }

    private boolean isEven(int i) {
        return (i & 1) == 0;
    }

    private int divideByTwo(int i) {
        return i >> 1;
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

