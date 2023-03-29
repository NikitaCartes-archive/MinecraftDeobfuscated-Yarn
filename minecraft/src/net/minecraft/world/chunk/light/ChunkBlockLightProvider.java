package net.minecraft.world.chunk.light;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;

public final class ChunkBlockLightProvider extends ChunkLightProvider<BlockLightStorage.Data, BlockLightStorage> {
	private static final Direction[] DIRECTIONS = Direction.values();
	private final BlockPos.Mutable mutablePos = new BlockPos.Mutable();

	public ChunkBlockLightProvider(ChunkProvider chunkProvider) {
		this(chunkProvider, new BlockLightStorage(chunkProvider));
	}

	@VisibleForTesting
	public ChunkBlockLightProvider(ChunkProvider chunkProvider, BlockLightStorage blockLightStorage) {
		super(chunkProvider, blockLightStorage);
	}

	private int getLightSourceLuminance(long blockPos) {
		return this.getStateForLighting(this.mutablePos.set(blockPos)).getLuminance();
	}

	@Override
	protected int getPropagatedLevel(long sourceId, long targetId, int level) {
		if (this.isMarker(targetId)) {
			return 15;
		} else if (this.isMarker(sourceId)) {
			return level + 15 - this.getLightSourceLuminance(targetId);
		} else if (level >= 14) {
			return 15;
		} else {
			this.mutablePos.set(targetId);
			BlockState blockState = this.getStateForLighting(this.mutablePos);
			int i = this.getOpacity(blockState, this.mutablePos);
			if (i >= 15) {
				return 15;
			} else {
				Direction direction = getDirection(sourceId, targetId);
				if (direction == null) {
					return 15;
				} else {
					this.mutablePos.set(sourceId);
					BlockState blockState2 = this.getStateForLighting(this.mutablePos);
					return this.shapesCoverFullCube(sourceId, blockState2, targetId, blockState, direction) ? 15 : level + Math.max(1, i);
				}
			}
		}
	}

	@Override
	protected void propagateLevel(long id, int level, boolean decrease) {
		if (!decrease || level < this.levelCount - 2) {
			long l = ChunkSectionPos.fromBlockPos(id);

			for (Direction direction : DIRECTIONS) {
				long m = BlockPos.offset(id, direction);
				long n = ChunkSectionPos.fromBlockPos(m);
				if (l == n || this.lightStorage.hasSection(n)) {
					this.propagateLevel(id, m, level, decrease);
				}
			}
		}
	}

	@Override
	protected int recalculateLevel(long id, long excludedId, int maxLevel) {
		int i = maxLevel;
		if (!this.isMarker(excludedId)) {
			int j = this.getPropagatedLevel(Long.MAX_VALUE, id, 0);
			if (maxLevel > j) {
				i = j;
			}

			if (i == 0) {
				return i;
			}
		}

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

				if (chunkNibbleArray2 != null) {
					int k = this.getCurrentLevelFromSection(chunkNibbleArray2, m);
					if (k + 1 < i) {
						int o = this.getPropagatedLevel(m, id, k);
						if (i > o) {
							i = o;
						}

						if (i == 0) {
							return i;
						}
					}
				}
			}
		}

		return i;
	}

	@Override
	public void addLightSource(BlockPos pos, int level) {
		this.lightStorage.updateAll();
		this.updateLevel(Long.MAX_VALUE, pos.asLong(), 15 - level, true);
	}
}
