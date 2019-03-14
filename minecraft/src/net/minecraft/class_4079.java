package net.minecraft;

import net.minecraft.util.LevelIndexedProcessor;
import net.minecraft.util.math.ChunkSectionPos;

public abstract class class_4079 extends LevelIndexedProcessor {
	protected class_4079(int i, int j, int k) {
		super(i, j, k);
	}

	@Override
	protected boolean isInvalidIndex(long l) {
		return l == Long.MAX_VALUE;
	}

	@Override
	protected void processLevel(long l, int i, boolean bl) {
		for (int j = -1; j <= 1; j++) {
			for (int k = -1; k <= 1; k++) {
				for (int m = -1; m <= 1; m++) {
					long n = ChunkSectionPos.offsetPacked(l, j, k, m);
					if (n != l) {
						this.scheduleUpdateRecursively(l, n, i, bl);
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
						int q = this.getBaseLevel(p, l, this.getLevel(p));
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
	protected int getBaseLevel(long l, long m, int i) {
		return l == Long.MAX_VALUE ? this.method_18749(m) : i + 1;
	}

	protected abstract int method_18749(long l);

	public void scheduleNewLevelUpdate(long l, int i, boolean bl) {
		this.scheduleNewLevelUpdate(Long.MAX_VALUE, l, i, bl);
	}
}
