package net.minecraft.world.biome.layer;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.Random;
import net.minecraft.class_4540;
import net.minecraft.util.math.noise.PerlinNoiseSampler;

public class CachingLayerContext implements LayerSampleContext<CachingLayerSampler> {
	private final Long2IntLinkedOpenHashMap cache;
	private final int cacheCapacity;
	private final PerlinNoiseSampler noiseSampler;
	private final long worldSeed;
	private long localSeed;

	public CachingLayerContext(int i, long l, long m) {
		this.worldSeed = method_22417(l, m);
		this.noiseSampler = new PerlinNoiseSampler(new Random(l));
		this.cache = new Long2IntLinkedOpenHashMap(16, 0.25F);
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
		n = class_4540.method_22372(n, m);
		this.localSeed = n;
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
		long n = class_4540.method_22372(m, m);
		n = class_4540.method_22372(n, m);
		n = class_4540.method_22372(n, m);
		long o = class_4540.method_22372(l, n);
		o = class_4540.method_22372(o, n);
		return class_4540.method_22372(o, n);
	}
}
