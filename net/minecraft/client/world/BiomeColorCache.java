/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.IntSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

@Environment(value=EnvType.CLIENT)
public class BiomeColorCache {
    private final ThreadLocal<Last> last = ThreadLocal.withInitial(() -> new Last());
    private final Long2ObjectLinkedOpenHashMap<int[]> colors = new Long2ObjectLinkedOpenHashMap(256, 0.25f);
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public int getBiomeColor(BlockPos blockPos, IntSupplier intSupplier) {
        int o;
        int i = blockPos.getX() >> 4;
        int j = blockPos.getZ() >> 4;
        Last last = this.last.get();
        if (last.x != i || last.z != j) {
            last.x = i;
            last.z = j;
            last.colors = this.getColorArray(i, j);
        }
        int k = blockPos.getX() & 0xF;
        int l = blockPos.getZ() & 0xF;
        int m = l << 4 | k;
        int n = last.colors[m];
        if (n != -1) {
            return n;
        }
        last.colors[m] = o = intSupplier.getAsInt();
        return o;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reset(int i, int j) {
        try {
            this.lock.writeLock().lock();
            for (int k = -1; k <= 1; ++k) {
                for (int l = -1; l <= 1; ++l) {
                    long m = ChunkPos.toLong(i + k, j + l);
                    this.colors.remove(m);
                }
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void reset() {
        try {
            this.lock.writeLock().lock();
            this.colors.clear();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int[] getColorArray(int i, int j) {
        int[] is;
        long l = ChunkPos.toLong(i, j);
        this.lock.readLock().lock();
        try {
            is = this.colors.get(l);
        } finally {
            this.lock.readLock().unlock();
        }
        if (is != null) {
            return is;
        }
        int[] js = new int[256];
        Arrays.fill(js, -1);
        try {
            this.lock.writeLock().lock();
            if (this.colors.size() >= 256) {
                this.colors.removeFirst();
            }
            this.colors.put(l, js);
        } finally {
            this.lock.writeLock().unlock();
        }
        return js;
    }

    @Environment(value=EnvType.CLIENT)
    static class Last {
        public int x = Integer.MIN_VALUE;
        public int z = Integer.MIN_VALUE;
        public int[] colors;

        private Last() {
        }
    }
}

