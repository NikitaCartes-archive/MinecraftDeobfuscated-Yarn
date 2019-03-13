package net.minecraft.world.biome.layer;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import net.minecraft.world.chunk.ChunkPos;

public final class CachingLayerSampler implements LayerSampler {
	private final LayerOperator field_16035;
	private final Long2IntLinkedOpenHashMap cache;
	private final int cacheCapacity;

	public CachingLayerSampler(Long2IntLinkedOpenHashMap long2IntLinkedOpenHashMap, int i, LayerOperator layerOperator) {
		this.cache = long2IntLinkedOpenHashMap;
		this.cacheCapacity = i;
		this.field_16035 = layerOperator;
	}

	@Override
	public int sample(int i, int j) {
		long l = ChunkPos.toLong(i, j);
		synchronized (this.cache) {
			int k = this.cache.get(l);
			if (k != Integer.MIN_VALUE) {
				return k;
			} else {
				int m = this.field_16035.apply(i, j);
				this.cache.put(l, m);
				if (this.cache.size() > this.cacheCapacity) {
					for (int n = 0; n < this.cacheCapacity / 16; n++) {
						this.cache.removeFirstInt();
					}
				}

				return m;
			}
		}
	}

	public int getCapacity() {
		return this.cacheCapacity;
	}
}
