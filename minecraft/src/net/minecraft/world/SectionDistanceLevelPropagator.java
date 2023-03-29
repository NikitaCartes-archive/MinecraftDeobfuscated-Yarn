package net.minecraft.world;

import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.light.LevelPropagator;

public abstract class SectionDistanceLevelPropagator extends LevelPropagator {
	protected SectionDistanceLevelPropagator(int i, int j, int k) {
		super(i, j, k);
	}

	@Override
	protected void propagateLevel(long id, int level, boolean decrease) {
		if (!decrease || level < this.levelCount - 2) {
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					for (int k = -1; k <= 1; k++) {
						long l = ChunkSectionPos.offset(id, i, j, k);
						if (l != id) {
							this.propagateLevel(id, l, level, decrease);
						}
					}
				}
			}
		}
	}

	@Override
	protected int recalculateLevel(long id, long excludedId, int maxLevel) {
		int i = maxLevel;

		for (int j = -1; j <= 1; j++) {
			for (int k = -1; k <= 1; k++) {
				for (int l = -1; l <= 1; l++) {
					long m = ChunkSectionPos.offset(id, j, k, l);
					if (m == id) {
						m = Long.MAX_VALUE;
					}

					if (m != excludedId) {
						int n = this.getPropagatedLevel(m, id, this.getLevel(m));
						if (i > n) {
							i = n;
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
	protected int getPropagatedLevel(long sourceId, long targetId, int level) {
		return this.isMarker(sourceId) ? this.getInitialLevel(targetId) : level + 1;
	}

	protected abstract int getInitialLevel(long id);

	public void update(long id, int level, boolean decrease) {
		this.updateLevel(Long.MAX_VALUE, id, level, decrease);
	}
}
