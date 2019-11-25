package net.minecraft.world.biome.layer.util;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import net.minecraft.util.math.ChunkPos;

public final class CachingLayerSampler implements LayerSampler {
	private final LayerOperator operator;
	private final Long2IntLinkedOpenHashMap cache;
	private final int cacheCapacity;

	public CachingLayerSampler(Long2IntLinkedOpenHashMap cache, int cacheCapacity, LayerOperator operator) {
		this.cache = cache;
		this.cacheCapacity = cacheCapacity;
		this.operator = operator;
	}

	@Override
	public int sample(int x, int z) {
		long l = ChunkPos.toLong(x, z);
		synchronized (this.cache) {
			int i = this.cache.get(l);
			if (i != Integer.MIN_VALUE) {
				return i;
			} else {
				int j = this.operator.apply(x, z);
				this.cache.put(l, j);
				if (this.cache.size() > this.cacheCapacity) {
					for (int k = 0; k < this.cacheCapacity / 16; k++) {
						this.cache.removeFirstInt();
					}
				}

				return j;
			}
		}
	}

	public int getCapacity() {
		return this.cacheCapacity;
	}
}
