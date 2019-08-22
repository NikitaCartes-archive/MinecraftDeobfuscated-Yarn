package net.minecraft.world.biome.layer;

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

	public CachingLayerContext(int i, long l, long m) {
		this.initSeed = m;
		this.initSeed = this.initSeed * (this.initSeed * 6364136223846793005L + 1442695040888963407L);
		this.initSeed += m;
		this.initSeed = this.initSeed * (this.initSeed * 6364136223846793005L + 1442695040888963407L);
		this.initSeed += m;
		this.initSeed = this.initSeed * (this.initSeed * 6364136223846793005L + 1442695040888963407L);
		this.initSeed += m;
		this.cache = new Long2IntLinkedOpenHashMap(16, 0.25F);
		this.cache.defaultReturnValue(Integer.MIN_VALUE);
		this.cacheCapacity = i;
		this.initWorldSeed(l);
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

	public void initWorldSeed(long l) {
		this.worldSeed = l;
		this.worldSeed = this.worldSeed * (this.worldSeed * 6364136223846793005L + 1442695040888963407L);
		this.worldSeed = this.worldSeed + this.initSeed;
		this.worldSeed = this.worldSeed * (this.worldSeed * 6364136223846793005L + 1442695040888963407L);
		this.worldSeed = this.worldSeed + this.initSeed;
		this.worldSeed = this.worldSeed * (this.worldSeed * 6364136223846793005L + 1442695040888963407L);
		this.worldSeed = this.worldSeed + this.initSeed;
		this.noiseSampler = new PerlinNoiseSampler(new Random(l));
	}

	@Override
	public void initSeed(long l, long m) {
		this.localSeed = this.worldSeed;
		this.localSeed = this.localSeed * (this.localSeed * 6364136223846793005L + 1442695040888963407L);
		this.localSeed += l;
		this.localSeed = this.localSeed * (this.localSeed * 6364136223846793005L + 1442695040888963407L);
		this.localSeed += m;
		this.localSeed = this.localSeed * (this.localSeed * 6364136223846793005L + 1442695040888963407L);
		this.localSeed += l;
		this.localSeed = this.localSeed * (this.localSeed * 6364136223846793005L + 1442695040888963407L);
		this.localSeed += m;
	}

	@Override
	public int nextInt(int i) {
		int j = (int)((this.localSeed >> 24) % (long)i);
		if (j < 0) {
			j += i;
		}

		this.localSeed = this.localSeed * (this.localSeed * 6364136223846793005L + 1442695040888963407L);
		this.localSeed = this.localSeed + this.worldSeed;
		return j;
	}

	@Override
	public PerlinNoiseSampler getNoiseSampler() {
		return this.noiseSampler;
	}
}
