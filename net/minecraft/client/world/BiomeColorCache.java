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
    private static final int field_32164 = 256;
    private final ThreadLocal<Last> last = ThreadLocal.withInitial(Last::new);
    private final Long2ObjectLinkedOpenHashMap<class_6598> colors = new Long2ObjectLinkedOpenHashMap(256, 0.25f);
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ToIntFunction<BlockPos> field_34795;

    public BiomeColorCache(ToIntFunction<BlockPos> toIntFunction) {
        this.field_34795 = toIntFunction;
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
        int[] is = last.colors.method_38528(pos.getY());
        int k = pos.getX() & 0xF;
        int l = pos.getZ() & 0xF;
        int m = l << 4 | k;
        int n = is[m];
        if (n != -1) {
            return n;
        }
        is[m] = o = this.field_34795.applyAsInt(pos);
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
    private class_6598 getColorArray(int chunkX, int chunkZ) {
        class_6598 lv;
        long l = ChunkPos.toLong(chunkX, chunkZ);
        this.lock.readLock().lock();
        try {
            lv = this.colors.get(l);
            if (lv != null) {
                class_6598 class_65982 = lv;
                return class_65982;
            }
        } finally {
            this.lock.readLock().unlock();
        }
        this.lock.writeLock().lock();
        try {
            lv = this.colors.get(l);
            if (lv != null) {
                class_6598 class_65983 = lv;
                return class_65983;
            }
            class_6598 lv2 = new class_6598();
            if (this.colors.size() >= 256) {
                this.colors.removeFirst();
            }
            this.colors.put(l, lv2);
            class_6598 class_65984 = lv2;
            return class_65984;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Last {
        public int x = Integer.MIN_VALUE;
        public int z = Integer.MIN_VALUE;
        @Nullable
        class_6598 colors;

        private Last() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class class_6598 {
        private Int2ObjectArrayMap<int[]> field_34796 = new Int2ObjectArrayMap(16);
        private final ReentrantReadWriteLock field_34797 = new ReentrantReadWriteLock();
        private static final int field_34798 = MathHelper.square(16);

        class_6598() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public int[] method_38528(int i2) {
            this.field_34797.readLock().lock();
            try {
                int[] is = this.field_34796.get(i2);
                if (is != null) {
                    int[] nArray = is;
                    return nArray;
                }
            } finally {
                this.field_34797.readLock().unlock();
            }
            this.field_34797.writeLock().lock();
            try {
                int[] nArray = this.field_34796.computeIfAbsent(i2, i -> this.method_38527());
                return nArray;
            } finally {
                this.field_34797.writeLock().unlock();
            }
        }

        private int[] method_38527() {
            int[] is = new int[field_34798];
            Arrays.fill(is, -1);
            return is;
        }
    }
}

