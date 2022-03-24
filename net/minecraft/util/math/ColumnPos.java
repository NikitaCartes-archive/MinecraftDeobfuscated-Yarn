/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;

public record ColumnPos(int x, int z) {
    private static final long field_29757 = 32L;
    private static final long field_29758 = 0xFFFFFFFFL;

    public ChunkPos toChunkPos() {
        return new ChunkPos(ChunkSectionPos.getSectionCoord(this.x), ChunkSectionPos.getSectionCoord(this.z));
    }

    public long pack() {
        return ColumnPos.pack(this.x, this.z);
    }

    public static long pack(int x, int z) {
        return (long)x & 0xFFFFFFFFL | ((long)z & 0xFFFFFFFFL) << 32;
    }

    public static int getX(long packed) {
        return (int)(packed & 0xFFFFFFFFL);
    }

    public static int getZ(long packed) {
        return (int)(packed >>> 32 & 0xFFFFFFFFL);
    }

    @Override
    public String toString() {
        return "[" + this.x + ", " + this.z + "]";
    }

    @Override
    public int hashCode() {
        return ChunkPos.hashCode(this.x, this.z);
    }
}

