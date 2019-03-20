package net.minecraft.world.chunk.light;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;

public final class ChunkSkyLightProvider extends ChunkLightProvider<SkyLightStorage.Data, SkyLightStorage> {
	private static final Direction[] DIRECTIONS_SKYLIGHT = Direction.values();
	private static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

	public ChunkSkyLightProvider(ChunkProvider chunkProvider) {
		super(chunkProvider, LightType.SKY, new SkyLightStorage(chunkProvider));
	}

	@Override
	protected int getUpdatedLevel(long l, long m, int i) {
		if (m == Long.MAX_VALUE) {
			return 15;
		} else {
			if (l == Long.MAX_VALUE) {
				if (!this.lightStorage.method_15565(m)) {
					return 15;
				}

				i = 0;
			}

			if (i >= 15) {
				return i;
			} else {
				int j = this.getLightBlockedBetween(l, m);
				boolean bl = l == Long.MAX_VALUE
					|| BlockPos.unpackLongX(l) == BlockPos.unpackLongX(m)
						&& BlockPos.unpackLongZ(l) == BlockPos.unpackLongZ(m)
						&& BlockPos.unpackLongY(l) > BlockPos.unpackLongY(m);
				return bl && i == 0 && j == 0 ? 0 : i + Math.max(1, j);
			}
		}
	}

	@Override
	protected void updateNeighborsRecursively(long l, int i, boolean bl) {
		long m = ChunkSectionPos.toChunkLong(l);
		int j = BlockPos.unpackLongY(l);
		int k = ChunkSectionPos.toLocalCoord(j);
		int n = ChunkSectionPos.toChunkCoord(j);
		int o;
		if (k != 0) {
			o = 0;
		} else {
			int p = 0;

			while (!this.lightStorage.hasChunk(ChunkSectionPos.offsetPacked(m, 0, -p - 1, 0)) && this.lightStorage.isAboveMinimumHeight(n - p - 1)) {
				p++;
			}

			o = p;
		}

		long q = BlockPos.add(l, 0, -1 - o * 16, 0);
		long r = ChunkSectionPos.toChunkLong(q);
		if (m == r || this.lightStorage.hasChunk(r)) {
			this.updateRecursively(l, q, i, bl);
		}

		long s = BlockPos.offset(l, Direction.UP);
		long t = ChunkSectionPos.toChunkLong(s);
		if (m == t || this.lightStorage.hasChunk(t)) {
			this.updateRecursively(l, s, i, bl);
		}

		for (Direction direction : HORIZONTAL_DIRECTIONS) {
			int u = 0;

			do {
				long v = BlockPos.add(l, direction.getOffsetX(), -u, direction.getOffsetZ());
				long w = ChunkSectionPos.toChunkLong(v);
				if (m == w) {
					this.updateRecursively(l, v, i, bl);
					break;
				}

				if (this.lightStorage.hasChunk(w)) {
					this.updateRecursively(l, v, i, bl);
				}
			} while (++u >= o * 16);
		}
	}

	@Override
	protected int getMergedLevel(long l, long m, int i) {
		int j = i;
		if (Long.MAX_VALUE != m) {
			int k = this.getUpdatedLevel(Long.MAX_VALUE, l, 0);
			if (i > k) {
				j = k;
			}

			if (j == 0) {
				return j;
			}
		}

		long n = ChunkSectionPos.toChunkLong(l);
		ChunkNibbleArray chunkNibbleArray = this.lightStorage.getDataForChunk(n, true);

		for (Direction direction : DIRECTIONS_SKYLIGHT) {
			long o = BlockPos.offset(l, direction);
			long p = ChunkSectionPos.toChunkLong(o);
			ChunkNibbleArray chunkNibbleArray2;
			if (n == p) {
				chunkNibbleArray2 = chunkNibbleArray;
			} else {
				chunkNibbleArray2 = this.lightStorage.getDataForChunk(p, true);
			}

			if (chunkNibbleArray2 != null) {
				if (o != m) {
					int q = this.getUpdatedLevel(o, l, this.getCurrentLevelFromArray(chunkNibbleArray2, o));
					if (j > q) {
						j = q;
					}

					if (j == 0) {
						return j;
					}
				}
			} else if (direction != Direction.DOWN) {
				for (o = BlockPos.removeChunkSectionLocalY(o); !this.lightStorage.hasChunk(p) && !this.lightStorage.method_15568(p); o = BlockPos.add(o, 0, 16, 0)) {
					p = ChunkSectionPos.offsetPacked(p, Direction.UP);
				}

				ChunkNibbleArray chunkNibbleArray3 = this.lightStorage.getDataForChunk(p, true);
				if (o != m) {
					int r;
					if (chunkNibbleArray3 != null) {
						r = this.getUpdatedLevel(o, l, this.getCurrentLevelFromArray(chunkNibbleArray3, o));
					} else {
						r = this.lightStorage.method_15566(p) ? 15 : 0;
					}

					if (j > r) {
						j = r;
					}

					if (j == 0) {
						return j;
					}
				}
			}
		}

		return j;
	}

	@Override
	protected void update(long l) {
		this.lightStorage.updateAll();
		long m = ChunkSectionPos.toChunkLong(l);
		if (this.lightStorage.hasChunk(m)) {
			super.update(l);
		} else {
			for (l = BlockPos.removeChunkSectionLocalY(l); !this.lightStorage.hasChunk(m) && !this.lightStorage.method_15568(m); l = BlockPos.add(l, 0, 16, 0)) {
				m = ChunkSectionPos.offsetPacked(m, Direction.UP);
			}

			if (this.lightStorage.hasChunk(m)) {
				super.update(l);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public String method_15520(long l) {
		return super.method_15520(l) + (this.lightStorage.method_15568(l) ? "*" : "");
	}
}
