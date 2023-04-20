package net.minecraft.world.chunk.light;

import java.util.Locale;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import org.jetbrains.annotations.VisibleForTesting;

public class ChunkSkyLightProvider extends ChunkLightProvider<SkyLightStorage.Data, SkyLightStorage> {
	private static final Direction[] DIRECTIONS = Direction.values();
	private static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

	public ChunkSkyLightProvider(ChunkProvider chunkProvider) {
		super(chunkProvider, new SkyLightStorage(chunkProvider));
	}

	@VisibleForTesting
	protected ChunkSkyLightProvider(ChunkProvider chunkProvider, SkyLightStorage lightStorage) {
		super(chunkProvider, lightStorage);
	}

	@Override
	protected int getPropagatedLevel(long sourceId, long targetId, int level) {
		if (this.isMarker(targetId) || this.isMarker(sourceId)) {
			return 15;
		} else if (level >= 14) {
			return 15;
		} else {
			this.reusableBlockPos.set(targetId);
			BlockState blockState = this.getStateForLighting(this.reusableBlockPos);
			int i = this.getOpacity(blockState, this.reusableBlockPos);
			if (i >= 15) {
				return 15;
			} else {
				Direction direction = getDirection(sourceId, targetId);
				if (direction == null) {
					throw new IllegalStateException(
						String.format(
							Locale.ROOT,
							"Light was spread in illegal direction. From %d, %d, %d to %d, %d, %d",
							BlockPos.unpackLongX(sourceId),
							BlockPos.unpackLongY(sourceId),
							BlockPos.unpackLongZ(sourceId),
							BlockPos.unpackLongX(targetId),
							BlockPos.unpackLongY(targetId),
							BlockPos.unpackLongZ(targetId)
						)
					);
				} else {
					this.reusableBlockPos.set(sourceId);
					BlockState blockState2 = this.getStateForLighting(this.reusableBlockPos);
					if (this.shapesCoverFullCube(sourceId, blockState2, targetId, blockState, direction)) {
						return 15;
					} else {
						boolean bl = direction == Direction.DOWN;
						return bl && level == 0 && i == 0 ? 0 : level + Math.max(1, i);
					}
				}
			}
		}
	}

	@Override
	protected void propagateLevel(long id, int level, boolean decrease) {
		if (!decrease || level < this.levelCount - 2) {
			long l = ChunkSectionPos.fromBlockPos(id);
			int i = BlockPos.unpackLongY(id);
			int j = ChunkSectionPos.getLocalCoord(i);
			int k = ChunkSectionPos.getSectionCoord(i);
			int m;
			if (j != 0) {
				m = 0;
			} else {
				int n = 0;

				while (!this.lightStorage.hasSection(ChunkSectionPos.offset(l, 0, -n - 1, 0)) && this.lightStorage.isAboveMinHeight(k - n - 1)) {
					n++;
				}

				m = n;
			}

			long o = BlockPos.add(id, 0, -1 - m * 16, 0);
			long p = ChunkSectionPos.fromBlockPos(o);
			if (l == p || this.lightStorage.hasSection(p)) {
				this.propagateLevel(id, o, level, decrease);
			}

			long q = BlockPos.offset(id, Direction.UP);
			long r = ChunkSectionPos.fromBlockPos(q);
			if (l == r || this.lightStorage.hasSection(r)) {
				this.propagateLevel(id, q, level, decrease);
			}

			for (Direction direction : HORIZONTAL_DIRECTIONS) {
				int s = 0;

				do {
					long t = BlockPos.add(id, direction.getOffsetX(), -s, direction.getOffsetZ());
					long u = ChunkSectionPos.fromBlockPos(t);
					if (l == u) {
						this.propagateLevel(id, t, level, decrease);
						break;
					}

					if (this.lightStorage.hasSection(u)) {
						long v = BlockPos.add(id, 0, -s, 0);
						this.propagateLevel(v, t, level, decrease);
					}
				} while (++s > m * 16);
			}
		}
	}

	@Override
	protected int recalculateLevel(long id, long excludedId, int maxLevel) {
		int i = maxLevel;
		long l = ChunkSectionPos.fromBlockPos(id);
		ChunkNibbleArray chunkNibbleArray = this.lightStorage.getLightSection(l, true);

		for (Direction direction : DIRECTIONS) {
			long m = BlockPos.offset(id, direction);
			if (m != excludedId) {
				long n = ChunkSectionPos.fromBlockPos(m);
				ChunkNibbleArray chunkNibbleArray2;
				if (l == n) {
					chunkNibbleArray2 = chunkNibbleArray;
				} else {
					chunkNibbleArray2 = this.lightStorage.getLightSection(n, true);
				}

				int j;
				if (chunkNibbleArray2 != null) {
					j = this.getCurrentLevelFromSection(chunkNibbleArray2, m);
				} else {
					if (direction == Direction.DOWN) {
						continue;
					}

					j = 15 - this.lightStorage.getLight(m, true);
				}

				if (j + 1 < i || j == 0 && direction == Direction.UP) {
					int k = this.getPropagatedLevel(m, id, j);
					if (i > k) {
						i = k;
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
		long l = ChunkSectionPos.fromBlockPos(id);
		if (this.lightStorage.hasSection(l)) {
			super.resetLevel(id);
		} else {
			for (id = BlockPos.removeChunkSectionLocalY(id);
				!this.lightStorage.hasSection(l) && !this.lightStorage.isAtOrAboveTopmostSection(l);
				id = BlockPos.add(id, 0, 16, 0)
			) {
				l = ChunkSectionPos.offset(l, Direction.UP);
			}

			if (this.lightStorage.hasSection(l)) {
				super.resetLevel(id);
			}
		}
	}

	@Override
	public String displaySectionLevel(long sectionPos) {
		return super.displaySectionLevel(sectionPos) + (this.lightStorage.isAtOrAboveTopmostSection(sectionPos) ? "*" : "");
	}
}
