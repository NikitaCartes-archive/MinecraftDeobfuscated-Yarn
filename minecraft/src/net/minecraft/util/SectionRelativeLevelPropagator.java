package net.minecraft.util;

import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.light.LevelPropagator;

public abstract class SectionRelativeLevelPropagator extends LevelPropagator {
	protected SectionRelativeLevelPropagator(int i, int j, int k) {
		super(i, j, k);
	}

	@Override
	protected boolean isInvalid(long l) {
		return l == Long.MAX_VALUE;
	}

	@Override
	protected void updateNeighborsRecursively(long l, int i, boolean bl) {
		for (int j = -1; j <= 1; j++) {
			for (int k = -1; k <= 1; k++) {
				for (int m = -1; m <= 1; m++) {
					long n = ChunkSectionPos.offsetPacked(l, j, k, m);
					if (n != l) {
						this.updateRecursively(l, n, i, bl);
					}
				}
			}
		}
	}

	@Override
	protected int getMergedLevel(long l, long m, int i) {
		int j = i;

		for (int k = -1; k <= 1; k++) {
			for (int n = -1; n <= 1; n++) {
				for (int o = -1; o <= 1; o++) {
					long p = ChunkSectionPos.offsetPacked(l, k, n, o);
					if (p == l) {
						p = Long.MAX_VALUE;
					}

					if (p != m) {
						int q = this.getPropagatedLevel(p, l, this.getLevel(p));
						if (j > q) {
							j = q;
						}

						if (j == 0) {
							return j;
						}
					}
				}
			}
		}

		return j;
	}

	@Override
	protected int getPropagatedLevel(long l, long m, int i) {
		return l == Long.MAX_VALUE ? this.getInitialLevel(m) : i + 1;
	}

	protected abstract int getInitialLevel(long l);

	public void update(long l, int i, boolean bl) {
		this.update(Long.MAX_VALUE, l, i, bl);
	}
}
