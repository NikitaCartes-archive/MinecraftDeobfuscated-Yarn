/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.world;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.ToIntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BiomeColorCache {
    private static final int MAX_ENTRY_SIZE = 256;
    private final ThreadLocal<Last> last = ThreadLocal.withInitial(Last::new);
    private final Long2ObjectLinkedOpenHashMap<Colors> colors = new Long2ObjectLinkedOpenHashMap(256, 0.25f);
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ToIntFunction<BlockPos> colorFactory;

    public BiomeColorCache(ToIntFunction<BlockPos> colorFactory) {
        this.colorFactory = colorFactory;
    }

    public int getBiomeColor(BlockPos pos) {
        int o;
        int i = ChunkSectionPos.getSectionCoord(pos.getX());
        int j = ChunkSectionPos.getSectionCoord(pos.getZ());
        Last last = this.last.get();
        if (last.x != i || last.z != j || last.colors == null) {
            last.x = i;
            last.z = j;
            last.colors = this.getColorArray(i, j);
        }
        int[] is = last.colors.get(pos.getY());
        int k = pos.getX() & 0xF;
        int l = pos.getZ() & 0xF;
        int m = l << 4 | k;
        int n = is[m];
        if (n != -1) {
            return n;
        }
        is[m] = o = this.colorFactory.applyAsInt(pos);
        return o;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reset(int chunkX, int chunkZ) {
        try {
            this.lock.writeLock().lock();
            for (int i = -1; i <= 1; ++i) {
                for (int j = -1; j <= 1; ++j) {
                    long l = ChunkPos.toLong(chunkX + i, chunkZ + j);
                    this.colors.remove(l);
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
    private Colors getColorArray(int chunkX, int chunkZ) {
        Colors colors;
        long l = ChunkPos.toLong(chunkX, chunkZ);
        this.lock.readLock().lock();
        try {
            colors = this.colors.get(l);
            if (colors != null) {
                Colors colors2 = colors;
                return colors2;
            }
        } finally {
            this.lock.readLock().unlock();
        }
        this.lock.writeLock().lock();
        try {
            colors = this.colors.get(l);
            if (colors != null) {
                Colors colors3 = colors;
                return colors3;
            }
            Colors colors2 = new Colors();
            if (this.colors.size() >= 256) {
                this.colors.removeFirst();
            }
            this.colors.put(l, colors2);
            Colors colors4 = colors2;
            return colors4;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Last {
        public int x = Integer.MIN_VALUE;
        public int z = Integer.MIN_VALUE;
        @Nullable
        Colors colors;

        private Last() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Colors {
        private final Int2ObjectArrayMap<int[]> colors = new Int2ObjectArrayMap(16);
        private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        private static final int XZ_COLORS_SIZE = MathHelper.square(16);

        Colors() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public int[] get(int y2) {
            this.lock.readLock().lock();
            try {
                int[] is = this.colors.get(y2);
                if (is != null) {
                    int[] nArray = is;
                    return nArray;
                }
            } finally {
                this.lock.readLock().unlock();
            }
            this.lock.writeLock().lock();
            try {
                int[] nArray = this.colors.computeIfAbsent(y2, y -> this.createDefault());
                return nArray;
            } finally {
                this.lock.writeLock().unlock();
            }
        }

        private int[] createDefault() {
            int[] is = new int[XZ_COLORS_SIZE];
            Arrays.fill(is, -1);
            return is;
        }
    }
}

