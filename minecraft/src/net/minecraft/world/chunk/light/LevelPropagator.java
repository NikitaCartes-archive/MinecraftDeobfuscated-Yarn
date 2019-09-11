package net.minecraft.world.chunk.light;

import it.unimi.dsi.fastutil.longs.Long2ByteFunction;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import net.minecraft.util.math.MathHelper;

public abstract class LevelPropagator {
	private final int levelCount;
	private final LongLinkedOpenHashSet[] pendingIdUpdatesByLevel;
	private final Long2ByteFunction pendingUpdates;
	private int minPendingLevel;
	private volatile boolean hasPendingUpdates;

	protected LevelPropagator(int i, int j, int k) {
		if (i >= 254) {
			throw new IllegalArgumentException("Level count must be < 254.");
		} else {
			this.levelCount = i;
			this.pendingIdUpdatesByLevel = new LongLinkedOpenHashSet[i];

			for (int l = 0; l < i; l++) {
				this.pendingIdUpdatesByLevel[l] = new LongLinkedOpenHashSet(j, 0.5F) {
					@Override
					protected void rehash(int i) {
						if (i > j) {
							super.rehash(i);
						}
					}
				};
			}

			this.pendingUpdates = new Long2ByteOpenHashMap(k, 0.5F) {
				@Override
				protected void rehash(int i) {
					if (i > k) {
						super.rehash(i);
					}
				}
			};
			this.pendingUpdates.defaultReturnValue((byte)-1);
			this.minPendingLevel = i;
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

	private void increaseMinPendingLevel(int i) {
		int j = this.minPendingLevel;
		this.minPendingLevel = i;

		for (int k = j + 1; k < i; k++) {
			if (!this.pendingIdUpdatesByLevel[k].isEmpty()) {
				this.minPendingLevel = k;
				break;
			}
		}
	}

	protected void removePendingUpdate(long l) {
		int i = this.pendingUpdates.get(l) & 255;
		if (i != 255) {
			int j = this.getLevel(l);
			int k = this.minLevel(j, i);
			this.removePendingUpdate(l, k, this.levelCount, true);
			this.hasPendingUpdates = this.minPendingLevel < this.levelCount;
		}
	}

	private void removePendingUpdate(long l, int i, int j, boolean bl) {
		if (bl) {
			this.pendingUpdates.remove(l);
		}

		this.pendingIdUpdatesByLevel[i].remove(l);
		if (this.pendingIdUpdatesByLevel[i].isEmpty() && this.minPendingLevel == i) {
			this.increaseMinPendingLevel(j);
		}
	}

	private void addPendingUpdate(long l, int i, int j) {
		this.pendingUpdates.put(l, (byte)i);
		this.pendingIdUpdatesByLevel[j].add(l);
		if (this.minPendingLevel > j) {
			this.minPendingLevel = j;
		}
	}

	protected void resetLevel(long l) {
		this.updateLevel(l, l, this.levelCount - 1, false);
	}

	protected void updateLevel(long l, long m, int i, boolean bl) {
		this.updateLevel(l, m, i, this.getLevel(m), this.pendingUpdates.get(m) & 255, bl);
		this.hasPendingUpdates = this.minPendingLevel < this.levelCount;
	}

	private void updateLevel(long l, long m, int i, int j, int k, boolean bl) {
		if (!this.isMarker(m)) {
			i = MathHelper.clamp(i, 0, this.levelCount - 1);
			j = MathHelper.clamp(j, 0, this.levelCount - 1);
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
				n = MathHelper.clamp(this.recalculateLevel(m, l, i), 0, this.levelCount - 1);
			}

			int o = this.minLevel(j, k);
			if (j != n) {
				int p = this.minLevel(j, n);
				if (o != p && !bl2) {
					this.removePendingUpdate(m, o, p, false);
				}

				this.addPendingUpdate(m, n, p);
			} else if (!bl2) {
				this.removePendingUpdate(m, o, this.levelCount, true);
			}
		}
	}

	protected final void propagateLevel(long l, long m, int i, boolean bl) {
		int j = this.pendingUpdates.get(m) & 255;
		int k = MathHelper.clamp(this.getPropagatedLevel(l, m, i), 0, this.levelCount - 1);
		if (bl) {
			this.updateLevel(l, m, k, this.getLevel(m), j, true);
		} else {
			int n;
			boolean bl2;
			if (j == 255) {
				bl2 = true;
				n = MathHelper.clamp(this.getLevel(m), 0, this.levelCount - 1);
			} else {
				n = j;
				bl2 = false;
			}

			if (k == n) {
				this.updateLevel(l, m, this.levelCount - 1, bl2 ? n : this.getLevel(m), j, false);
			}
		}
	}

	protected final boolean hasPendingUpdates() {
		return this.hasPendingUpdates;
	}

	protected final int applyPendingUpdates(int i) {
		if (this.minPendingLevel >= this.levelCount) {
			return i;
		} else {
			while (this.minPendingLevel < this.levelCount && i > 0) {
				i--;
				LongLinkedOpenHashSet longLinkedOpenHashSet = this.pendingIdUpdatesByLevel[this.minPendingLevel];
				long l = longLinkedOpenHashSet.removeFirstLong();
				int j = MathHelper.clamp(this.getLevel(l), 0, this.levelCount - 1);
				if (longLinkedOpenHashSet.isEmpty()) {
					this.increaseMinPendingLevel(this.levelCount);
				}

				int k = this.pendingUpdates.remove(l) & 255;
				if (k < j) {
					this.setLevel(l, k);
					this.propagateLevel(l, k, true);
				} else if (k > j) {
					this.addPendingUpdate(l, k, this.minLevel(this.levelCount - 1, k));
					this.setLevel(l, this.levelCount - 1);
					this.propagateLevel(l, j, false);
				}
			}

			this.hasPendingUpdates = this.minPendingLevel < this.levelCount;
			return i;
		}
	}

	protected abstract boolean isMarker(long l);

	protected abstract int recalculateLevel(long l, long m, int i);

	protected abstract void propagateLevel(long l, int i, boolean bl);

	protected abstract int getLevel(long l);

	protected abstract void setLevel(long l, int i);

	protected abstract int getPropagatedLevel(long l, long m, int i);
}
