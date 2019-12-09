/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer.util;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.Random;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.biome.layer.util.CachingLayerSampler;
import net.minecraft.world.biome.layer.util.LayerOperator;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.source.SeedMixer;

public class CachingLayerContext
implements LayerSampleContext<CachingLayerSampler> {
    private final Long2IntLinkedOpenHashMap cache;
    private final int cacheCapacity;
    private final PerlinNoiseSampler noiseSampler;
    private final long worldSeed;
    private long localSeed;

    public CachingLayerContext(int cacheCapacity, long seed, long salt) {
        this.worldSeed = CachingLayerContext.addSalt(seed, salt);
        this.noiseSampler = new PerlinNoiseSampler(new Random(seed));
        this.cache = new Long2IntLinkedOpenHashMap(16, 0.25f);
        this.cache.defaultReturnValue(Integer.MIN_VALUE);
        this.cacheCapacity = cacheCapacity;
    }

    @Override
    public CachingLayerSampler createSampler(LayerOperator layerOperator) {
        return new CachingLayerSampler(this.cache, this.cacheCapacity, layerOperator);
    }

    @Override
    public CachingLayerSampler createSampler(LayerOperator layerOperator, CachingLayerSampler cachingLayerSampler) {
        return new CachingLayerSampler(this.cache, Math.min(1024, cachingLayerSampler.getCapacity() * 4), layerOperator);
    }

    @Override
    public CachingLayerSampler createSampler(LayerOperator layerOperator, CachingLayerSampler cachingLayerSampler, CachingLayerSampler cachingLayerSampler2) {
        return new CachingLayerSampler(this.cache, Math.min(1024, Math.max(cachingLayerSampler.getCapacity(), cachingLayerSampler2.getCapacity()) * 4), layerOperator);
    }

    @Override
    public void initSeed(long x, long y) {
        long l = this.worldSeed;
        l = SeedMixer.mixSeed(l, x);
        l = SeedMixer.mixSeed(l, y);
        l = SeedMixer.mixSeed(l, x);
        this.localSeed = l = SeedMixer.mixSeed(l, y);
    }

    @Override
    public int nextInt(int bound) {
        int i = (int)Math.floorMod(this.localSeed >> 24, (long)bound);
        this.localSeed = SeedMixer.mixSeed(this.localSeed, this.worldSeed);
        return i;
    }

    @Override
    public PerlinNoiseSampler getNoiseSampler() {
        return this.noiseSampler;
    }

    private static long addSalt(long seed, long salt) {
        long l = salt;
        l = SeedMixer.mixSeed(l, salt);
        l = SeedMixer.mixSeed(l, salt);
        l = SeedMixer.mixSeed(l, salt);
        long m = seed;
        m = SeedMixer.mixSeed(m, l);
        m = SeedMixer.mixSeed(m, l);
        m = SeedMixer.mixSeed(m, l);
        return m;
    }

    @Override
    public /* synthetic */ LayerSampler createSampler(LayerOperator operator) {
        return this.createSampler(operator);
    }
}

