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
	private final LongSet field_15815 = new LongOpenHashSet();
	private final LongSet field_15816 = new LongOpenHashSet();
	private final LongSet field_15817 = new LongOpenHashSet();
	private volatile boolean hasSkyLightUpdates;

	protected SkyLightStorage(ChunkProvider chunkProvider) {
		super(LightType.SKY, chunkProvider, new SkyLightStorage.Data(new Long2ObjectOpenHashMap<>(), new Long2IntOpenHashMap(), Integer.MAX_VALUE));
	}

	@Override
	protected int getLight(long blockPos) {
		long l = ChunkSectionPos.fromGlobalPos(blockPos);
		int i = ChunkSectionPos.getY(l);
		SkyLightStorage.Data data = this.uncachedLightArrays;
		int j = data.heightMap.get(ChunkSectionPos.withZeroZ(l));
		if (j != data.defaultHeight && i < j) {
			ChunkNibbleArray chunkNibbleArray = this.getLightArray(data, l);
			if (chunkNibbleArray == null) {
				for (blockPos = BlockPos.removeChunkSectionLocalY(blockPos); chunkNibbleArray == null; chunkNibbleArray = this.getLightArray(data, l)) {
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
	protected void onLightArrayCreated(long blockPos) {
		int i = ChunkSectionPos.getY(blockPos);
		if (this.lightArrays.defaultHeight > i) {
			this.lightArrays.defaultHeight = i;
			this.lightArrays.heightMap.defaultReturnValue(this.lightArrays.defaultHeight);
		}

		long l = ChunkSectionPos.withZeroZ(blockPos);
		int j = this.lightArrays.heightMap.get(l);
		if (j < i + 1) {
			this.lightArrays.heightMap.put(l, i + 1);
			if (this.field_15817.contains(l)) {
				this.method_20810(blockPos);
				if (j > this.lightArrays.defaultHeight) {
					long m = ChunkSectionPos.asLong(ChunkSectionPos.getX(blockPos), j - 1, ChunkSectionPos.getZ(blockPos));
					this.method_20809(m);
				}

				this.checkForUpdates();
			}
		}
	}

	private void method_20809(long l) {
		this.field_15816.add(l);
		this.field_15815.remove(l);
	}

	private void method_20810(long l) {
		this.field_15815.add(l);
		this.field_15816.remove(l);
	}

	private void checkForUpdates() {
		this.hasSkyLightUpdates = !this.field_15815.isEmpty() || !this.field_15816.isEmpty();
	}

	@Override
	protected void onChunkRemoved(long l) {
		long m = ChunkSectionPos.withZeroZ(l);
		boolean bl = this.field_15817.contains(m);
		if (bl) {
			this.method_20809(l);
		}

		int i = ChunkSectionPos.getY(l);
		if (this.lightArrays.heightMap.get(m) == i + 1) {
			long n;
			for (n = l; !this.hasLight(n) && this.isAboveMinimumHeight(i); n = ChunkSectionPos.offset(n, Direction.DOWN)) {
				i--;
			}

			if (this.hasLight(n)) {
				this.lightArrays.heightMap.put(m, i + 1);
				if (bl) {
					this.method_20810(n);
				}
			} else {
				this.lightArrays.heightMap.remove(m);
			}
		}

		if (bl) {
			this.checkForUpdates();
		}
	}

	@Override
	protected void method_15535(long l, boolean bl) {
		if (bl && this.field_15817.add(l)) {
			int i = this.lightArrays.heightMap.get(l);
			if (i != this.lightArrays.defaultHeight) {
				long m = ChunkSectionPos.asLong(ChunkSectionPos.getX(l), i - 1, ChunkSectionPos.getZ(l));
				this.method_20810(m);
				this.checkForUpdates();
			}
		} else if (!bl) {
			this.field_15817.remove(l);
		}
	}

	@Override
	protected boolean hasLightUpdates() {
		return super.hasLightUpdates() || this.hasSkyLightUpdates;
	}

	@Override
	protected ChunkNibbleArray createLightArray(long pos) {
		ChunkNibbleArray chunkNibbleArray = this.lightArraysToAdd.get(pos);
		if (chunkNibbleArray != null) {
			return chunkNibbleArray;
		} else {
			long l = ChunkSectionPos.offset(pos, Direction.UP);
			int i = this.lightArrays.heightMap.get(ChunkSectionPos.withZeroZ(pos));
			if (i != this.lightArrays.defaultHeight && ChunkSectionPos.getY(l) < i) {
				ChunkNibbleArray chunkNibbleArray2;
				while ((chunkNibbleArray2 = this.getLightArray(l, true)) == null) {
					l = ChunkSectionPos.offset(l, Direction.UP);
				}

				return new ChunkNibbleArray(new ColumnChunkNibbleArray(chunkNibbleArray2, 0).asByteArray());
			} else {
				return new ChunkNibbleArray();
			}
		}
	}

	@Override
	protected void updateLightArrays(ChunkLightProvider<SkyLightStorage.Data, ?> lightProvider, boolean doSkylight, boolean skipEdgeLightPropagation) {
		super.updateLightArrays(lightProvider, doSkylight, skipEdgeLightPropagation);
		if (doSkylight) {
			if (!this.field_15815.isEmpty()) {
				LongIterator var4 = this.field_15815.iterator();

				while (var4.hasNext()) {
					long l = (Long)var4.next();
					int i = this.getLevel(l);
					if (i != 2 && !this.field_15816.contains(l) && this.field_15820.add(l)) {
						if (i == 1) {
							this.removeChunkData(lightProvider, l);
							if (this.field_15802.add(l)) {
								this.lightArrays.replaceWithCopy(l);
							}

							Arrays.fill(this.getLightArray(l, true).asByteArray(), (byte)-1);
							int j = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(l));
							int k = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(l));
							int m = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(l));

							for (Direction direction : LIGHT_REDUCTION_DIRECTIONS) {
								long n = ChunkSectionPos.offset(l, direction);
								if ((this.field_15816.contains(n) || !this.field_15820.contains(n) && !this.field_15815.contains(n)) && this.hasLight(n)) {
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
										ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(l)) + s,
										ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(l)),
										ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(l)) + t
									);
									long n = BlockPos.asLong(
										ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(l)) + s,
										ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(l)) - 1,
										ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(l)) + t
									);
									lightProvider.updateLevel(u, n, lightProvider.getPropagatedLevel(u, n, 0), true);
								}
							}
						} else {
							for (int j = 0; j < 16; j++) {
								for (int k = 0; k < 16; k++) {
									long v = BlockPos.asLong(
										ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(l)) + j,
										ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(l)) + 16 - 1,
										ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(l)) + k
									);
									lightProvider.updateLevel(Long.MAX_VALUE, v, 0, true);
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
					if (this.field_15820.remove(l) && this.hasLight(l)) {
						for (int i = 0; i < 16; i++) {
							for (int j = 0; j < 16; j++) {
								long w = BlockPos.asLong(
									ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(l)) + i,
									ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(l)) + 16 - 1,
									ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(l)) + j
								);
								lightProvider.updateLevel(Long.MAX_VALUE, w, 15, false);
							}
						}
					}
				}
			}

			this.field_15816.clear();
			this.hasSkyLightUpdates = false;
		}
	}

	protected boolean isAboveMinimumHeight(int blockY) {
		return blockY >= this.lightArrays.defaultHeight;
	}

	protected boolean method_15565(long l) {
		int i = BlockPos.unpackLongY(l);
		if ((i & 15) != 15) {
			return false;
		} else {
			long m = ChunkSectionPos.fromGlobalPos(l);
			long n = ChunkSectionPos.withZeroZ(m);
			if (!this.field_15817.contains(n)) {
				return false;
			} else {
				int j = this.lightArrays.heightMap.get(n);
				return ChunkSectionPos.getWorldCoord(j) == i + 16;
			}
		}
	}

	protected boolean method_15568(long l) {
		long m = ChunkSectionPos.withZeroZ(l);
		int i = this.lightArrays.heightMap.get(m);
		return i == this.lightArrays.defaultHeight || ChunkSectionPos.getY(l) >= i;
	}

	protected boolean method_15566(long l) {
		long m = ChunkSectionPos.withZeroZ(l);
		return this.field_15817.contains(m);
	}

	public static final class Data extends ChunkToNibbleArrayMap<SkyLightStorage.Data> {
		private int defaultHeight;
		private final Long2IntOpenHashMap heightMap;

		public Data(Long2ObjectOpenHashMap<ChunkNibbleArray> long2ObjectOpenHashMap, Long2IntOpenHashMap long2IntOpenHashMap, int i) {
			super(long2ObjectOpenHashMap);
			this.heightMap = long2IntOpenHashMap;
			long2IntOpenHashMap.defaultReturnValue(i);
			this.defaultHeight = i;
		}

		public SkyLightStorage.Data copy() {
			return new SkyLightStorage.Data(this.arrays.clone(), this.heightMap.clone(), this.defaultHeight);
		}
	}
}
