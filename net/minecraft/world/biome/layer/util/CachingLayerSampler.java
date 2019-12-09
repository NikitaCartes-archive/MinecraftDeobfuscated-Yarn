/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer.util;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.layer.util.LayerOperator;
import net.minecraft.world.biome.layer.util.LayerSampler;

public final class CachingLayerSampler
implements LayerSampler {
    private final LayerOperator operator;
    private final Long2IntLinkedOpenHashMap cache;
    private final int cacheCapacity;

    public CachingLayerSampler(Long2IntLinkedOpenHashMap cache, int cacheCapacity, LayerOperator operator) {
        this.cache = cache;
        this.cacheCapacity = cacheCapacity;
        this.operator = operator;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sample(int x, int z) {
        long l = ChunkPos.toLong(x, z);
        Long2IntLinkedOpenHashMap long2IntLinkedOpenHashMap = this.cache;
        synchronized (long2IntLinkedOpenHashMap) {
            int i = this.cache.get(l);
            if (i != Integer.MIN_VALUE) {
                return i;
            }
            int j = this.operator.apply(x, z);
            this.cache.put(l, j);
            if (this.cache.size() > this.cacheCapacity) {
                for (int k = 0; k < this.cacheCapacity / 16; ++k) {
                    this.cache.removeFirstInt();
                }
            }
            return j;
        }
    }

    public int getCapacity() {
        return this.cacheCapacity;
    }
}

