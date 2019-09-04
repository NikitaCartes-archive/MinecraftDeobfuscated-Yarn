/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.Random;
import net.minecraft.class_4540;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.biome.layer.CachingLayerSampler;
import net.minecraft.world.biome.layer.LayerOperator;
import net.minecraft.world.biome.layer.LayerSampleContext;
import net.minecraft.world.biome.layer.LayerSampler;

public class CachingLayerContext
implements LayerSampleContext<CachingLayerSampler> {
    private final Long2IntLinkedOpenHashMap cache;
    private final int cacheCapacity;
    private final PerlinNoiseSampler noiseSampler;
    private final long worldSeed;
    private long localSeed;

    public CachingLayerContext(int i, long l, long m) {
        this.worldSeed = CachingLayerContext.method_22417(l, m);
        this.noiseSampler = new PerlinNoiseSampler(new Random(l));
        this.cache = new Long2IntLinkedOpenHashMap(16, 0.25f);
        this.cache.defaultReturnValue(Integer.MIN_VALUE);
        this.cacheCapacity = i;
    }

    public CachingLayerSampler method_15837(LayerOperator layerOperator) {
        return new CachingLayerSampler(this.cache, this.cacheCapacity, layerOperator);
    }

    public CachingLayerSampler method_15838(LayerOperator layerOperator, CachingLayerSampler cachingLayerSampler) {
        return new CachingLayerSampler(this.cache, Math.min(1024, cachingLayerSampler.getCapacity() * 4), layerOperator);
    }

    public CachingLayerSampler method_15836(LayerOperator layerOperator, CachingLayerSampler cachingLayerSampler, CachingLayerSampler cachingLayerSampler2) {
        return new CachingLayerSampler(this.cache, Math.min(1024, Math.max(cachingLayerSampler.getCapacity(), cachingLayerSampler2.getCapacity()) * 4), layerOperator);
    }

    @Override
    public void initSeed(long l, long m) {
        long n = this.worldSeed;
        n = class_4540.method_22372(n, l);
        n = class_4540.method_22372(n, m);
        n = class_4540.method_22372(n, l);
        this.localSeed = n = class_4540.method_22372(n, m);
    }

    @Override
    public int nextInt(int i) {
        int j = (int)Math.floorMod(this.localSeed >> 24, (long)i);
        this.localSeed = class_4540.method_22372(this.localSeed, this.worldSeed);
        return j;
    }

    @Override
    public PerlinNoiseSampler getNoiseSampler() {
        return this.noiseSampler;
    }

    private static long method_22417(long l, long m) {
        long n = m;
        n = class_4540.method_22372(n, m);
        n = class_4540.method_22372(n, m);
        n = class_4540.method_22372(n, m);
        long o = l;
        o = class_4540.method_22372(o, n);
        o = class_4540.method_22372(o, n);
        o = class_4540.method_22372(o, n);
        return o;
    }

    @Override
    public /* synthetic */ LayerSampler createSampler(LayerOperator layerOperator) {
        return this.method_15837(layerOperator);
    }
}

