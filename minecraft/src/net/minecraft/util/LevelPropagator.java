package net.minecraft.util;

import it.unimi.dsi.fastutil.longs.Long2ByteFunction;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;

public abstract class LevelPropagator {
	private final int levelCount;
	private final LongLinkedOpenHashSet[] levelToIds;
	private final Long2ByteFunction idToLevel;
	private int minLevel;
	private volatile boolean hasUpdates;

	protected LevelPropagator(int i, int j, int k) {
		if (i >= 254) {
			throw new IllegalArgumentException("Level count must be < 254.");
		} else {
			this.levelCount = i;
			this.levelToIds = new LongLinkedOpenHashSet[i];

			for (int l = 0; l < i; l++) {
				this.levelToIds[l] = new LongLinkedOpenHashSet(j, 0.5F) {
					@Override
					protected void rehash(int i) {
						if (i > j) {
							super.rehash(i);
						}
					}
				};
			}

			this.idToLevel = new Long2ByteOpenHashMap(k, 0.5F) {
				@Override
				protected void rehash(int i) {
					if (i > k) {
						super.rehash(i);
					}
				}
			};
			this.idToLevel.defaultReturnValue((byte)-1);
			this.minLevel = i;
		}
	}

	private int minLevel(int i, int j) {
		int k = i;
		if (i > j) {
			k = j;
		}

		if (k > this.levelCount - 1) {
			k = this.levelCount - 1;
		}

		return k;
	}

	private void updateMinLevel(int i) {
		int j = this.minLevel;
		this.minLevel = i;

		for (int k = j + 1; k < i; k++) {
			if (!this.levelToIds[k].isEmpty()) {
				this.minLevel = k;
				break;
			}
		}
	}

	public void remove(long l) {
		int i = this.idToLevel.get(l) & 255;
		if (i != 255) {
			int j = this.getLevel(l);
			int k = this.minLevel(j, i);
			this.removeFromLevel(l, k, this.levelCount, true);
			this.hasUpdates = this.minLevel < this.levelCount;
		}
	}

	private void removeFromLevel(long l, int i, int j, boolean bl) {
		if (bl) {
			this.idToLevel.remove(l);
		}

		this.levelToIds[i].remove(l);
		if (this.levelToIds[i].isEmpty() && this.minLevel == i) {
			this.updateMinLevel(j);
		}
	}

	private void add(long l, int i, int j) {
		if (i <= this.levelCount - 1) {
			i = Math.min(this.levelCount - 1, i);
			this.idToLevel.put(l, (byte)i);
			this.levelToIds[j].add(l);
			if (this.minLevel > j) {
				this.minLevel = j;
			}
		}
	}

	protected void update(long l) {
		this.update(l, l, this.levelCount - 1, false);
	}

	public void update(long l, long m, int i, boolean bl) {
		this.update(l, m, i, this.getLevel(m), this.idToLevel.get(m) & 255, bl);
		this.hasUpdates = this.minLevel < this.levelCount;
	}

	private void update(long l, long m, int i, int j, int k, boolean bl) {
		if (!this.isInvalid(m)) {
			if (i >= this.levelCount) {
				i = this.levelCount;
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

				this.add(m, n, p);
			} else if (!bl2) {
				this.removeFromLevel(m, o, this.levelCount, true);
			}
		}
	}

	protected final void updateRecursively(long l, long m, int i, boolean bl) {
		int j = this.idToLevel.get(m) & 255;
		int k = this.getUpdatedLevel(l, m, i);
		if (bl) {
			this.update(l, m, k, this.getLevel(m), j, true);
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
				this.update(l, m, this.levelCount - 1, bl2 ? n : this.getLevel(m), j, false);
			}
		}
	}

	public final boolean hasLevelUpdates() {
		return this.hasUpdates;
	}

	public final int updateAllRecursively(int i) {
		if (this.minLevel >= this.levelCount) {
			return i;
		} else {
			while (this.minLevel < this.levelCount && i > 0) {
				i--;
				LongLinkedOpenHashSet longLinkedOpenHashSet = this.levelToIds[this.minLevel];
				long l = longLinkedOpenHashSet.removeFirstLong();
				int j = this.getLevel(l);
				if (longLinkedOpenHashSet.isEmpty()) {
					this.updateMinLevel(this.levelCount);
				}

				int k = this.idToLevel.remove(l) & 255;
				if (k < j) {
					this.setLevel(l, k);
					this.updateNeighborsRecursively(l, k, true);
				} else if (k > j) {
					this.add(l, k, this.minLevel(this.levelCount - 1, k));
					this.setLevel(l, this.levelCount - 1);
					this.updateNeighborsRecursively(l, j, false);
				}
			}

			this.hasUpdates = this.minLevel < this.levelCount;
			return i;
		}
	}

	protected abstract boolean isInvalid(long l);

	protected abstract int getMergedLevel(long l, long m, int i);

	protected abstract void updateNeighborsRecursively(long l, int i, boolean bl);

	protected abstract int getLevel(long l);

	protected abstract void setLevel(long l, int i);

	protected abstract int getUpdatedLevel(long l, long m, int i);
}
