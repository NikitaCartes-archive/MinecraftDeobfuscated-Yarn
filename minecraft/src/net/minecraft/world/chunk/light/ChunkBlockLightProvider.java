package net.minecraft.world.chunk.light;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;

public final class ChunkBlockLightProvider extends ChunkLightProvider<BlockLightStorage.Data, BlockLightStorage> {
	private static final Direction[] DIRECTIONS_BLOCKLIGHT = Direction.values();
	private final BlockPos.Mutable mutablePos = new BlockPos.Mutable();

	public ChunkBlockLightProvider(ChunkProvider chunkProvider) {
		super(chunkProvider, LightType.BLOCK_LIGHT, new BlockLightStorage(chunkProvider));
	}

	private int getLightSourceLuminance(long l) {
		int i = BlockPos.unpackLongX(l);
		int j = BlockPos.unpackLongY(l);
		int k = BlockPos.unpackLongZ(l);
		BlockView blockView = this.chunkProvider.getChunk(i >> 4, k >> 4);
		return blockView != null ? blockView.getLuminance(this.mutablePos.set(i, j, k)) : 0;
	}

	@Override
	protected int getBaseLevelFor(long l, long m, int i) {
		if (m == -1L) {
			return 15;
		} else if (l == -1L) {
			return i + 15 - this.getLightSourceLuminance(m);
		} else {
			return i >= 15 ? i : i + Math.max(1, this.getLightBlockedBetween(l, m));
		}
	}

	@Override
	protected void processLevelAt(long l, int i, boolean bl) {
		long m = BlockPos.toChunkSectionOrigin(l);

		for (Direction direction : DIRECTIONS_BLOCKLIGHT) {
			long n = BlockPos.offset(l, direction);
			long o = BlockPos.toChunkSectionOrigin(n);
			if (m == o || this.lightStorage.hasChunk(o)) {
				this.scheduleUpdateRecursively(l, n, i, bl);
			}
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

		for (Direction direction : DIRECTIONS_BLOCKLIGHT) {
			long o = BlockPos.offset(l, direction);
			if (o != m) {
				long p = BlockPos.toChunkSectionOrigin(o);
				ChunkNibbleArray chunkNibbleArray2;
				if (n == p) {
					chunkNibbleArray2 = chunkNibbleArray;
				} else {
					chunkNibbleArray2 = this.lightStorage.getDataForChunk(p, true);
				}

				if (chunkNibbleArray2 != null) {
					int q = this.getBaseLevelFor(o, l, this.getCurrentLevelFromArray(chunkNibbleArray2, o));
					if (j > q) {
						j = q;
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
	public void method_15514(BlockPos blockPos, int i) {
		this.lightStorage.updateAll();
		this.scheduleNewLevelUpdate(-1L, blockPos.asLong(), 15 - i, true);
	}
}
