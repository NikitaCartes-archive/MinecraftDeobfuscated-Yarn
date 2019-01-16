package net.minecraft.server.world;

import net.minecraft.util.LevelIndexedProcessor;
import net.minecraft.world.chunk.ChunkPos;

public abstract class ChunkLevelIndexedProcessor extends LevelIndexedProcessor {
	protected ChunkLevelIndexedProcessor(int i, int j, int k) {
		super(i, j, k);
	}

	@Override
	protected boolean isInvalidIndex(long l) {
		return l == ChunkPos.INVALID;
	}

	@Override
	protected void processLevelAt(long l, int i, boolean bl) {
		ChunkPos chunkPos = new ChunkPos(l);
		int j = chunkPos.x;
		int k = chunkPos.z;

		for (int m = -1; m <= 1; m++) {
			for (int n = -1; n <= 1; n++) {
				long o = ChunkPos.toLong(j + m, k + n);
				if (o != l) {
					this.scheduleUpdateRecursively(l, o, i, bl);
				}
			}
		}
	}

	@Override
	protected int getMergedLevel(long l, long m, int i) {
		int j = i;
		ChunkPos chunkPos = new ChunkPos(l);
		int k = chunkPos.x;
		int n = chunkPos.z;

		for (int o = -1; o <= 1; o++) {
			for (int p = -1; p <= 1; p++) {
				long q = ChunkPos.toLong(k + o, n + p);
				if (q == l) {
					q = ChunkPos.INVALID;
				}

				if (q != m) {
					int r = this.getBaseLevelFor(q, l, this.getCurrentLevelFor(q));
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
	protected int getBaseLevelFor(long l, long m, int i) {
		return l == ChunkPos.INVALID ? this.getLevel(m) : i + 1;
	}

	protected abstract int getLevel(long l);

	public void scheduleNewLevelUpdate(long l, int i, boolean bl) {
		this.scheduleNewLevelUpdate(ChunkPos.INVALID, l, i, bl);
	}
}
