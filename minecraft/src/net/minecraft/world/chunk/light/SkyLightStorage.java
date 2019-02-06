package net.minecraft.world.chunk.light;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Arrays;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.WorldNibbleStorage;

public class SkyLightStorage extends LightStorage<SkyLightStorage.Data> {
	private static final Direction[] DIRECTIONS_SKYLIGHT = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
	private final LongSet field_15820 = new LongOpenHashSet();
	private final LongSet field_15815 = new LongOpenHashSet();
	private final LongSet field_15816 = new LongOpenHashSet();
	private final LongSet field_15817 = new LongOpenHashSet();
	private volatile boolean hasSkyLightUpdates;

	protected SkyLightStorage(ChunkProvider chunkProvider) {
		super(LightType.SKY_LIGHT, chunkProvider, new SkyLightStorage.Data(new Long2ObjectOpenHashMap<>(), new Long2IntOpenHashMap(), Integer.MAX_VALUE));
	}

	@Override
	protected int getLight(long l) {
		long m = BlockPos.toChunkSectionOrigin(l);
		int i = BlockPos.unpackLongY(m);
		SkyLightStorage.Data data = this.dataStorageUncached;
		int j = data.heightMap.get(BlockPos.removeY(m));
		if (j != data.defaultHeight && i < j) {
			ChunkNibbleArray chunkNibbleArray = this.getDataForChunk(data, m);
			if (chunkNibbleArray == null) {
				for (l = BlockPos.removeChunkSectionLocalY(l); chunkNibbleArray == null; chunkNibbleArray = this.getDataForChunk(data, m)) {
					m = BlockPos.add(m, 0, 16, 0);
					i += 16;
					if (i >= j) {
						return 15;
					}

					l = BlockPos.add(l, 0, 16, 0);
				}
			}

			return chunkNibbleArray.get(BlockPos.unpackLongX(l) & 15, BlockPos.unpackLongY(l) & 15, BlockPos.unpackLongZ(l) & 15);
		} else {
			return 15;
		}
	}

	@Override
	protected void method_15523(long l) {
		int i = BlockPos.unpackLongY(l);
		if (this.dataStorage.defaultHeight > i) {
			this.dataStorage.defaultHeight = i;
			this.dataStorage.heightMap.defaultReturnValue(this.dataStorage.defaultHeight);
		}

		long m = BlockPos.removeY(l);
		int j = this.dataStorage.heightMap.get(m);
		if (j < i + 16) {
			this.dataStorage.heightMap.put(m, i + 16);
			if (!this.field_15817.contains(m)) {
				this.field_15815.add(l);
				this.field_15816.remove(l);
				if (j > this.dataStorage.defaultHeight) {
					long n = BlockPos.asLong(BlockPos.unpackLongX(l), j - 16, BlockPos.unpackLongZ(l));
					this.field_15815.remove(n);
					this.field_15816.add(n);
				}

				this.checkForUpdates();
			}
		}
	}

	private void checkForUpdates() {
		this.hasSkyLightUpdates = !this.field_15815.isEmpty() || !this.field_15816.isEmpty();
	}

	@Override
	protected void onChunkRemoved(long l) {
		long m = BlockPos.removeY(l);
		boolean bl = this.field_15817.contains(m);
		if (!bl) {
			this.field_15816.add(l);
			this.field_15815.remove(l);
		}

		int i = BlockPos.unpackLongY(l);
		if (this.dataStorage.heightMap.get(m) == i + 16) {
			long n;
			for (n = l; !this.hasChunk(n) && this.isAboveMinimumHeight(i); n = BlockPos.add(n, 0, -16, 0)) {
				i -= 16;
			}

			if (this.hasChunk(n)) {
				this.dataStorage.heightMap.put(m, i + 16);
				if (!bl) {
					this.field_15815.add(n);
					this.field_15816.remove(n);
				}
			} else {
				this.dataStorage.heightMap.remove(m);
			}
		}

		if (!bl) {
			this.checkForUpdates();
		}
	}

	@Override
	protected void method_15535(long l, boolean bl) {
		if (bl && this.field_15817.add(l)) {
			int i = this.dataStorage.heightMap.get(l);
			if (i != this.dataStorage.defaultHeight) {
				long m = BlockPos.add(l, 0, i - 16, 0);
				this.field_15816.add(m);
				this.field_15815.remove(m);
				this.checkForUpdates();
			}
		} else if (!bl && this.field_15817.remove(l)) {
			int i = this.dataStorage.heightMap.get(l);
			if (i != this.dataStorage.defaultHeight) {
				long m = BlockPos.add(l, 0, i - 16, 0);
				this.field_15815.add(m);
				this.field_15816.remove(m);
				this.checkForUpdates();
			}
		}
	}

	@Override
	protected boolean hasLightUpdates() {
		return super.hasLightUpdates() || this.hasSkyLightUpdates;
	}

	@Override
	protected void processUpdates(ChunkLightProvider<SkyLightStorage.Data, ?> chunkLightProvider, boolean bl, boolean bl2) {
		super.processUpdates(chunkLightProvider, bl, bl2);
		if (bl) {
			if (!this.field_15815.isEmpty()) {
				LongIterator var4 = this.field_15815.iterator();

				while (var4.hasNext()) {
					long l = (Long)var4.next();
					int i = this.getCurrentLevelFor(l);
					if (i != 2 && !this.field_15816.contains(l) && this.field_15820.add(l)) {
						if (i == 1) {
							this.removeChunkData(chunkLightProvider, l);
							if (this.field_15802.add(l)) {
								this.dataStorage.cloneChunkData(l);
							}

							Arrays.fill(this.getDataForChunk(l, true).asByteArray(), (byte)-1);
							int j = BlockPos.unpackLongX(l);
							int k = BlockPos.unpackLongY(l);
							int m = BlockPos.unpackLongZ(l);

							for (Direction direction : DIRECTIONS_SKYLIGHT) {
								long n = BlockPos.add(l, direction.getOffsetX() * 16, direction.getOffsetY() * 16, direction.getOffsetZ() * 16);
								if ((this.field_15816.contains(n) || !this.field_15820.contains(n) && !this.field_15815.contains(n)) && this.hasChunk(n)) {
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

											chunkLightProvider.scheduleNewLevelUpdate(q, r, chunkLightProvider.getBaseLevelFor(q, r, 0), true);
										}
									}
								}
							}

							for (int s = 0; s < 16; s++) {
								for (int t = 0; t < 16; t++) {
									long u = BlockPos.add(l, s, 0, t);
									long n = BlockPos.add(l, s, -1, t);
									chunkLightProvider.scheduleNewLevelUpdate(u, n, chunkLightProvider.getBaseLevelFor(u, n, 0), true);
								}
							}
						} else {
							for (int j = 0; j < 16; j++) {
								for (int k = 0; k < 16; k++) {
									long v = BlockPos.add(l, j, 15, k);
									chunkLightProvider.scheduleNewLevelUpdate(-1L, v, 0, true);
								}
							}
						}
					}
				}
			}

			this.field_15815.clear();
			if (!this.field_15816.isEmpty()) {
				LongIterator var23 = this.field_15816.iterator();

				while (var23.hasNext()) {
					long l = (Long)var23.next();
					if (this.field_15820.remove(l) && this.hasChunk(l)) {
						for (int i = 0; i < 16; i++) {
							for (int j = 0; j < 16; j++) {
								long w = BlockPos.add(l, i, 15, j);
								chunkLightProvider.scheduleNewLevelUpdate(-1L, w, 15, false);
							}
						}
					}
				}
			}

			this.field_15816.clear();
			this.hasSkyLightUpdates = false;
		}
	}

	protected boolean isAboveMinimumHeight(int i) {
		return i >= this.dataStorage.defaultHeight;
	}

	protected boolean method_15565(long l) {
		int i = BlockPos.unpackLongY(l);
		if ((i & 15) != 15) {
			return false;
		} else {
			long m = BlockPos.toChunkSectionOrigin(l);
			long n = BlockPos.removeY(m);
			if (this.field_15817.contains(n)) {
				return false;
			} else {
				int j = this.dataStorage.heightMap.get(n);
				return j == i + 16;
			}
		}
	}

	protected boolean method_15568(long l) {
		long m = BlockPos.removeY(l);
		int i = this.dataStorage.heightMap.get(m);
		return i == this.dataStorage.defaultHeight || BlockPos.unpackLongY(l) >= i;
	}

	protected boolean method_15566(long l) {
		long m = BlockPos.removeY(l);
		return this.field_15817.contains(m);
	}

	public static final class Data extends WorldNibbleStorage<SkyLightStorage.Data> {
		private int defaultHeight;
		private final Long2IntOpenHashMap heightMap;

		public Data(Long2ObjectOpenHashMap<ChunkNibbleArray> long2ObjectOpenHashMap, Long2IntOpenHashMap long2IntOpenHashMap, int i) {
			super(long2ObjectOpenHashMap);
			this.heightMap = long2IntOpenHashMap;
			long2IntOpenHashMap.defaultReturnValue(i);
			this.defaultHeight = i;
		}

		public SkyLightStorage.Data method_15572() {
			return new SkyLightStorage.Data(this.arraysByChunk.clone(), this.heightMap.clone(), this.defaultHeight);
		}
	}
}
