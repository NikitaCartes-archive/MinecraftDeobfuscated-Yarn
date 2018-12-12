package net.minecraft;

import net.minecraft.sortme.LevelIndexedProcessor;
import net.minecraft.world.chunk.ChunkPos;

public abstract class class_3196 extends LevelIndexedProcessor {
	protected static final long CHUNK_POS_OUT_OF_WORLD = ChunkPos.toLong(1875016, 1875016);

	protected class_3196(int i, int j, int k) {
		super(i, j, k);
	}

	@Override
	protected boolean isInvalidIndex(long l) {
		return l == CHUNK_POS_OUT_OF_WORLD;
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
					q = CHUNK_POS_OUT_OF_WORLD;
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
		return l == CHUNK_POS_OUT_OF_WORLD ? this.method_14028(m) : i + 1;
	}

	protected abstract int method_14028(long l);

	public void scheduleNewLevelUpdate(long l, int i, boolean bl) {
		this.scheduleNewLevelUpdate(CHUNK_POS_OUT_OF_WORLD, l, i, bl);
	}
}
