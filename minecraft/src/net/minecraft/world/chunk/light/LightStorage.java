package net.minecraft.world.chunk.light;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import javax.annotation.Nullable;
import net.minecraft.class_4076;
import net.minecraft.class_4079;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.WorldNibbleStorage;

public abstract class LightStorage<M extends WorldNibbleStorage<M>> extends class_4079 {
	protected static final ChunkNibbleArray EMPTY = new ChunkNibbleArray();
	private static final Direction[] DIRECTIONS = Direction.values();
	private final LightType lightType;
	private final ChunkProvider chunkProvider;
	protected final LongSet field_15808 = new LongOpenHashSet();
	protected final LongSet field_15797 = new LongOpenHashSet();
	protected final LongSet field_15804 = new LongOpenHashSet();
	protected volatile M dataStorageUncached;
	protected final M dataStorage;
	protected final LongSet field_15802 = new LongOpenHashSet();
	protected final LongSet toNotify = new LongOpenHashSet();
	protected final Long2ObjectMap<ChunkNibbleArray> toUpdate = new Long2ObjectOpenHashMap<>();
	private final LongSet toRemove = new LongOpenHashSet();
	protected volatile boolean hasLightUpdates;

	protected LightStorage(LightType lightType, ChunkProvider chunkProvider, M worldNibbleStorage) {
		super(3, 16, 256);
		this.lightType = lightType;
		this.chunkProvider = chunkProvider;
		this.dataStorage = worldNibbleStorage;
		this.dataStorageUncached = worldNibbleStorage.copy();
		this.dataStorageUncached.disableCache();
	}

	protected boolean hasChunk(long l) {
		return this.getDataForChunk(l, true) != null;
	}

	@Nullable
	protected ChunkNibbleArray getDataForChunk(long l, boolean bl) {
		return this.getDataForChunk(bl ? this.dataStorage : this.dataStorageUncached, l);
	}

	@Nullable
	protected ChunkNibbleArray getDataForChunk(M worldNibbleStorage, long l) {
		return worldNibbleStorage.getDataForChunk(l);
	}

	protected abstract int getLight(long l);

	protected int get(long l) {
		long m = class_4076.method_18691(l);
		ChunkNibbleArray chunkNibbleArray = this.getDataForChunk(m, true);
		return chunkNibbleArray.get(
			class_4076.method_18684(BlockPos.unpackLongX(l)), class_4076.method_18684(BlockPos.unpackLongY(l)), class_4076.method_18684(BlockPos.unpackLongZ(l))
		);
	}

	protected void set(long l, int i) {
		long m = class_4076.method_18691(l);
		if (this.field_15802.add(m)) {
			this.dataStorage.cloneChunkData(m);
		}

		ChunkNibbleArray chunkNibbleArray = this.getDataForChunk(m, true);
		chunkNibbleArray.set(
			class_4076.method_18684(BlockPos.unpackLongX(l)), class_4076.method_18684(BlockPos.unpackLongY(l)), class_4076.method_18684(BlockPos.unpackLongZ(l)), i
		);

		for (int j = -1; j <= 1; j++) {
			for (int k = -1; k <= 1; k++) {
				for (int n = -1; n <= 1; n++) {
					this.toNotify.add(class_4076.method_18691(BlockPos.add(l, k, n, j)));
				}
			}
		}
	}

	@Override
	protected int getCurrentLevelFor(long l) {
		if (l == Long.MAX_VALUE) {
			return 2;
		} else if (this.field_15808.contains(l)) {
			return 0;
		} else {
			return !this.toRemove.contains(l) && this.dataStorage.hasChunk(l) ? 1 : 2;
		}
	}

	@Override
	protected int method_18749(long l) {
		if (this.field_15797.contains(l)) {
			return 2;
		} else {
			return !this.field_15808.contains(l) && !this.field_15804.contains(l) ? 2 : 0;
		}
	}

	@Override
	protected void setLevelFor(long l, int i) {
		int j = this.getCurrentLevelFor(l);
		if (j != 0 && i == 0) {
			this.field_15808.add(l);
			this.field_15804.remove(l);
		}

		if (j == 0 && i != 0) {
			this.field_15808.remove(l);
			this.field_15797.remove(l);
		}

		if (j >= 2 && i != 2) {
			if (this.toRemove.contains(l)) {
				this.toRemove.remove(l);
			} else {
				this.dataStorage.addForChunk(l, this.getDataForChunk(l));
				this.field_15802.add(l);
				this.method_15523(l);

				for (int k = -1; k <= 1; k++) {
					for (int m = -1; m <= 1; m++) {
						for (int n = -1; n <= 1; n++) {
							this.toNotify.add(class_4076.method_18691(BlockPos.add(l, m, n, k)));
						}
					}
				}
			}
		}

		if (j != 2 && i >= 2) {
			this.toRemove.add(l);
		}

		this.hasLightUpdates = !this.toRemove.isEmpty();
	}

	private ChunkNibbleArray getDataForChunk(long l) {
		ChunkNibbleArray chunkNibbleArray = this.toUpdate.get(l);
		return chunkNibbleArray != null ? chunkNibbleArray : new ChunkNibbleArray();
	}

	protected void removeChunkData(ChunkLightProvider<?, ?> chunkLightProvider, long l) {
		int i = class_4076.method_18688(class_4076.method_18686(l));
		int j = class_4076.method_18688(class_4076.method_18689(l));
		int k = class_4076.method_18688(class_4076.method_18690(l));

		for (int m = 0; m < 16; m++) {
			for (int n = 0; n < 16; n++) {
				for (int o = 0; o < 16; o++) {
					long p = BlockPos.asLong(i + m, j + n, k + o);
					chunkLightProvider.remove(p);
				}
			}
		}
	}

	protected boolean hasLightUpdates() {
		return this.hasLightUpdates;
	}

	protected void processUpdates(ChunkLightProvider<M, ?> chunkLightProvider, boolean bl, boolean bl2) {
		if (this.hasLightUpdates() || !this.toUpdate.isEmpty()) {
			LongIterator objectIterator = this.toRemove.iterator();

			while (objectIterator.hasNext()) {
				long l = (Long)objectIterator.next();
				this.toUpdate.remove(l);
				this.removeChunkData(chunkLightProvider, l);
				this.dataStorage.removeChunk(l);
			}

			this.dataStorage.clearCache();
			objectIterator = this.toRemove.iterator();

			while (objectIterator.hasNext()) {
				long l = (Long)objectIterator.next();
				this.onChunkRemoved(l);
			}

			this.toRemove.clear();
			this.hasLightUpdates = false;

			for (Entry<ChunkNibbleArray> entry : this.toUpdate.long2ObjectEntrySet()) {
				long m = entry.getLongKey();
				if (this.hasChunk(m)) {
					ChunkNibbleArray chunkNibbleArray = (ChunkNibbleArray)entry.getValue();
					if (this.dataStorage.getDataForChunk(m) != chunkNibbleArray) {
						this.removeChunkData(chunkLightProvider, m);
						this.dataStorage.addForChunk(m, chunkNibbleArray);
						this.field_15802.add(m);
					}
				}
			}

			this.dataStorage.clearCache();
			if (!bl2) {
				objectIterator = this.toUpdate.keySet().iterator();

				while (objectIterator.hasNext()) {
					long l = (Long)objectIterator.next();
					if (this.hasChunk(l)) {
						int i = class_4076.method_18688(class_4076.method_18686(l));
						int j = class_4076.method_18688(class_4076.method_18689(l));
						int k = class_4076.method_18688(class_4076.method_18690(l));

						for (Direction direction : DIRECTIONS) {
							long n = class_4076.method_18679(l, direction);
							if (!this.toUpdate.containsKey(n) && this.hasChunk(n)) {
								for (int o = 0; o < 16; o++) {
									for (int p = 0; p < 16; p++) {
										long q;
										long r;
										switch (direction) {
											case DOWN:
												q = BlockPos.asLong(i + p, j, k + o);
												r = BlockPos.asLong(i + p, j - 1, k + o);
												break;
											case UP:
												q = BlockPos.asLong(i + p, j + 16 - 1, k + o);
												r = BlockPos.asLong(i + p, j + 16, k + o);
												break;
											case NORTH:
												q = BlockPos.asLong(i + o, j + p, k);
												r = BlockPos.asLong(i + o, j + p, k - 1);
												break;
											case SOUTH:
												q = BlockPos.asLong(i + o, j + p, k + 16 - 1);
												r = BlockPos.asLong(i + o, j + p, k + 16);
												break;
											case WEST:
												q = BlockPos.asLong(i, j + o, k + p);
												r = BlockPos.asLong(i - 1, j + o, k + p);
												break;
											default:
												q = BlockPos.asLong(i + 16 - 1, j + o, k + p);
												r = BlockPos.asLong(i + 16, j + o, k + p);
										}

										chunkLightProvider.scheduleNewLevelUpdate(q, r, chunkLightProvider.getBaseLevelFor(q, r, chunkLightProvider.getCurrentLevelFor(q)), false);
										chunkLightProvider.scheduleNewLevelUpdate(r, q, chunkLightProvider.getBaseLevelFor(r, q, chunkLightProvider.getCurrentLevelFor(r)), false);
									}
								}
							}
						}
					}
				}
			}

			ObjectIterator<Entry<ChunkNibbleArray>> objectIteratorx = this.toUpdate.long2ObjectEntrySet().iterator();

			while (objectIteratorx.hasNext()) {
				Entry<ChunkNibbleArray> entryx = (Entry<ChunkNibbleArray>)objectIteratorx.next();
				long m = entryx.getLongKey();
				if (this.hasChunk(m)) {
					objectIteratorx.remove();
				}
			}
		}
	}

	protected void method_15523(long l) {
	}

	protected void onChunkRemoved(long l) {
	}

	protected void method_15535(long l, boolean bl) {
	}

	protected void scheduleToUpdate(long l, ChunkNibbleArray chunkNibbleArray) {
		this.toUpdate.put(l, chunkNibbleArray);
	}

	protected void scheduleChunkLightUpdate(long l, boolean bl) {
		boolean bl2 = this.field_15808.contains(l);
		if (!bl2 && !bl) {
			this.field_15804.add(l);
			this.scheduleNewLevelUpdate(Long.MAX_VALUE, l, 0, true);
		}

		if (bl2 && bl) {
			this.field_15797.add(l);
			this.scheduleNewLevelUpdate(Long.MAX_VALUE, l, 2, false);
		}
	}

	protected void updateAll() {
		if (this.hasLevelUpdates()) {
			this.updateLevels(Integer.MAX_VALUE);
		}
	}

	protected void notifyChunkProvider() {
		if (!this.field_15802.isEmpty()) {
			M worldNibbleStorage = this.dataStorage.copy();
			worldNibbleStorage.disableCache();
			this.dataStorageUncached = worldNibbleStorage;
			this.field_15802.clear();
		}

		if (!this.toNotify.isEmpty()) {
			LongIterator longIterator = this.toNotify.iterator();

			while (longIterator.hasNext()) {
				long l = longIterator.nextLong();
				this.chunkProvider.onLightUpdate(this.lightType, class_4076.method_18677(l));
			}

			this.toNotify.clear();
		}
	}
}
