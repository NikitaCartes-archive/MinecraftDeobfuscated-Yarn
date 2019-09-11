package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;

public abstract class ChunkToNibbleArrayMap<M extends ChunkToNibbleArrayMap<M>> {
	private final long[] cachePositions = new long[2];
	private final ChunkNibbleArray[] cacheArrays = new ChunkNibbleArray[2];
	private boolean cacheEnabled;
	protected final Long2ObjectOpenHashMap<ChunkNibbleArray> arrays;

	protected ChunkToNibbleArrayMap(Long2ObjectOpenHashMap<ChunkNibbleArray> long2ObjectOpenHashMap) {
		this.arrays = long2ObjectOpenHashMap;
		this.clearCache();
		this.cacheEnabled = true;
	}

	public abstract M copy();

	public void replaceWithCopy(long l) {
		this.arrays.put(l, this.arrays.get(l).copy());
		this.clearCache();
	}

	public boolean containsKey(long l) {
		return this.arrays.containsKey(l);
	}

	@Nullable
	public ChunkNibbleArray get(long l) {
		if (this.cacheEnabled) {
			for (int i = 0; i < 2; i++) {
				if (l == this.cachePositions[i]) {
					return this.cacheArrays[i];
				}
			}
		}

		ChunkNibbleArray chunkNibbleArray = this.arrays.get(l);
		if (chunkNibbleArray == null) {
			return null;
		} else {
			if (this.cacheEnabled) {
				for (int j = 1; j > 0; j--) {
					this.cachePositions[j] = this.cachePositions[j - 1];
					this.cacheArrays[j] = this.cacheArrays[j - 1];
				}

				this.cachePositions[0] = l;
				this.cacheArrays[0] = chunkNibbleArray;
			}

			return chunkNibbleArray;
		}
	}

	@Nullable
	public ChunkNibbleArray removeChunk(long l) {
		return this.arrays.remove(l);
	}

	public void put(long l, ChunkNibbleArray chunkNibbleArray) {
		this.arrays.put(l, chunkNibbleArray);
	}

	public void clearCache() {
		for (int i = 0; i < 2; i++) {
			this.cachePositions[i] = Long.MAX_VALUE;
			this.cacheArrays[i] = null;
		}
	}

	public void disableCache() {
		this.cacheEnabled = false;
	}
}
