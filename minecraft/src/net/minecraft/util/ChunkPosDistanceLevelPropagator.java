package net.minecraft.util;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.light.LevelPropagator;

public abstract class ChunkPosDistanceLevelPropagator extends LevelPropagator {
	protected ChunkPosDistanceLevelPropagator(int i, int j, int k) {
		super(i, j, k);
	}

	@Override
	protected boolean isMarker(long l) {
		return l == ChunkPos.MARKER;
	}

	@Override
	protected void propagateLevel(long l, int i, boolean bl) {
		ChunkPos chunkPos = new ChunkPos(l);
		int j = chunkPos.x;
		int k = chunkPos.z;

		for (int m = -1; m <= 1; m++) {
			for (int n = -1; n <= 1; n++) {
				long o = ChunkPos.toLong(j + m, k + n);
				if (o != l) {
					this.propagateLevel(l, o, i, bl);
				}
			}
		}
	}

	@Override
	protected int recalculateLevel(long l, long m, int i) {
		int j = i;
		ChunkPos chunkPos = new ChunkPos(l);
		int k = chunkPos.x;
		int n = chunkPos.z;

		for (int o = -1; o <= 1; o++) {
			for (int p = -1; p <= 1; p++) {
				long q = ChunkPos.toLong(k + o, n + p);
				if (q == l) {
					q = ChunkPos.MARKER;
				}

				if (q != m) {
					int r = this.getPropagatedLevel(q, l, this.getLevel(q));
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
	protected int getPropagatedLevel(long l, long m, int i) {
		return l == ChunkPos.MARKER ? this.getInitialLevel(m) : i + 1;
	}

	protected abstract int getInitialLevel(long l);

	public void updateLevel(long l, int i, boolean bl) {
		this.updateLevel(ChunkPos.MARKER, l, i, bl);
	}
}
