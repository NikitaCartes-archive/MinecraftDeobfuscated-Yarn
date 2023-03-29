package net.minecraft.world.chunk.light;

import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;

public class PendingUpdateQueue {
	private final int levelCount;
	private final LongLinkedOpenHashSet[] pendingIdUpdatesByLevel;
	private int minPendingLevel;

	public PendingUpdateQueue(int levelCount, int expectedLevelSize) {
		this.levelCount = levelCount;
		this.pendingIdUpdatesByLevel = new LongLinkedOpenHashSet[levelCount];

		for (int i = 0; i < levelCount; i++) {
			this.pendingIdUpdatesByLevel[i] = new LongLinkedOpenHashSet(expectedLevelSize, 0.5F) {
				@Override
				protected void rehash(int newN) {
					if (newN > expectedLevelSize) {
						super.rehash(newN);
					}
				}
			};
		}

		this.minPendingLevel = levelCount;
	}

	public long dequeue() {
		LongLinkedOpenHashSet longLinkedOpenHashSet = this.pendingIdUpdatesByLevel[this.minPendingLevel];
		long l = longLinkedOpenHashSet.removeFirstLong();
		if (longLinkedOpenHashSet.isEmpty()) {
			this.increaseMinPendingLevel(this.levelCount);
		}

		return l;
	}

	public boolean isEmpty() {
		return this.minPendingLevel >= this.levelCount;
	}

	public void remove(long id, int level, int levelCount) {
		LongLinkedOpenHashSet longLinkedOpenHashSet = this.pendingIdUpdatesByLevel[level];
		longLinkedOpenHashSet.remove(id);
		if (longLinkedOpenHashSet.isEmpty() && this.minPendingLevel == level) {
			this.increaseMinPendingLevel(levelCount);
		}
	}

	public void enqueue(long id, int level) {
		this.pendingIdUpdatesByLevel[level].add(id);
		if (this.minPendingLevel > level) {
			this.minPendingLevel = level;
		}
	}

	private void increaseMinPendingLevel(int maxLevel) {
		int i = this.minPendingLevel;
		this.minPendingLevel = maxLevel;

		for (int j = i + 1; j < maxLevel; j++) {
			if (!this.pendingIdUpdatesByLevel[j].isEmpty()) {
				this.minPendingLevel = j;
				break;
			}
		}
	}
}
