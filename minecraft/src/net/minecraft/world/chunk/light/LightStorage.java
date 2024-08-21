package net.minecraft.world.chunk.light;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkToNibbleArrayMap;

/**
 * LightStorage handles the access, storage and propagation of a specific kind of light within the world.
 * For example, separate instances will be used to store block light as opposed to sky light.
 * 
 * <p>The smallest unit within LightStorage is the section. Sections represent a cube of 16x16x16 blocks and their lighting data.
 * In turn, 16 sections stacked on top of each other form a column, which are analogous to the standard 16x256x16 world chunks.
 * 
 * <p>To avoid allocations, LightStorage packs all the coordinate arguments into single long values. Extra care should be taken
 * to ensure that the relevant types are being used where appropriate.
 * 
 * @see SkyLightStorage
 * @see BlockLightStorage
 */
public abstract class LightStorage<M extends ChunkToNibbleArrayMap<M>> {
	private final LightType lightType;
	protected final ChunkProvider chunkProvider;
	protected final Long2ByteMap sectionPropagations = new Long2ByteOpenHashMap();
	private final LongSet enabledColumns = new LongOpenHashSet();
	protected volatile M uncachedStorage;
	protected final M storage;
	protected final LongSet dirtySections = new LongOpenHashSet();
	protected final LongSet notifySections = new LongOpenHashSet();
	protected final Long2ObjectMap<ChunkNibbleArray> queuedSections = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap<>());
	private final LongSet columnsToRetain = new LongOpenHashSet();
	private final LongSet sectionsToRemove = new LongOpenHashSet();
	protected volatile boolean hasLightUpdates;

	protected LightStorage(LightType lightType, ChunkProvider chunkProvider, M lightData) {
		this.lightType = lightType;
		this.chunkProvider = chunkProvider;
		this.storage = lightData;
		this.uncachedStorage = lightData.copy();
		this.uncachedStorage.disableCache();
		this.sectionPropagations.defaultReturnValue((byte)0);
	}

	protected boolean hasSection(long sectionPos) {
		return this.getLightSection(sectionPos, true) != null;
	}

	@Nullable
	protected ChunkNibbleArray getLightSection(long sectionPos, boolean cached) {
		return this.getLightSection(cached ? this.storage : this.uncachedStorage, sectionPos);
	}

	@Nullable
	protected ChunkNibbleArray getLightSection(M storage, long sectionPos) {
		return storage.get(sectionPos);
	}

	@Nullable
	protected ChunkNibbleArray method_51547(long sectionPos) {
		ChunkNibbleArray chunkNibbleArray = this.storage.get(sectionPos);
		if (chunkNibbleArray == null) {
			return null;
		} else {
			if (this.dirtySections.add(sectionPos)) {
				chunkNibbleArray = chunkNibbleArray.copy();
				this.storage.put(sectionPos, chunkNibbleArray);
				this.storage.clearCache();
			}

			return chunkNibbleArray;
		}
	}

	@Nullable
	public ChunkNibbleArray getLightSection(long sectionPos) {
		ChunkNibbleArray chunkNibbleArray = this.queuedSections.get(sectionPos);
		return chunkNibbleArray != null ? chunkNibbleArray : this.getLightSection(sectionPos, false);
	}

	protected abstract int getLight(long blockPos);

	protected int get(long blockPos) {
		long l = ChunkSectionPos.fromBlockPos(blockPos);
		ChunkNibbleArray chunkNibbleArray = this.getLightSection(l, true);
		return chunkNibbleArray.get(
			ChunkSectionPos.getLocalCoord(BlockPos.unpackLongX(blockPos)),
			ChunkSectionPos.getLocalCoord(BlockPos.unpackLongY(blockPos)),
			ChunkSectionPos.getLocalCoord(BlockPos.unpackLongZ(blockPos))
		);
	}

	protected void set(long blockPos, int value) {
		long l = ChunkSectionPos.fromBlockPos(blockPos);
		ChunkNibbleArray chunkNibbleArray;
		if (this.dirtySections.add(l)) {
			chunkNibbleArray = this.storage.replaceWithCopy(l);
		} else {
			chunkNibbleArray = this.getLightSection(l, true);
		}

		chunkNibbleArray.set(
			ChunkSectionPos.getLocalCoord(BlockPos.unpackLongX(blockPos)),
			ChunkSectionPos.getLocalCoord(BlockPos.unpackLongY(blockPos)),
			ChunkSectionPos.getLocalCoord(BlockPos.unpackLongZ(blockPos)),
			value
		);
		ChunkSectionPos.forEachChunkSectionAround(blockPos, this.notifySections::add);
	}

	protected void addNotifySections(long id) {
		int i = ChunkSectionPos.unpackX(id);
		int j = ChunkSectionPos.unpackY(id);
		int k = ChunkSectionPos.unpackZ(id);

		for (int l = -1; l <= 1; l++) {
			for (int m = -1; m <= 1; m++) {
				for (int n = -1; n <= 1; n++) {
					this.notifySections.add(ChunkSectionPos.asLong(i + m, j + n, k + l));
				}
			}
		}
	}

	protected ChunkNibbleArray createSection(long sectionPos) {
		ChunkNibbleArray chunkNibbleArray = this.queuedSections.get(sectionPos);
		return chunkNibbleArray != null ? chunkNibbleArray : new ChunkNibbleArray();
	}

	protected boolean hasLightUpdates() {
		return this.hasLightUpdates;
	}

	protected void updateLight(ChunkLightProvider<M, ?> lightProvider) {
		if (this.hasLightUpdates) {
			this.hasLightUpdates = false;
			LongIterator objectIterator = this.sectionsToRemove.iterator();

			while (objectIterator.hasNext()) {
				long l = (Long)objectIterator.next();
				ChunkNibbleArray chunkNibbleArray = this.queuedSections.remove(l);
				ChunkNibbleArray chunkNibbleArray2 = this.storage.removeChunk(l);
				if (this.columnsToRetain.contains(ChunkSectionPos.withZeroY(l))) {
					if (chunkNibbleArray != null) {
						this.queuedSections.put(l, chunkNibbleArray);
					} else if (chunkNibbleArray2 != null) {
						this.queuedSections.put(l, chunkNibbleArray2);
					}
				}
			}

			this.storage.clearCache();
			objectIterator = this.sectionsToRemove.iterator();

			while (objectIterator.hasNext()) {
				long l = (Long)objectIterator.next();
				this.onUnloadSection(l);
				this.dirtySections.add(l);
			}

			this.sectionsToRemove.clear();
			ObjectIterator<Entry<ChunkNibbleArray>> objectIteratorx = Long2ObjectMaps.fastIterator(this.queuedSections);

			while (objectIteratorx.hasNext()) {
				Entry<ChunkNibbleArray> entry = (Entry<ChunkNibbleArray>)objectIteratorx.next();
				long m = entry.getLongKey();
				if (this.hasSection(m)) {
					ChunkNibbleArray chunkNibbleArray2 = (ChunkNibbleArray)entry.getValue();
					if (this.storage.get(m) != chunkNibbleArray2) {
						this.storage.put(m, chunkNibbleArray2);
						this.dirtySections.add(m);
					}

					objectIteratorx.remove();
				}
			}

			this.storage.clearCache();
		}
	}

	protected void onLoadSection(long sectionPos) {
	}

	protected void onUnloadSection(long sectionPos) {
	}

	protected void setColumnEnabled(long columnPos, boolean enabled) {
		if (enabled) {
			this.enabledColumns.add(columnPos);
		} else {
			this.enabledColumns.remove(columnPos);
		}
	}

	protected boolean isSectionInEnabledColumn(long sectionPos) {
		long l = ChunkSectionPos.withZeroY(sectionPos);
		return this.enabledColumns.contains(l);
	}

	protected boolean isColumnEnabled(long columnPos) {
		return this.enabledColumns.contains(columnPos);
	}

	public void setRetainColumn(long sectionPos, boolean retain) {
		if (retain) {
			this.columnsToRetain.add(sectionPos);
		} else {
			this.columnsToRetain.remove(sectionPos);
		}
	}

	protected void enqueueSectionData(long sectionPos, @Nullable ChunkNibbleArray array) {
		if (array != null) {
			this.queuedSections.put(sectionPos, array);
			this.hasLightUpdates = true;
		} else {
			this.queuedSections.remove(sectionPos);
		}
	}

	protected void setSectionStatus(long sectionPos, boolean notReady) {
		byte b = this.sectionPropagations.get(sectionPos);
		byte c = LightStorage.PropagationFlags.setReady(b, !notReady);
		if (b != c) {
			this.setSectionPropagation(sectionPos, c);
			int i = notReady ? -1 : 1;

			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					for (int l = -1; l <= 1; l++) {
						if (j != 0 || k != 0 || l != 0) {
							long m = ChunkSectionPos.offset(sectionPos, j, k, l);
							byte d = this.sectionPropagations.get(m);
							this.setSectionPropagation(m, LightStorage.PropagationFlags.withNeighborCount(d, LightStorage.PropagationFlags.getNeighborCount(d) + i));
						}
					}
				}
			}
		}
	}

	protected void setSectionPropagation(long sectionPos, byte flags) {
		if (flags != 0) {
			if (this.sectionPropagations.put(sectionPos, flags) == 0) {
				this.queueForUpdate(sectionPos);
			}
		} else if (this.sectionPropagations.remove(sectionPos) != 0) {
			this.queueForRemoval(sectionPos);
		}
	}

	private void queueForUpdate(long sectionPos) {
		if (!this.sectionsToRemove.remove(sectionPos)) {
			this.storage.put(sectionPos, this.createSection(sectionPos));
			this.dirtySections.add(sectionPos);
			this.onLoadSection(sectionPos);
			this.addNotifySections(sectionPos);
			this.hasLightUpdates = true;
		}
	}

	private void queueForRemoval(long sectionPos) {
		this.sectionsToRemove.add(sectionPos);
		this.hasLightUpdates = true;
	}

	protected void notifyChanges() {
		if (!this.dirtySections.isEmpty()) {
			M chunkToNibbleArrayMap = this.storage.copy();
			chunkToNibbleArrayMap.disableCache();
			this.uncachedStorage = chunkToNibbleArrayMap;
			this.dirtySections.clear();
		}

		if (!this.notifySections.isEmpty()) {
			LongIterator longIterator = this.notifySections.iterator();

			while (longIterator.hasNext()) {
				long l = longIterator.nextLong();
				this.chunkProvider.onLightUpdate(this.lightType, ChunkSectionPos.from(l));
			}

			this.notifySections.clear();
		}
	}

	public LightStorage.Status getStatus(long sectionPos) {
		return LightStorage.PropagationFlags.getStatus(this.sectionPropagations.get(sectionPos));
	}

	protected static class PropagationFlags {
		public static final byte field_44719 = 0;
		private static final int MIN_NEIGHBOR_COUNT = 0;
		private static final int MAX_NEIGHBOR_COUNT = 26;
		private static final byte field_44722 = 32;
		private static final byte NEIGHBOR_COUNT_MASK = 31;

		public static byte setReady(byte packed, boolean ready) {
			return (byte)(ready ? packed | 32 : packed & -33);
		}

		public static byte withNeighborCount(byte packed, int neighborCount) {
			if (neighborCount >= 0 && neighborCount <= 26) {
				return (byte)(packed & -32 | neighborCount & 31);
			} else {
				throw new IllegalArgumentException("Neighbor count was not within range [0; 26]");
			}
		}

		public static boolean isReady(byte packed) {
			return (packed & 32) != 0;
		}

		public static int getNeighborCount(byte packed) {
			return packed & 31;
		}

		public static LightStorage.Status getStatus(byte packed) {
			if (packed == 0) {
				return LightStorage.Status.EMPTY;
			} else {
				return isReady(packed) ? LightStorage.Status.LIGHT_AND_DATA : LightStorage.Status.LIGHT_ONLY;
			}
		}
	}

	public static enum Status {
		EMPTY("2"),
		LIGHT_ONLY("1"),
		LIGHT_AND_DATA("0");

		private final String sigil;

		private Status(final String sigil) {
			this.sigil = sigil;
		}

		public String getSigil() {
			return this.sigil;
		}
	}
}
