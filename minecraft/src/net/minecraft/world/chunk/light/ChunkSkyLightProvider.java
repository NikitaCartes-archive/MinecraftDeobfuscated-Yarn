package net.minecraft.world.chunk.light;

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
import org.apache.commons.lang3.mutable.MutableInt;

public final class ChunkSkyLightProvider extends ChunkLightProvider<SkyLightStorage.Data, SkyLightStorage> {
	private static final Direction[] DIRECTIONS = Direction.values();
	private static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

	public ChunkSkyLightProvider(ChunkProvider chunkProvider) {
		super(chunkProvider, LightType.SKY, new SkyLightStorage(chunkProvider));
	}

	@Override
	protected int getPropagatedLevel(long sourceId, long targetId, int level) {
		if (targetId == Long.MAX_VALUE) {
			return 15;
		} else {
			if (sourceId == Long.MAX_VALUE) {
				if (!this.lightStorage.method_15565(targetId)) {
					return 15;
				}

				level = 0;
			}

			if (level >= 15) {
				return level;
			} else {
				MutableInt mutableInt = new MutableInt();
				BlockState blockState = this.getStateForLighting(targetId, mutableInt);
				if (mutableInt.getValue() >= 15) {
					return 15;
				} else {
					int i = BlockPos.unpackLongX(sourceId);
					int j = BlockPos.unpackLongY(sourceId);
					int k = BlockPos.unpackLongZ(sourceId);
					int l = BlockPos.unpackLongX(targetId);
					int m = BlockPos.unpackLongY(targetId);
					int n = BlockPos.unpackLongZ(targetId);
					boolean bl = i == l && k == n;
					int o = Integer.signum(l - i);
					int p = Integer.signum(m - j);
					int q = Integer.signum(n - k);
					Direction direction;
					if (sourceId == Long.MAX_VALUE) {
						direction = Direction.DOWN;
					} else {
						direction = Direction.fromVector(o, p, q);
					}

					BlockState blockState2 = this.getStateForLighting(sourceId, null);
					if (direction != null) {
						VoxelShape voxelShape = this.getOpaqueShape(blockState2, sourceId, direction);
						VoxelShape voxelShape2 = this.getOpaqueShape(blockState, targetId, direction.getOpposite());
						if (VoxelShapes.unionCoversFullCube(voxelShape, voxelShape2)) {
							return 15;
						}
					} else {
						VoxelShape voxelShape = this.getOpaqueShape(blockState2, sourceId, Direction.DOWN);
						if (VoxelShapes.unionCoversFullCube(voxelShape, VoxelShapes.empty())) {
							return 15;
						}

						int r = bl ? -1 : 0;
						Direction direction2 = Direction.fromVector(o, r, q);
						if (direction2 == null) {
							return 15;
						}

						VoxelShape voxelShape3 = this.getOpaqueShape(blockState, targetId, direction2.getOpposite());
						if (VoxelShapes.unionCoversFullCube(VoxelShapes.empty(), voxelShape3)) {
							return 15;
						}
					}

					boolean bl2 = sourceId == Long.MAX_VALUE || bl && j > m;
					return bl2 && level == 0 && mutableInt.getValue() == 0 ? 0 : level + Math.max(1, mutableInt.getValue());
				}
			}
		}
	}

	@Override
	protected void propagateLevel(long id, int level, boolean decrease) {
		long l = ChunkSectionPos.fromGlobalPos(id);
		int i = BlockPos.unpackLongY(id);
		int j = ChunkSectionPos.getLocalCoord(i);
		int k = ChunkSectionPos.getSectionCoord(i);
		int m;
		if (j != 0) {
			m = 0;
		} else {
			int n = 0;

			while (!this.lightStorage.hasLight(ChunkSectionPos.offset(l, 0, -n - 1, 0)) && this.lightStorage.isAboveMinHeight(k - n - 1)) {
				n++;
			}

			m = n;
		}

		long o = BlockPos.add(id, 0, -1 - m * 16, 0);
		long p = ChunkSectionPos.fromGlobalPos(o);
		if (l == p || this.lightStorage.hasLight(p)) {
			this.propagateLevel(id, o, level, decrease);
		}

		long q = BlockPos.offset(id, Direction.UP);
		long r = ChunkSectionPos.fromGlobalPos(q);
		if (l == r || this.lightStorage.hasLight(r)) {
			this.propagateLevel(id, q, level, decrease);
		}

		for (Direction direction : HORIZONTAL_DIRECTIONS) {
			int s = 0;

			do {
				long t = BlockPos.add(id, direction.getOffsetX(), -s, direction.getOffsetZ());
				long u = ChunkSectionPos.fromGlobalPos(t);
				if (l == u) {
					this.propagateLevel(id, t, level, decrease);
					break;
				}

				if (this.lightStorage.hasLight(u)) {
					this.propagateLevel(id, t, level, decrease);
				}
			} while (++s > m * 16);
		}
	}

	@Override
	protected int recalculateLevel(long id, long excludedId, int maxLevel) {
		int i = maxLevel;
		if (Long.MAX_VALUE != excludedId) {
			int j = this.getPropagatedLevel(Long.MAX_VALUE, id, 0);
			if (maxLevel > j) {
				i = j;
			}

			if (i == 0) {
				return i;
			}
		}

		long l = ChunkSectionPos.fromGlobalPos(id);
		ChunkNibbleArray chunkNibbleArray = this.lightStorage.getLightArray(l, true);

		for (Direction direction : DIRECTIONS) {
			long m = BlockPos.offset(id, direction);
			long n = ChunkSectionPos.fromGlobalPos(m);
			ChunkNibbleArray chunkNibbleArray2;
			if (l == n) {
				chunkNibbleArray2 = chunkNibbleArray;
			} else {
				chunkNibbleArray2 = this.lightStorage.getLightArray(n, true);
			}

			if (chunkNibbleArray2 != null) {
				if (m != excludedId) {
					int k = this.getPropagatedLevel(m, id, this.getCurrentLevelFromArray(chunkNibbleArray2, m));
					if (i > k) {
						i = k;
					}

					if (i == 0) {
						return i;
					}
				}
			} else if (direction != Direction.DOWN) {
				for (m = BlockPos.removeChunkSectionLocalY(m);
					!this.lightStorage.hasLight(n) && !this.lightStorage.isAboveTopmostLightArray(n);
					m = BlockPos.add(m, 0, 16, 0)
				) {
					n = ChunkSectionPos.offset(n, Direction.UP);
				}

				ChunkNibbleArray chunkNibbleArray3 = this.lightStorage.getLightArray(n, true);
				if (m != excludedId) {
					int o;
					if (chunkNibbleArray3 != null) {
						o = this.getPropagatedLevel(m, id, this.getCurrentLevelFromArray(chunkNibbleArray3, m));
					} else {
						o = this.lightStorage.isLightEnabled(n) ? 0 : 15;
					}

					if (i > o) {
						i = o;
					}

					if (i == 0) {
						return i;
					}
				}
			}
		}

		return i;
	}

	@Override
	protected void resetLevel(long id) {
		this.lightStorage.updateAll();
		long l = ChunkSectionPos.fromGlobalPos(id);
		if (this.lightStorage.hasLight(l)) {
			super.resetLevel(id);
		} else {
			for (id = BlockPos.removeChunkSectionLocalY(id);
				!this.lightStorage.hasLight(l) && !this.lightStorage.isAboveTopmostLightArray(l);
				id = BlockPos.add(id, 0, 16, 0)
			) {
				l = ChunkSectionPos.offset(l, Direction.UP);
			}

			if (this.lightStorage.hasLight(l)) {
				super.resetLevel(id);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public String method_22875(long l) {
		return super.method_22875(l) + (this.lightStorage.isAboveTopmostLightArray(l) ? "*" : "");
	}
}
