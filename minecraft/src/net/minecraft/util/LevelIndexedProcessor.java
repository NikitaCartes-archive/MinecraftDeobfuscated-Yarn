package net.minecraft.util;

import it.unimi.dsi.fastutil.longs.Long2ByteFunction;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;

public abstract class LevelIndexedProcessor {
	private final int maxLevelCount;
	private final LongLinkedOpenHashSet[] toProcess;
	private final Long2ByteFunction levels;
	private int minUnprocessedLevel;
	private volatile boolean hasUpdates;

	protected LevelIndexedProcessor(int i, int j, int k) {
		if (i >= 254) {
			throw new IllegalArgumentException("Level count must be < 254.");
		} else {
			this.maxLevelCount = i;
			this.toProcess = new LongLinkedOpenHashSet[i];

			for (int l = 0; l < i; l++) {
				this.toProcess[l] = new LongLinkedOpenHashSet(j, 0.5F) {
					@Override
					protected void rehash(int i) {
						if (i > j) {
							super.rehash(i);
						}
					}
				};
			}

			this.levels = new Long2ByteOpenHashMap(k, 0.5F) {
				@Override
				protected void rehash(int i) {
					if (i > k) {
						super.rehash(i);
					}
				}
			};
			this.levels.defaultReturnValue((byte)-1);
			this.minUnprocessedLevel = i;
		}
	}

	private int minLevel(int i, int j) {
		int k = i;
		if (i > j) {
			k = j;
		}

		if (k > this.maxLevelCount - 1) {
			k = this.maxLevelCount - 1;
		}

		return k;
	}

	private void updateMinUnprocessed(int i) {
		int j = this.minUnprocessedLevel;
		this.minUnprocessedLevel = i;

		for (int k = j + 1; k < i; k++) {
			if (!this.toProcess[k].isEmpty()) {
				this.minUnprocessedLevel = k;
				break;
			}
		}
	}

	public void remove(long l) {
		int i = this.levels.get(l) & 255;
		if (i != 255) {
			int j = this.getLevel(l);
			int k = this.minLevel(j, i);
			this.removeFromLevel(l, k, this.maxLevelCount, true);
			this.hasUpdates = this.minUnprocessedLevel < this.maxLevelCount;
		}
	}

	private void removeFromLevel(long l, int i, int j, boolean bl) {
		if (bl) {
			this.levels.remove(l);
		}

		this.toProcess[i].remove(l);
		if (this.toProcess[i].isEmpty() && this.minUnprocessedLevel == i) {
			this.updateMinUnprocessed(j);
		}
	}

	private void addWithLevel(long l, int i, int j) {
		if (i <= this.maxLevelCount - 1) {
			i = Math.min(this.maxLevelCount - 1, i);
			this.levels.put(l, (byte)i);
			this.toProcess[j].add(l);
			if (this.minUnprocessedLevel > j) {
				this.minUnprocessedLevel = j;
			}
		}
	}

	protected void scheduleNewUpdate(long l) {
		this.scheduleNewLevelUpdate(l, l, this.maxLevelCount - 1, false);
	}

	public void scheduleNewLevelUpdate(long l, long m, int i, boolean bl) {
		this.scheduleUpdate(l, m, i, this.getLevel(m), this.levels.get(m) & 255, bl);
		this.hasUpdates = this.minUnprocessedLevel < this.maxLevelCount;
	}

	private void scheduleUpdate(long l, long m, int i, int j, int k, boolean bl) {
		if (!this.isInvalidIndex(m)) {
			if (i >= this.maxLevelCount) {
				i = this.maxLevelCount;
			}

			boolean bl2;
			if (k == 255) {
				bl2 = true;
				k = j;
			} else {
				bl2 = false;
			}

			int n;
			if (bl) {
				n = Math.min(k, i);
			} else {
				n = this.getMergedLevel(m, l, i);
			}

			int o = this.minLevel(j, k);
			if (j != n) {
				int p = this.minLevel(j, n);
				if (o != p && !bl2) {
					this.removeFromLevel(m, o, p, false);
				}

				this.addWithLevel(m, n, p);
			} else if (!bl2) {
				this.removeFromLevel(m, o, this.maxLevelCount, true);
			}
		}
	}

	protected final void scheduleUpdateRecursively(long l, long m, int i, boolean bl) {
		int j = this.levels.get(m) & 255;
		int k = this.getBaseLevel(l, m, i);
		if (bl) {
			this.scheduleUpdate(l, m, k, this.getLevel(m), j, true);
		} else {
			int n;
			boolean bl2;
			if (j == 255) {
				bl2 = true;
				n = this.getLevel(m);
			} else {
				n = j;
				bl2 = false;
			}

			if (k == n) {
				this.scheduleUpdate(l, m, this.maxLevelCount - 1, bl2 ? n : this.getLevel(m), j, false);
			}
		}
	}

	public final boolean hasLevelUpdates() {
		return this.hasUpdates;
	}

	public final int updateLevels(int i) {
		if (this.minUnprocessedLevel >= this.maxLevelCount) {
			return i;
		} else {
			while (this.minUnprocessedLevel < this.maxLevelCount && i > 0) {
				i--;
				LongLinkedOpenHashSet longLinkedOpenHashSet = this.toProcess[this.minUnprocessedLevel];
				long l = longLinkedOpenHashSet.removeFirstLong();
				int j = this.getLevel(l);
				if (longLinkedOpenHashSet.isEmpty()) {
					this.updateMinUnprocessed(this.maxLevelCount);
				}

				int k = this.levels.remove(l) & 255;
				if (k < j) {
					this.setLevel(l, k);
					this.processLevel(l, k, true);
				} else if (k > j) {
					this.addWithLevel(l, k, this.minLevel(this.maxLevelCount - 1, k));
					this.setLevel(l, this.maxLevelCount - 1);
					this.processLevel(l, j, false);
				}
			}

			this.hasUpdates = this.minUnprocessedLevel < this.maxLevelCount;
			return i;
		}
	}

	protected abstract boolean isInvalidIndex(long l);

	protected abstract int getMergedLevel(long l, long m, int i);

	protected abstract void processLevel(long l, int i, boolean bl);

	protected abstract int getLevel(long l);

	protected abstract void setLevel(long l, int i);

	protected abstract int getBaseLevel(long l, long m, int i);
}
