package net.minecraft.world.chunk.light;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Arrays;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkToNibbleArrayMap;
import net.minecraft.world.chunk.ColumnChunkNibbleArray;

public class SkyLightStorage extends LightStorage<SkyLightStorage.Data> {
	private static final Direction[] LIGHT_REDUCTION_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
	private final LongSet field_15820 = new LongOpenHashSet();
	private final LongSet sectionsToUpdate = new LongOpenHashSet();
	private final LongSet sectionsToRemove = new LongOpenHashSet();
	private final LongSet enabledColumns = new LongOpenHashSet();
	private volatile boolean hasUpdates;

	protected SkyLightStorage(ChunkProvider chunkProvider) {
		super(LightType.SKY, chunkProvider, new SkyLightStorage.Data(new Long2ObjectOpenHashMap<>(), new Long2IntOpenHashMap(), Integer.MAX_VALUE));
	}

	@Override
	protected int getLight(long blockPos) {
		long l = ChunkSectionPos.fromBlockPos(blockPos);
		int i = ChunkSectionPos.getY(l);
		SkyLightStorage.Data data = this.uncachedStorage;
		int j = data.columnToTopSection.get(ChunkSectionPos.withZeroZ(l));
		if (j != data.minSectionY && i < j) {
			ChunkNibbleArray chunkNibbleArray = this.getLightSection(data, l);
			if (chunkNibbleArray == null) {
				for (blockPos = BlockPos.removeChunkSectionLocalY(blockPos); chunkNibbleArray == null; chunkNibbleArray = this.getLightSection(data, l)) {
					l = ChunkSectionPos.offset(l, Direction.UP);
					if (++i >= j) {
						return 15;
					}

					blockPos = BlockPos.add(blockPos, 0, 16, 0);
				}
			}

			return chunkNibbleArray.get(
				ChunkSectionPos.getLocalCoord(BlockPos.unpackLongX(blockPos)),
				ChunkSectionPos.getLocalCoord(BlockPos.unpackLongY(blockPos)),
				ChunkSectionPos.getLocalCoord(BlockPos.unpackLongZ(blockPos))
			);
		} else {
			return 15;
		}
	}

	@Override
	protected void onLoadSection(long sectionPos) {
		int i = ChunkSectionPos.getY(sectionPos);
		if (this.storage.minSectionY > i) {
			this.storage.minSectionY = i;
			this.storage.columnToTopSection.defaultReturnValue(this.storage.minSectionY);
		}

		long l = ChunkSectionPos.withZeroZ(sectionPos);
		int j = this.storage.columnToTopSection.get(l);
		if (j < i + 1) {
			this.storage.columnToTopSection.put(l, i + 1);
			if (this.enabledColumns.contains(l)) {
				this.enqueueAddSection(sectionPos);
				if (j > this.storage.minSectionY) {
					long m = ChunkSectionPos.asLong(ChunkSectionPos.getX(sectionPos), j - 1, ChunkSectionPos.getZ(sectionPos));
					this.enqueueRemoveSection(m);
				}

				this.checkForUpdates();
			}
		}
	}

	private void enqueueRemoveSection(long sectionPos) {
		this.sectionsToRemove.add(sectionPos);
		this.sectionsToUpdate.remove(sectionPos);
	}

	private void enqueueAddSection(long sectionPos) {
		this.sectionsToUpdate.add(sectionPos);
		this.sectionsToRemove.remove(sectionPos);
	}

	private void checkForUpdates() {
		this.hasUpdates = !this.sectionsToUpdate.isEmpty() || !this.sectionsToRemove.isEmpty();
	}

	@Override
	protected void onUnloadSection(long sectionPos) {
		long l = ChunkSectionPos.withZeroZ(sectionPos);
		boolean bl = this.enabledColumns.contains(l);
		if (bl) {
			this.enqueueRemoveSection(sectionPos);
		}

		int i = ChunkSectionPos.getY(sectionPos);
		if (this.storage.columnToTopSection.get(l) == i + 1) {
			long m;
			for (m = sectionPos; !this.hasSection(m) && this.isAboveMinHeight(i); m = ChunkSectionPos.offset(m, Direction.DOWN)) {
				i--;
			}

			if (this.hasSection(m)) {
				this.storage.columnToTopSection.put(l, i + 1);
				if (bl) {
					this.enqueueAddSection(m);
				}
			} else {
				this.storage.columnToTopSection.remove(l);
			}
		}

		if (bl) {
			this.checkForUpdates();
		}
	}

	@Override
	protected void setColumnEnabled(long columnPos, boolean enabled) {
		this.updateAll();
		if (enabled && this.enabledColumns.add(columnPos)) {
			int i = this.storage.columnToTopSection.get(columnPos);
			if (i != this.storage.minSectionY) {
				long l = ChunkSectionPos.asLong(ChunkSectionPos.getX(columnPos), i - 1, ChunkSectionPos.getZ(columnPos));
				this.enqueueAddSection(l);
				this.checkForUpdates();
			}
		} else if (!enabled) {
			this.enabledColumns.remove(columnPos);
		}
	}

	@Override
	protected boolean hasLightUpdates() {
		return super.hasLightUpdates() || this.hasUpdates;
	}

	@Override
	protected ChunkNibbleArray createSection(long sectionPos) {
		ChunkNibbleArray chunkNibbleArray = this.queuedSections.get(sectionPos);
		if (chunkNibbleArray != null) {
			return chunkNibbleArray;
		} else {
			long l = ChunkSectionPos.offset(sectionPos, Direction.UP);
			int i = this.storage.columnToTopSection.get(ChunkSectionPos.withZeroZ(sectionPos));
			if (i != this.storage.minSectionY && ChunkSectionPos.getY(l) < i) {
				ChunkNibbleArray chunkNibbleArray2;
				while ((chunkNibbleArray2 = this.getLightSection(l, true)) == null) {
					l = ChunkSectionPos.offset(l, Direction.UP);
				}

				return new ChunkNibbleArray(new ColumnChunkNibbleArray(chunkNibbleArray2, 0).asByteArray());
			} else {
				return new ChunkNibbleArray();
			}
		}
	}

	@Override
	protected void updateLight(ChunkLightProvider<SkyLightStorage.Data, ?> lightProvider, boolean doSkylight, boolean skipEdgeLightPropagation) {
		super.updateLight(lightProvider, doSkylight, skipEdgeLightPropagation);
		if (doSkylight) {
			if (!this.sectionsToUpdate.isEmpty()) {
				LongIterator var4 = this.sectionsToUpdate.iterator();

				while (var4.hasNext()) {
					long l = (Long)var4.next();
					int i = this.getLevel(l);
					if (i != 2 && !this.sectionsToRemove.contains(l) && this.field_15820.add(l)) {
						if (i == 1) {
							this.removeSection(lightProvider, l);
							if (this.dirtySections.add(l)) {
								this.storage.replaceWithCopy(l);
							}

							Arrays.fill(this.getLightSection(l, true).asByteArray(), (byte)-1);
							int j = ChunkSectionPos.getBlockCoord(ChunkSectionPos.getX(l));
							int k = ChunkSectionPos.getBlockCoord(ChunkSectionPos.getY(l));
							int m = ChunkSectionPos.getBlockCoord(ChunkSectionPos.getZ(l));

							for (Direction direction : LIGHT_REDUCTION_DIRECTIONS) {
								long n = ChunkSectionPos.offset(l, direction);
								if ((this.sectionsToRemove.contains(n) || !this.field_15820.contains(n) && !this.sectionsToUpdate.contains(n)) && this.hasSection(n)) {
									for (int o = 0; o < 16; o++) {
										for (int p = 0; p < 16; p++) {
											long q;
											long r;
											switch (direction) {
												case NORTH:
													q = BlockPos.asLong(j + o, k + p, m);
													r = BlockPos.asLong(j + o, k + p, m - 1);
													break;
												case SOUTH:
													q = BlockPos.asLong(j + o, k + p, m + 16 - 1);
													r = BlockPos.asLong(j + o, k + p, m + 16);
													break;
												case WEST:
													q = BlockPos.asLong(j, k + o, m + p);
													r = BlockPos.asLong(j - 1, k + o, m + p);
													break;
												default:
													q = BlockPos.asLong(j + 16 - 1, k + o, m + p);
													r = BlockPos.asLong(j + 16, k + o, m + p);
											}

											lightProvider.updateLevel(q, r, lightProvider.getPropagatedLevel(q, r, 0), true);
										}
									}
								}
							}

							for (int s = 0; s < 16; s++) {
								for (int t = 0; t < 16; t++) {
									long u = BlockPos.asLong(
										ChunkSectionPos.getBlockCoord(ChunkSectionPos.getX(l)) + s,
										ChunkSectionPos.getBlockCoord(ChunkSectionPos.getY(l)),
										ChunkSectionPos.getBlockCoord(ChunkSectionPos.getZ(l)) + t
									);
									long n = BlockPos.asLong(
										ChunkSectionPos.getBlockCoord(ChunkSectionPos.getX(l)) + s,
										ChunkSectionPos.getBlockCoord(ChunkSectionPos.getY(l)) - 1,
										ChunkSectionPos.getBlockCoord(ChunkSectionPos.getZ(l)) + t
									);
									lightProvider.updateLevel(u, n, lightProvider.getPropagatedLevel(u, n, 0), true);
								}
							}
						} else {
							for (int j = 0; j < 16; j++) {
								for (int k = 0; k < 16; k++) {
									long v = BlockPos.asLong(
										ChunkSectionPos.getBlockCoord(ChunkSectionPos.getX(l)) + j,
										ChunkSectionPos.getBlockCoord(ChunkSectionPos.getY(l)) + 16 - 1,
										ChunkSectionPos.getBlockCoord(ChunkSectionPos.getZ(l)) + k
									);
									lightProvider.updateLevel(Long.MAX_VALUE, v, 0, true);
								}
							}
						}
					}
				}
			}

			this.sectionsToUpdate.clear();
			if (!this.sectionsToRemove.isEmpty()) {
				LongIterator var23 = this.sectionsToRemove.iterator();

				while (var23.hasNext()) {
					long l = (Long)var23.next();
					if (this.field_15820.remove(l) && this.hasSection(l)) {
						for (int i = 0; i < 16; i++) {
							for (int j = 0; j < 16; j++) {
								long w = BlockPos.asLong(
									ChunkSectionPos.getBlockCoord(ChunkSectionPos.getX(l)) + i,
									ChunkSectionPos.getBlockCoord(ChunkSectionPos.getY(l)) + 16 - 1,
									ChunkSectionPos.getBlockCoord(ChunkSectionPos.getZ(l)) + j
								);
								lightProvider.updateLevel(Long.MAX_VALUE, w, 15, false);
							}
						}
					}
				}
			}

			this.sectionsToRemove.clear();
			this.hasUpdates = false;
		}
	}

	protected boolean isAboveMinHeight(int sectionY) {
		return sectionY >= this.storage.minSectionY;
	}

	protected boolean isTopmostBlock(long blockPos) {
		int i = BlockPos.unpackLongY(blockPos);
		if ((i & 15) != 15) {
			return false;
		} else {
			long l = ChunkSectionPos.fromBlockPos(blockPos);
			long m = ChunkSectionPos.withZeroZ(l);
			if (!this.enabledColumns.contains(m)) {
				return false;
			} else {
				int j = this.storage.columnToTopSection.get(m);
				return ChunkSectionPos.getBlockCoord(j) == i + 16;
			}
		}
	}

	protected boolean isAtOrAboveTopmostSection(long sectionPos) {
		long l = ChunkSectionPos.withZeroZ(sectionPos);
		int i = this.storage.columnToTopSection.get(l);
		return i == this.storage.minSectionY || ChunkSectionPos.getY(sectionPos) >= i;
	}

	protected boolean isSectionEnabled(long sectionPos) {
		long l = ChunkSectionPos.withZeroZ(sectionPos);
		return this.enabledColumns.contains(l);
	}

	public static final class Data extends ChunkToNibbleArrayMap<SkyLightStorage.Data> {
		private int minSectionY;
		private final Long2IntOpenHashMap columnToTopSection;

		public Data(Long2ObjectOpenHashMap<ChunkNibbleArray> arrays, Long2IntOpenHashMap columnToTopSection, int minSectionY) {
			super(arrays);
			this.columnToTopSection = columnToTopSection;
			columnToTopSection.defaultReturnValue(minSectionY);
			this.minSectionY = minSectionY;
		}

		public SkyLightStorage.Data copy() {
			return new SkyLightStorage.Data(this.arrays.clone(), this.columnToTopSection.clone(), this.minSectionY);
		}
	}
}
