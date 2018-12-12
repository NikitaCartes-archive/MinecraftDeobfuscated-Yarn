package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;

public abstract class WorldNibbleStorage<M extends WorldNibbleStorage<M>> {
	private final long[] cachedCoords = new long[2];
	private final ChunkNibbleArray[] cachedData = new ChunkNibbleArray[2];
	private boolean hasCache;
	protected final Long2ObjectOpenHashMap<ChunkNibbleArray> arraysByChunk;

	protected WorldNibbleStorage(Long2ObjectOpenHashMap<ChunkNibbleArray> long2ObjectOpenHashMap) {
		this.arraysByChunk = long2ObjectOpenHashMap;
		this.clearCache();
		this.hasCache = true;
	}

	public abstract M copy();

	public void cloneChunkData(long l) {
		this.arraysByChunk.put(l, this.arraysByChunk.get(l).method_12144());
		this.clearCache();
	}

	public boolean hasChunk(long l) {
		return this.arraysByChunk.containsKey(l);
	}

	@Nullable
	public ChunkNibbleArray getDataForChunk(long l) {
		if (this.hasCache) {
			for (int i = 0; i < 2; i++) {
				if (l == this.cachedCoords[i]) {
					return this.cachedData[i];
				}
			}
		}

		ChunkNibbleArray chunkNibbleArray = this.arraysByChunk.get(l);
		if (chunkNibbleArray == null) {
			return null;
		} else {
			if (this.hasCache) {
				for (int j = 1; j > 0; j--) {
					this.cachedCoords[j] = this.cachedCoords[j - 1];
					this.cachedData[j] = this.cachedData[j - 1];
				}

				this.cachedCoords[0] = l;
				this.cachedData[0] = chunkNibbleArray;
			}

			return chunkNibbleArray;
		}
	}

	public void removeChunk(long l) {
		this.arraysByChunk.remove(l);
	}

	public void addForChunk(long l, ChunkNibbleArray chunkNibbleArray) {
		this.arraysByChunk.put(l, chunkNibbleArray);
	}

	public void clearCache() {
		for (int i = 0; i < 2; i++) {
			this.cachedCoords[i] = Long.MAX_VALUE;
			this.cachedData[i] = null;
		}
	}

	public void disableCache() {
		this.hasCache = false;
	}
}
