/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk.light;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.util.function.LongPredicate;
import net.minecraft.util.math.MathHelper;

public abstract class LevelPropagator {
    private static final int field_31706 = 255;
    private final int levelCount;
    private final LongLinkedOpenHashSet[] pendingIdUpdatesByLevel;
    private final Long2ByteMap pendingUpdates;
    private int minPendingLevel;
    private volatile boolean hasPendingUpdates;

    protected LevelPropagator(int levelCount, final int expectedLevelSize, final int expectedTotalSize) {
        if (levelCount >= 254) {
            throw new IllegalArgumentException("Level count must be < 254.");
        }
        this.levelCount = levelCount;
        this.pendingIdUpdatesByLevel = new LongLinkedOpenHashSet[levelCount];
        for (int i = 0; i < levelCount; ++i) {
            this.pendingIdUpdatesByLevel[i] = new LongLinkedOpenHashSet(expectedLevelSize, 0.5f){

                @Override
                protected void rehash(int newN) {
                    if (newN > expectedLevelSize) {
                        super.rehash(newN);
                    }
                }
            };
        }
        this.pendingUpdates = new Long2ByteOpenHashMap(expectedTotalSize, 0.5f){

            @Override
            protected void rehash(int newN) {
                if (newN > expectedTotalSize) {
                    super.rehash(newN);
                }
            }
        };
        this.pendingUpdates.defaultReturnValue((byte)-1);
        this.minPendingLevel = levelCount;
    }

    private int minLevel(int a, int b) {
        int i = a;
        if (i > b) {
            i = b;
        }
        if (i > this.levelCount - 1) {
            i = this.levelCount - 1;
        }
        return i;
    }

    private void increaseMinPendingLevel(int maxLevel) {
        int i = this.minPendingLevel;
        this.minPendingLevel = maxLevel;
        for (int j = i + 1; j < maxLevel; ++j) {
            if (this.pendingIdUpdatesByLevel[j].isEmpty()) continue;
            this.minPendingLevel = j;
            break;
        }
    }

    protected void removePendingUpdate(long id) {
        int i = this.pendingUpdates.get(id) & 0xFF;
        if (i == 255) {
            return;
        }
        int j = this.getLevel(id);
        int k = this.minLevel(j, i);
        this.removePendingUpdate(id, k, this.levelCount, true);
        this.hasPendingUpdates = this.minPendingLevel < this.levelCount;
    }

    public void removePendingUpdateIf(LongPredicate predicate) {
        LongArrayList longList = new LongArrayList();
        this.pendingUpdates.keySet().forEach(l -> {
            if (predicate.test(l)) {
                longList.add(l);
            }
        });
        longList.forEach(this::removePendingUpdate);
    }

    private void removePendingUpdate(long id, int level, int levelCount, boolean removeFully) {
        if (removeFully) {
            this.pendingUpdates.remove(id);
        }
        this.pendingIdUpdatesByLevel[level].remove(id);
        if (this.pendingIdUpdatesByLevel[level].isEmpty() && this.minPendingLevel == level) {
            this.increaseMinPendingLevel(levelCount);
        }
    }

    private void addPendingUpdate(long id, int level, int targetLevel) {
        this.pendingUpdates.put(id, (byte)level);
        this.pendingIdUpdatesByLevel[targetLevel].add(id);
        if (this.minPendingLevel > targetLevel) {
            this.minPendingLevel = targetLevel;
        }
    }

    protected void resetLevel(long id) {
        this.updateLevel(id, id, this.levelCount - 1, false);
    }

    protected void updateLevel(long sourceId, long id, int level, boolean decrease) {
        this.updateLevel(sourceId, id, level, this.getLevel(id), this.pendingUpdates.get(id) & 0xFF, decrease);
        this.hasPendingUpdates = this.minPendingLevel < this.levelCount;
    }

    private void updateLevel(long sourceId, long id, int level, int currentLevel, int pendingLevel, boolean decrease) {
        boolean bl;
        if (this.isMarker(id)) {
            return;
        }
        level = MathHelper.clamp(level, 0, this.levelCount - 1);
        currentLevel = MathHelper.clamp(currentLevel, 0, this.levelCount - 1);
        if (pendingLevel == 255) {
            bl = true;
            pendingLevel = currentLevel;
        } else {
            bl = false;
        }
        int i = decrease ? Math.min(pendingLevel, level) : MathHelper.clamp(this.recalculateLevel(id, sourceId, level), 0, this.levelCount - 1);
        int j = this.minLevel(currentLevel, pendingLevel);
        if (currentLevel != i) {
            int k = this.minLevel(currentLevel, i);
            if (j != k && !bl) {
                this.removePendingUpdate(id, j, k, false);
            }
            this.addPendingUpdate(id, i, k);
        } else if (!bl) {
            this.removePendingUpdate(id, j, this.levelCount, true);
        }
    }

    protected final void propagateLevel(long sourceId, long targetId, int level, boolean decrease) {
        int i = this.pendingUpdates.get(targetId) & 0xFF;
        int j = MathHelper.clamp(this.getPropagatedLevel(sourceId, targetId, level), 0, this.levelCount - 1);
        if (decrease) {
            this.updateLevel(sourceId, targetId, j, this.getLevel(targetId), i, true);
        } else {
            int k;
            boolean bl;
            if (i == 255) {
                bl = true;
                k = MathHelper.clamp(this.getLevel(targetId), 0, this.levelCount - 1);
            } else {
                k = i;
                bl = false;
            }
            if (j == k) {
                this.updateLevel(sourceId, targetId, this.levelCount - 1, bl ? k : this.getLevel(targetId), i, false);
            }
        }
    }

    protected final boolean hasPendingUpdates() {
        return this.hasPendingUpdates;
    }

    protected final int applyPendingUpdates(int maxSteps) {
        if (this.minPendingLevel >= this.levelCount) {
            return maxSteps;
        }
        while (this.minPendingLevel < this.levelCount && maxSteps > 0) {
            int j;
            --maxSteps;
            LongLinkedOpenHashSet longLinkedOpenHashSet = this.pendingIdUpdatesByLevel[this.minPendingLevel];
            long l = longLinkedOpenHashSet.removeFirstLong();
            int i = MathHelper.clamp(this.getLevel(l), 0, this.levelCount - 1);
            if (longLinkedOpenHashSet.isEmpty()) {
                this.increaseMinPendingLevel(this.levelCount);
            }
            if ((j = this.pendingUpdates.remove(l) & 0xFF) < i) {
                this.setLevel(l, j);
                this.propagateLevel(l, j, true);
                continue;
            }
            if (j <= i) continue;
            this.addPendingUpdate(l, j, this.minLevel(this.levelCount - 1, j));
            this.setLevel(l, this.levelCount - 1);
            this.propagateLevel(l, i, false);
        }
        this.hasPendingUpdates = this.minPendingLevel < this.levelCount;
        return maxSteps;
    }

    public int getPendingUpdateCount() {
        return this.pendingUpdates.size();
    }

    protected abstract boolean isMarker(long var1);

    protected abstract int recalculateLevel(long var1, long var3, int var5);

    protected abstract void propagateLevel(long var1, int var3, boolean var4);

    protected abstract int getLevel(long var1);

    protected abstract void setLevel(long var1, int var3);

    protected abstract int getPropagatedLevel(long var1, long var3, int var5);
}

