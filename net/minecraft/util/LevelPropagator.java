/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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

    protected LevelPropagator(int i, final int j, final int k) {
        if (i >= 254) {
            throw new IllegalArgumentException("Level count must be < 254.");
        }
        this.levelCount = i;
        this.levelToIds = new LongLinkedOpenHashSet[i];
        for (int l = 0; l < i; ++l) {
            this.levelToIds[l] = new LongLinkedOpenHashSet(j, 0.5f){

                @Override
                protected void rehash(int i) {
                    if (i > j) {
                        super.rehash(i);
                    }
                }
            };
        }
        this.idToLevel = new Long2ByteOpenHashMap(k, 0.5f){

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

    private int min(int i, int j) {
        int k = i;
        if (k > j) {
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
        for (int k = j + 1; k < i; ++k) {
            if (this.levelToIds[k].isEmpty()) continue;
            this.minLevel = k;
            break;
        }
    }

    protected void remove(long l) {
        int i = this.idToLevel.get(l) & 0xFF;
        if (i == 255) {
            return;
        }
        int j = this.getLevel(l);
        int k = this.min(j, i);
        this.removeFromLevel(l, k, this.levelCount, true);
        this.hasUpdates = this.minLevel < this.levelCount;
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
        if (i > this.levelCount - 1) {
            return;
        }
        i = Math.min(this.levelCount - 1, i);
        this.idToLevel.put(l, (byte)i);
        this.levelToIds[j].add(l);
        if (this.minLevel > j) {
            this.minLevel = j;
        }
    }

    protected void fullyUpdate(long l) {
        this.update(l, l, this.levelCount - 1, false);
    }

    protected void update(long l, long m, int i, boolean bl) {
        this.update(l, m, i, this.getLevel(m), this.idToLevel.get(m) & 0xFF, bl);
        this.hasUpdates = this.minLevel < this.levelCount;
    }

    private void update(long l, long m, int i, int j, int k, boolean bl) {
        boolean bl2;
        if (this.isInvalid(m)) {
            return;
        }
        if (i >= this.levelCount) {
            i = this.levelCount;
        }
        if (k == 255) {
            bl2 = true;
            k = j;
        } else {
            bl2 = false;
        }
        int n = bl ? Math.min(k, i) : this.getMergedLevel(m, l, i);
        int o = this.min(j, k);
        if (j != n) {
            int p = this.min(j, n);
            if (o != p && !bl2) {
                this.removeFromLevel(m, o, p, false);
            }
            this.add(m, n, p);
        } else if (!bl2) {
            this.removeFromLevel(m, o, this.levelCount, true);
        }
    }

    protected final void updateRecursively(long l, long m, int i, boolean bl) {
        int j = this.idToLevel.get(m) & 0xFF;
        int k = this.getPropagatedLevel(l, m, i);
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

    protected final boolean hasLevelUpdates() {
        return this.hasUpdates;
    }

    protected final int updateAllRecursively(int i) {
        if (this.minLevel >= this.levelCount) {
            return i;
        }
        while (this.minLevel < this.levelCount && i > 0) {
            int k;
            --i;
            LongLinkedOpenHashSet longLinkedOpenHashSet = this.levelToIds[this.minLevel];
            long l = longLinkedOpenHashSet.removeFirstLong();
            int j = this.getLevel(l);
            if (longLinkedOpenHashSet.isEmpty()) {
                this.updateMinLevel(this.levelCount);
            }
            if ((k = this.idToLevel.remove(l) & 0xFF) < j) {
                this.setLevel(l, k);
                this.updateNeighborsRecursively(l, k, true);
                continue;
            }
            if (k <= j) continue;
            this.add(l, k, this.min(this.levelCount - 1, k));
            this.setLevel(l, this.levelCount - 1);
            this.updateNeighborsRecursively(l, j, false);
        }
        this.hasUpdates = this.minLevel < this.levelCount;
        return i;
    }

    protected abstract boolean isInvalid(long var1);

    protected abstract int getMergedLevel(long var1, long var3, int var5);

    protected abstract void updateNeighborsRecursively(long var1, int var3, boolean var4);

    protected abstract int getLevel(long var1);

    protected abstract void setLevel(long var1, int var3);

    protected abstract int getPropagatedLevel(long var1, long var3, int var5);
}

