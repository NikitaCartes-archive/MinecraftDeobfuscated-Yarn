/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.IntSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

@Environment(value=EnvType.CLIENT)
public class class_4700 {
    private final ThreadLocal<class_4701> field_21519 = ThreadLocal.withInitial(() -> new class_4701());
    private final Long2ObjectLinkedOpenHashMap<int[]> field_21520 = new Long2ObjectLinkedOpenHashMap(256, 0.25f);
    private final ReentrantReadWriteLock field_21521 = new ReentrantReadWriteLock();

    public int method_23770(BlockPos blockPos, IntSupplier intSupplier) {
        int o;
        int i = blockPos.getX() >> 4;
        int j = blockPos.getZ() >> 4;
        class_4701 lv = this.field_21519.get();
        if (lv.field_21522 != i || lv.field_21523 != j) {
            lv.field_21522 = i;
            lv.field_21523 = j;
            lv.field_21524 = this.method_23772(i, j);
        }
        int k = blockPos.getX() & 0xF;
        int l = blockPos.getZ() & 0xF;
        int m = l << 4 | k;
        int n = lv.field_21524[m];
        if (n != -1) {
            return n;
        }
        lv.field_21524[m] = o = intSupplier.getAsInt();
        return o;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void method_23769(int i, int j) {
        try {
            this.field_21521.writeLock().lock();
            for (int k = -1; k <= 1; ++k) {
                for (int l = -1; l <= 1; ++l) {
                    long m = ChunkPos.toLong(i + k, j + l);
                    this.field_21520.remove(m);
                }
            }
        } finally {
            this.field_21521.writeLock().unlock();
        }
    }

    public void method_23768() {
        try {
            this.field_21521.writeLock().lock();
            this.field_21520.clear();
        } finally {
            this.field_21521.writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int[] method_23772(int i, int j) {
        int[] is;
        long l = ChunkPos.toLong(i, j);
        this.field_21521.readLock().lock();
        try {
            is = this.field_21520.get(l);
        } finally {
            this.field_21521.readLock().unlock();
        }
        if (is != null) {
            return is;
        }
        int[] js = new int[256];
        Arrays.fill(js, -1);
        try {
            this.field_21521.writeLock().lock();
            if (this.field_21520.size() >= 256) {
                this.field_21520.removeFirst();
            }
            this.field_21520.put(l, js);
        } finally {
            this.field_21521.writeLock().unlock();
        }
        return js;
    }

    @Environment(value=EnvType.CLIENT)
    static class class_4701 {
        public int field_21522;
        public int field_21523;
        public int[] field_21524;

        private class_4701() {
        }
    }
}

