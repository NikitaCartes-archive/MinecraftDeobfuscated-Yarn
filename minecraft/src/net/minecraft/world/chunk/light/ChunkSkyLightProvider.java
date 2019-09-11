package net.minecraft.world.chunk.light;

import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;

public final class ChunkSkyLightProvider extends ChunkLightProvider<SkyLightStorage.Data, SkyLightStorage> {
	private static final Direction[] DIRECTIONS = Direction.values();
	private static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

	public ChunkSkyLightProvider(ChunkProvider chunkProvider) {
		super(chunkProvider, LightType.SKY, new SkyLightStorage(chunkProvider));
	}

	@Override
	protected int getPropagatedLevel(long l, long m, int i) {
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
				AtomicInteger atomicInteger = new AtomicInteger();
				BlockState blockState = this.getStateForLighting(m, atomicInteger);
				if (atomicInteger.get() >= 15) {
					return 15;
				} else {
					int j = BlockPos.unpackLongX(l);
					int k = BlockPos.unpackLongY(l);
					int n = BlockPos.unpackLongZ(l);
					int o = BlockPos.unpackLongX(m);
					int p = BlockPos.unpackLongY(m);
					int q = BlockPos.unpackLongZ(m);
					boolean bl = j == o && n == q;
					int r = Integer.signum(o - j);
					int s = Integer.signum(p - k);
					int t = Integer.signum(q - n);
					Direction direction;
					if (l == Long.MAX_VALUE) {
						direction = Direction.DOWN;
					} else {
						direction = Direction.fromVector(r, s, t);
					}

					BlockState blockState2 = this.getStateForLighting(l, null);
					if (direction != null) {
						VoxelShape voxelShape = this.getOpaqueShape(blockState2, l, direction);
						VoxelShape voxelShape2 = this.getOpaqueShape(blockState, m, direction.getOpposite());
						if (VoxelShapes.unionCoversFullCube(voxelShape, voxelShape2)) {
							return 15;
						}
					} else {
						VoxelShape voxelShape = this.getOpaqueShape(blockState2, l, Direction.DOWN);
						if (VoxelShapes.unionCoversFullCube(voxelShape, VoxelShapes.empty())) {
							return 15;
						}

						int u = bl ? -1 : 0;
						Direction direction2 = Direction.fromVector(r, u, t);
						if (direction2 == null) {
							return 15;
						}

						VoxelShape voxelShape3 = this.getOpaqueShape(blockState, m, direction2.getOpposite());
						if (VoxelShapes.unionCoversFullCube(VoxelShapes.empty(), voxelShape3)) {
							return 15;
						}
					}

					boolean bl2 = l == Long.MAX_VALUE || bl && k > p;
					return bl2 && i == 0 && atomicInteger.get() == 0 ? 0 : i + Math.max(1, atomicInteger.get());
				}
			}
		}
	}

	@Override
	protected void propagateLevel(long l, int i, boolean bl) {
		long m = ChunkSectionPos.fromGlobalPos(l);
		int j = BlockPos.unpackLongY(l);
		int k = ChunkSectionPos.getLocalCoord(j);
		int n = ChunkSectionPos.getSectionCoord(j);
		int o;
		if (k != 0) {
			o = 0;
		} else {
			int p = 0;

			while (!this.lightStorage.hasLight(ChunkSectionPos.offset(m, 0, -p - 1, 0)) && this.lightStorage.isAboveMinimumHeight(n - p - 1)) {
				p++;
			}

			o = p;
		}

		long q = BlockPos.add(l, 0, -1 - o * 16, 0);
		long r = ChunkSectionPos.fromGlobalPos(q);
		if (m == r || this.lightStorage.hasLight(r)) {
			this.propagateLevel(l, q, i, bl);
		}

		long s = BlockPos.offset(l, Direction.UP);
		long t = ChunkSectionPos.fromGlobalPos(s);
		if (m == t || this.lightStorage.hasLight(t)) {
			this.propagateLevel(l, s, i, bl);
		}

		for (Direction direction : HORIZONTAL_DIRECTIONS) {
			int u = 0;

			do {
				long v = BlockPos.add(l, direction.getOffsetX(), -u, direction.getOffsetZ());
				long w = ChunkSectionPos.fromGlobalPos(v);
				if (m == w) {
					this.propagateLevel(l, v, i, bl);
					break;
				}

				if (this.lightStorage.hasLight(w)) {
					this.propagateLevel(l, v, i, bl);
				}
			} while (++u > o * 16);
		}
	}

	@Override
	protected int recalculateLevel(long l, long m, int i) {
		int j = i;
		if (Long.MAX_VALUE != m) {
			int k = this.getPropagatedLevel(Long.MAX_VALUE, l, 0);
			if (i > k) {
				j = k;
			}

			if (j == 0) {
				return j;
			}
		}

		long n = ChunkSectionPos.fromGlobalPos(l);
		ChunkNibbleArray chunkNibbleArray = this.lightStorage.getLightArray(n, true);

		for (Direction direction : DIRECTIONS) {
			long o = BlockPos.offset(l, direction);
			long p = ChunkSectionPos.fromGlobalPos(o);
			ChunkNibbleArray chunkNibbleArray2;
			if (n == p) {
				chunkNibbleArray2 = chunkNibbleArray;
			} else {
				chunkNibbleArray2 = this.lightStorage.getLightArray(p, true);
			}

			if (chunkNibbleArray2 != null) {
				if (o != m) {
					int q = this.getPropagatedLevel(o, l, this.getCurrentLevelFromArray(chunkNibbleArray2, o));
					if (j > q) {
						j = q;
					}

					if (j == 0) {
						return j;
					}
				}
			} else if (direction != Direction.DOWN) {
				for (o = BlockPos.removeChunkSectionLocalY(o);
					!this.lightStorage.hasLight(p) && !this.lightStorage.isAboveTopmostLightArray(p);
					o = BlockPos.add(o, 0, 16, 0)
				) {
					p = ChunkSectionPos.offset(p, Direction.UP);
				}

				ChunkNibbleArray chunkNibbleArray3 = this.lightStorage.getLightArray(p, true);
				if (o != m) {
					int r;
					if (chunkNibbleArray3 != null) {
						r = this.getPropagatedLevel(o, l, this.getCurrentLevelFromArray(chunkNibbleArray3, o));
					} else {
						r = this.lightStorage.isLightEnabled(p) ? 0 : 15;
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
	protected void resetLevel(long l) {
		this.lightStorage.updateAll();
		long m = ChunkSectionPos.fromGlobalPos(l);
		if (this.lightStorage.hasLight(m)) {
			super.resetLevel(l);
		} else {
			for (l = BlockPos.removeChunkSectionLocalY(l);
				!this.lightStorage.hasLight(m) && !this.lightStorage.isAboveTopmostLightArray(m);
				l = BlockPos.add(l, 0, 16, 0)
			) {
				m = ChunkSectionPos.offset(m, Direction.UP);
			}

			if (this.lightStorage.hasLight(m)) {
				super.resetLevel(l);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public String getSectionDebugString(long l) {
		return super.getSectionDebugString(l) + (this.lightStorage.isAboveTopmostLightArray(l) ? "*" : "");
	}
}
