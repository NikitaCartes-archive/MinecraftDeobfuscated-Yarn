/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.light.LevelPropagator;

public abstract class ChunkPosLevelPropagator
extends LevelPropagator {
    protected ChunkPosLevelPropagator(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected boolean isInvalid(long l) {
        return l == ChunkPos.INVALID;
    }

    @Override
    protected void updateNeighborsRecursively(long l, int i, boolean bl) {
        ChunkPos chunkPos = new ChunkPos(l);
        int j = chunkPos.x;
        int k = chunkPos.z;
        for (int m = -1; m <= 1; ++m) {
            for (int n = -1; n <= 1; ++n) {
                long o = ChunkPos.toLong(j + m, k + n);
                if (o == l) continue;
                this.updateRecursively(l, o, i, bl);
            }
        }
    }

    @Override
    protected int getMergedLevel(long l, long m, int i) {
        int j = i;
        ChunkPos chunkPos = new ChunkPos(l);
        int k = chunkPos.x;
        int n = chunkPos.z;
        for (int o = -1; o <= 1; ++o) {
            for (int p = -1; p <= 1; ++p) {
                long q = ChunkPos.toLong(k + o, n + p);
                if (q == l) {
                    q = ChunkPos.INVALID;
                }
                if (q == m) continue;
                int r = this.getPropagatedLevel(q, l, this.getLevel(q));
                if (j > r) {
                    j = r;
                }
                if (j != 0) continue;
                return j;
            }
        }
        return j;
    }

    @Override
    protected int getPropagatedLevel(long l, long m, int i) {
        if (l == ChunkPos.INVALID) {
            return this.getInitialLevel(m);
        }
        return i + 1;
    }

    protected abstract int getInitialLevel(long var1);

    public void update(long l, int i, boolean bl) {
        this.update(ChunkPos.INVALID, l, i, bl);
    }
}

