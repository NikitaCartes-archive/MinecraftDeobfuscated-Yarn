package net.minecraft.world.biome.layer.util;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.Random;
import net.minecraft.util.math.noise.PerlinNoiseSampler;

public class CachingLayerContext implements LayerSampleContext<CachingLayerSampler> {
	private final Long2IntLinkedOpenHashMap cache;
	private final int cacheCapacity;
	protected long initSeed;
	protected PerlinNoiseSampler noiseSampler;
	private long worldSeed;
	private long localSeed;

	public CachingLayerContext(int cacheCapacity, long seed, long salt) {
		this.initSeed = salt;
		this.initSeed = this.initSeed * (this.initSeed * 6364136223846793005L + 1442695040888963407L);
		this.initSeed += salt;
		this.initSeed = this.initSeed * (this.initSeed * 6364136223846793005L + 1442695040888963407L);
		this.initSeed += salt;
		this.initSeed = this.initSeed * (this.initSeed * 6364136223846793005L + 1442695040888963407L);
		this.initSeed += salt;
		this.cache = new Long2IntLinkedOpenHashMap(16, 0.25F);
		this.cache.defaultReturnValue(Integer.MIN_VALUE);
		this.cacheCapacity = cacheCapacity;
		this.initWorldSeed(seed);
	}

	public CachingLayerSampler createSampler(LayerOperator layerOperator) {
		return new CachingLayerSampler(this.cache, this.cacheCapacity, layerOperator);
	}

	public CachingLayerSampler createSampler(LayerOperator layerOperator, CachingLayerSampler cachingLayerSampler) {
		return new CachingLayerSampler(this.cache, Math.min(1024, cachingLayerSampler.getCapacity() * 4), layerOperator);
	}

	public CachingLayerSampler createSampler(LayerOperator layerOperator, CachingLayerSampler cachingLayerSampler, CachingLayerSampler cachingLayerSampler2) {
		return new CachingLayerSampler(this.cache, Math.min(1024, Math.max(cachingLayerSampler.getCapacity(), cachingLayerSampler2.getCapacity()) * 4), layerOperator);
	}

	public void initWorldSeed(long seed) {
		this.worldSeed = seed;
		this.worldSeed = this.worldSeed * (this.worldSeed * 6364136223846793005L + 1442695040888963407L);
		this.worldSeed = this.worldSeed + this.initSeed;
		this.worldSeed = this.worldSeed * (this.worldSeed * 6364136223846793005L + 1442695040888963407L);
		this.worldSeed = this.worldSeed + this.initSeed;
		this.worldSeed = this.worldSeed * (this.worldSeed * 6364136223846793005L + 1442695040888963407L);
		this.worldSeed = this.worldSeed + this.initSeed;
		this.noiseSampler = new PerlinNoiseSampler(new Random(seed));
	}

	@Override
	public void initSeed(long x, long y) {
		this.localSeed = this.worldSeed;
		this.localSeed = this.localSeed * (this.localSeed * 6364136223846793005L + 1442695040888963407L);
		this.localSeed += x;
		this.localSeed = this.localSeed * (this.localSeed * 6364136223846793005L + 1442695040888963407L);
		this.localSeed += y;
		this.localSeed = this.localSeed * (this.localSeed * 6364136223846793005L + 1442695040888963407L);
		this.localSeed += x;
		this.localSeed = this.localSeed * (this.localSeed * 6364136223846793005L + 1442695040888963407L);
		this.localSeed += y;
	}

	@Override
	public int nextInt(int bound) {
		int i = (int)((this.localSeed >> 24) % (long)bound);
		if (i < 0) {
			i += bound;
		}

		this.localSeed = this.localSeed * (this.localSeed * 6364136223846793005L + 1442695040888963407L);
		this.localSeed = this.localSeed + this.worldSeed;
		return i;
	}

	@Override
	public PerlinNoiseSampler getNoiseSampler() {
		return this.noiseSampler;
	}
}
