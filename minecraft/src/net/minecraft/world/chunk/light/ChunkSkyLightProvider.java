package net.minecraft.world.chunk.light;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;

public final class ChunkSkyLightProvider extends ChunkLightProvider<SkyLightStorage.Data, SkyLightStorage> {
	private static final Direction[] DIRECTIONS_SKYLIGHT = Direction.values();
	private static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

	public ChunkSkyLightProvider(ChunkProvider chunkProvider) {
		super(chunkProvider, LightType.SKY_LIGHT, new SkyLightStorage(chunkProvider));
	}

	@Override
	protected int getBaseLevelFor(long l, long m, int i) {
		if (m == -1L) {
			return 15;
		} else {
			if (l == -1L) {
				if (!this.lightStorage.method_15565(m)) {
					return 15;
				}

				i = 0;
			}

			if (i >= 15) {
				return i;
			} else {
				int j = this.getLightBlockedBetween(l, m);
				boolean bl = l == -1L
					|| BlockPos.unpackLongX(l) == BlockPos.unpackLongX(m)
						&& BlockPos.unpackLongZ(l) == BlockPos.unpackLongZ(m)
						&& BlockPos.unpackLongY(l) > BlockPos.unpackLongY(m);
				return bl && i == 0 && j == 0 ? 0 : i + Math.max(1, j);
			}
		}
	}

	@Override
	protected void processLevelAt(long l, int i, boolean bl) {
		long m = BlockPos.toChunkSectionOrigin(l);
		int j = BlockPos.unpackLongY(l);
		int k = j & 15;
		int n = j & -16;
		int o;
		if (k != 0) {
			o = 0;
		} else {
			int p = 0;

			while (!this.lightStorage.hasChunk(BlockPos.add(m, 0, -p - 16, 0)) && this.lightStorage.isAboveMinimumHeight(n - p - 16)) {
				p += 16;
			}

			o = p;
		}

		long q = BlockPos.add(l, 0, -1 - o, 0);
		long r = BlockPos.toChunkSectionOrigin(q);
		if (m == r || this.lightStorage.hasChunk(r)) {
			this.scheduleUpdateRecursively(l, q, i, bl);
		}

		long s = BlockPos.offset(l, Direction.UP);
		long t = BlockPos.toChunkSectionOrigin(s);
		if (m == t || this.lightStorage.hasChunk(t)) {
			this.scheduleUpdateRecursively(l, s, i, bl);
		}

		for (Direction direction : HORIZONTAL_DIRECTIONS) {
			int u = 0;

			do {
				long v = BlockPos.add(l, direction.getOffsetX(), -u, direction.getOffsetZ());
				long w = BlockPos.toChunkSectionOrigin(v);
				if (m == w) {
					this.scheduleUpdateRecursively(l, v, i, bl);
					break;
				}

				if (this.lightStorage.hasChunk(w)) {
					this.scheduleUpdateRecursively(l, v, i, bl);
				}
			} while (++u >= o);
		}
	}

	@Override
	protected int getMergedLevel(long l, long m, int i) {
		int j = i;
		if (-1L != m) {
			int k = this.getBaseLevelFor(-1L, l, 0);
			if (i > k) {
				j = k;
			}

			if (j == 0) {
				return j;
			}
		}

		long n = BlockPos.toChunkSectionOrigin(l);
		ChunkNibbleArray chunkNibbleArray = this.lightStorage.getDataForChunk(n, true);

		for (Direction direction : DIRECTIONS_SKYLIGHT) {
			long o = BlockPos.offset(l, direction);
			long p = BlockPos.toChunkSectionOrigin(o);
			ChunkNibbleArray chunkNibbleArray2;
			if (n == p) {
				chunkNibbleArray2 = chunkNibbleArray;
			} else {
				chunkNibbleArray2 = this.lightStorage.getDataForChunk(p, true);
			}

			if (chunkNibbleArray2 != null) {
				if (o != m) {
					int q = this.getBaseLevelFor(o, l, this.getCurrentLevelFromArray(chunkNibbleArray2, o));
					if (j > q) {
						j = q;
					}

					if (j == 0) {
						return j;
					}
				}
			} else if (direction != Direction.DOWN) {
				for (o = BlockPos.removeChunkSectionLocalY(o); !this.lightStorage.hasChunk(p) && !this.lightStorage.method_15568(p); o = BlockPos.add(o, 0, 16, 0)) {
					p = BlockPos.add(p, 0, 16, 0);
				}

				ChunkNibbleArray chunkNibbleArray3 = this.lightStorage.getDataForChunk(p, true);
				if (o != m) {
					int r;
					if (chunkNibbleArray3 != null) {
						r = this.getBaseLevelFor(o, l, this.getCurrentLevelFromArray(chunkNibbleArray3, o));
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
	protected void scheduleNewUpdate(long l) {
		this.lightStorage.updateAll();
		long m = BlockPos.toChunkSectionOrigin(l);
		if (this.lightStorage.hasChunk(m)) {
			super.scheduleNewUpdate(l);
		} else {
			for (l = BlockPos.removeChunkSectionLocalY(l); !this.lightStorage.hasChunk(m) && !this.lightStorage.method_15568(m); l = BlockPos.add(l, 0, 16, 0)) {
				m = BlockPos.add(m, 0, 16, 0);
			}

			if (this.lightStorage.hasChunk(m)) {
				super.scheduleNewUpdate(l);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public String method_15520(long l) {
		return super.method_15520(l) + (this.lightStorage.method_15568(l) ? "*" : "");
	}
}
