package net.minecraft.world.chunk.light;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import java.util.function.LongPredicate;
import net.minecraft.util.math.MathHelper;

public abstract class LevelPropagator {
	public static final long field_43397 = Long.MAX_VALUE;
	private static final int MAX_LEVEL = 255;
	protected final int levelCount;
	private final PendingUpdateQueue pendingUpdateQueue;
	private final Long2ByteMap pendingUpdates;
	private volatile boolean hasPendingUpdates;

	protected LevelPropagator(int levelCount, int expectedLevelSize, int expectedTotalSize) {
		if (levelCount >= 254) {
			throw new IllegalArgumentException("Level count must be < 254.");
		} else {
			this.levelCount = levelCount;
			this.pendingUpdateQueue = new PendingUpdateQueue(levelCount, expectedLevelSize);
			this.pendingUpdates = new Long2ByteOpenHashMap(expectedTotalSize, 0.5F) {
				@Override
				protected void rehash(int newN) {
					if (newN > expectedTotalSize) {
						super.rehash(newN);
					}
				}
			};
			this.pendingUpdates.defaultReturnValue((byte)-1);
		}
	}

	protected void removePendingUpdate(long id) {
		int i = this.pendingUpdates.remove(id) & 255;
		if (i != 255) {
			int j = this.getLevel(id);
			int k = this.calculateLevel(j, i);
			this.pendingUpdateQueue.remove(id, k, this.levelCount);
			this.hasPendingUpdates = !this.pendingUpdateQueue.isEmpty();
		}
	}

	public void removePendingUpdateIf(LongPredicate predicate) {
		LongList longList = new LongArrayList();
		this.pendingUpdates.keySet().forEach(l -> {
			if (predicate.test(l)) {
				longList.add(l);
			}
		});
		longList.forEach(this::removePendingUpdate);
	}

	private int calculateLevel(int a, int b) {
		return Math.min(Math.min(a, b), this.levelCount - 1);
	}

	protected void resetLevel(long id) {
		this.updateLevel(id, id, this.levelCount - 1, false);
	}

	protected void updateLevel(long sourceId, long id, int level, boolean decrease) {
		this.updateLevel(sourceId, id, level, this.getLevel(id), this.pendingUpdates.get(id) & 255, decrease);
		this.hasPendingUpdates = !this.pendingUpdateQueue.isEmpty();
	}

	private void updateLevel(long sourceId, long id, int level, int currentLevel, int i, boolean decrease) {
		if (!this.isMarker(id)) {
			level = MathHelper.clamp(level, 0, this.levelCount - 1);
			currentLevel = MathHelper.clamp(currentLevel, 0, this.levelCount - 1);
			boolean bl = i == 255;
			if (bl) {
				i = currentLevel;
			}

			int j;
			if (decrease) {
				j = Math.min(i, level);
			} else {
				j = MathHelper.clamp(this.recalculateLevel(id, sourceId, level), 0, this.levelCount - 1);
			}

			int k = this.calculateLevel(currentLevel, i);
			if (currentLevel != j) {
				int l = this.calculateLevel(currentLevel, j);
				if (k != l && !bl) {
					this.pendingUpdateQueue.remove(id, k, l);
				}

				this.pendingUpdateQueue.enqueue(id, l);
				this.pendingUpdates.put(id, (byte)j);
			} else if (!bl) {
				this.pendingUpdateQueue.remove(id, k, this.levelCount);
				this.pendingUpdates.remove(id);
			}
		}
	}

	protected final void propagateLevel(long sourceId, long targetId, int level, boolean decrease) {
		int i = this.pendingUpdates.get(targetId) & 255;
		int j = MathHelper.clamp(this.getPropagatedLevel(sourceId, targetId, level), 0, this.levelCount - 1);
		if (decrease) {
			this.updateLevel(sourceId, targetId, j, this.getLevel(targetId), i, decrease);
		} else {
			boolean bl = i == 255;
			int k;
			if (bl) {
				k = MathHelper.clamp(this.getLevel(targetId), 0, this.levelCount - 1);
			} else {
				k = i;
			}

			if (j == k) {
				this.updateLevel(sourceId, targetId, this.levelCount - 1, bl ? k : this.getLevel(targetId), i, decrease);
			}
		}
	}

	protected final boolean hasPendingUpdates() {
		return this.hasPendingUpdates;
	}

	protected final int applyPendingUpdates(int maxSteps) {
		if (this.pendingUpdateQueue.isEmpty()) {
			return maxSteps;
		} else {
			while (!this.pendingUpdateQueue.isEmpty() && maxSteps > 0) {
				maxSteps--;
				long l = this.pendingUpdateQueue.dequeue();
				int i = MathHelper.clamp(this.getLevel(l), 0, this.levelCount - 1);
				int j = this.pendingUpdates.remove(l) & 255;
				if (j < i) {
					this.setLevel(l, j);
					this.propagateLevel(l, j, true);
				} else if (j > i) {
					this.setLevel(l, this.levelCount - 1);
					if (j != this.levelCount - 1) {
						this.pendingUpdateQueue.enqueue(l, this.calculateLevel(this.levelCount - 1, j));
						this.pendingUpdates.put(l, (byte)j);
					}

					this.propagateLevel(l, i, false);
				}
			}

			this.hasPendingUpdates = !this.pendingUpdateQueue.isEmpty();
			return maxSteps;
		}
	}

	public int getPendingUpdateCount() {
		return this.pendingUpdates.size();
	}

	protected boolean isMarker(long id) {
		return id == Long.MAX_VALUE;
	}

	protected abstract int recalculateLevel(long id, long excludedId, int maxLevel);

	protected abstract void propagateLevel(long id, int level, boolean decrease);

	protected abstract int getLevel(long id);

	protected abstract void setLevel(long id, int level);

	protected abstract int getPropagatedLevel(long sourceId, long targetId, int level);
}
