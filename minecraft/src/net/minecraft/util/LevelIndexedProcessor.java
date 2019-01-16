package net.minecraft.util;

import it.unimi.dsi.fastutil.longs.Long2ByteFunction;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;

public abstract class LevelIndexedProcessor {
	private final int maxLevelCount;
	private final LongLinkedOpenHashSet[] toProcess;
	private final Long2ByteFunction levelMap;
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

			this.levelMap = new Long2ByteOpenHashMap(k, 0.5F) {
				@Override
				protected void rehash(int i) {
					if (i > k) {
						super.rehash(i);
					}
				}
			};
			this.levelMap.defaultReturnValue((byte)-1);
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
		int i = this.levelMap.get(l) & 255;
		if (i != 255) {
			int j = this.getCurrentLevelFor(l);
			int k = this.minLevel(j, i);
			this.removeFromLevel(l, k, this.maxLevelCount, true);
			this.hasUpdates = this.minUnprocessedLevel < this.maxLevelCount;
		}
	}

	private void removeFromLevel(long l, int i, int j, boolean bl) {
		if (bl) {
			this.levelMap.remove(l);
		}

		this.toProcess[i].remove(l);
		if (this.toProcess[i].isEmpty() && this.minUnprocessedLevel == i) {
			this.updateMinUnprocessed(j);
		}
	}

	private void addWithLevel(long l, int i, int j) {
		if (i <= this.maxLevelCount - 1) {
			i = Math.min(this.maxLevelCount - 1, i);
			this.levelMap.put(l, (byte)i);
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
		this.scheduleUpdate(l, m, i, this.getCurrentLevelFor(m), this.levelMap.get(m) & 255, bl);
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
		int j = this.levelMap.get(m) & 255;
		int k = this.getBaseLevelFor(l, m, i);
		if (bl) {
			this.scheduleUpdate(l, m, k, this.getCurrentLevelFor(m), j, true);
		} else {
			int n;
			boolean bl2;
			if (j == 255) {
				bl2 = true;
				n = this.getCurrentLevelFor(m);
			} else {
				n = j;
				bl2 = false;
			}

			if (k == n) {
				this.scheduleUpdate(l, m, this.maxLevelCount - 1, bl2 ? n : this.getCurrentLevelFor(m), j, false);
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
				int j = this.getCurrentLevelFor(l);
				if (longLinkedOpenHashSet.isEmpty()) {
					this.updateMinUnprocessed(this.maxLevelCount);
				}

				int k = this.levelMap.remove(l) & 255;
				if (k < j) {
					this.setLevelFor(l, k);
					this.processLevelAt(l, k, true);
				} else if (k > j) {
					this.addWithLevel(l, k, this.minLevel(this.maxLevelCount - 1, k));
					this.setLevelFor(l, this.maxLevelCount - 1);
					this.processLevelAt(l, j, false);
				}
			}

			this.hasUpdates = this.minUnprocessedLevel < this.maxLevelCount;
			return i;
		}
	}

	protected abstract boolean isInvalidIndex(long l);

	protected abstract int getMergedLevel(long l, long m, int i);

	protected abstract void processLevelAt(long l, int i, boolean bl);

	protected abstract int getCurrentLevelFor(long l);

	protected abstract void setLevelFor(long l, int i);

	protected abstract int getBaseLevelFor(long l, long m, int i);
}
