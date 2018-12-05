package net.minecraft.world.biome.layer;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.Random;
import net.minecraft.util.math.noise.PerlinNoiseSampler;

public class CachingLayerContext implements LayerSampleContext<CachingLayerSampler> {
	private final Long2IntLinkedOpenHashMap cache;
	private final int field_16044;
	protected long field_16731;
	protected PerlinNoiseSampler noiseSampler;
	private long worldSeed;
	private long localSeed;

	public CachingLayerContext(int i, long l, long m) {
		this.field_16731 = m;
		this.field_16731 = this.field_16731 * (this.field_16731 * 6364136223846793005L + 1442695040888963407L);
		this.field_16731 += m;
		this.field_16731 = this.field_16731 * (this.field_16731 * 6364136223846793005L + 1442695040888963407L);
		this.field_16731 += m;
		this.field_16731 = this.field_16731 * (this.field_16731 * 6364136223846793005L + 1442695040888963407L);
		this.field_16731 += m;
		this.cache = new Long2IntLinkedOpenHashMap(16, 0.25F);
		this.cache.defaultReturnValue(Integer.MIN_VALUE);
		this.field_16044 = i;
		this.initWorldSeed(l);
	}

	public CachingLayerSampler method_15837(LayerOperator layerOperator) {
		return new CachingLayerSampler(this.cache, this.field_16044, layerOperator);
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
		this.worldSeed = this.worldSeed + this.field_16731;
		this.worldSeed = this.worldSeed * (this.worldSeed * 6364136223846793005L + 1442695040888963407L);
		this.worldSeed = this.worldSeed + this.field_16731;
		this.worldSeed = this.worldSeed * (this.worldSeed * 6364136223846793005L + 1442695040888963407L);
		this.worldSeed = this.worldSeed + this.field_16731;
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
