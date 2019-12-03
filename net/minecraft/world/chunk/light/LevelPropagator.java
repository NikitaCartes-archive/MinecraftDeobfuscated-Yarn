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
    private final int levelCount;
    private final LongLinkedOpenHashSet[] pendingIdUpdatesByLevel;
    private final Long2ByteMap pendingUpdates;
    private int minPendingLevel;
    private volatile boolean hasPendingUpdates;

    protected LevelPropagator(int i, final int j, final int k) {
        if (i >= 254) {
            throw new IllegalArgumentException("Level count must be < 254.");
        }
        this.levelCount = i;
        this.pendingIdUpdatesByLevel = new LongLinkedOpenHashSet[i];
        for (int l = 0; l < i; ++l) {
            this.pendingIdUpdatesByLevel[l] = new LongLinkedOpenHashSet(j, 0.5f){

                @Override
                protected void rehash(int i) {
                    if (i > j) {
                        super.rehash(i);
                    }
                }
            };
        }
        this.pendingUpdates = new Long2ByteOpenHashMap(k, 0.5f){

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

    private int minLevel(int i, int j) {
        int k = i;
        if (k > j) {
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
        for (int k = j + 1; k < i; ++k) {
            if (this.pendingIdUpdatesByLevel[k].isEmpty()) continue;
            this.minPendingLevel = k;
            break;
        }
    }

    protected void removePendingUpdate(long l) {
        int i = this.pendingUpdates.get(l) & 0xFF;
        if (i == 255) {
            return;
        }
        int j = this.getLevel(l);
        int k = this.minLevel(j, i);
        this.removePendingUpdate(l, k, this.levelCount, true);
        this.hasPendingUpdates = this.minPendingLevel < this.levelCount;
    }

    public void method_24206(LongPredicate longPredicate) {
        LongArrayList longList = new LongArrayList(0);
        this.pendingUpdates.keySet().forEach(l -> {
            if (longPredicate.test(l)) {
                longList.add(l);
            }
        });
        longList.forEach(this::removePendingUpdate);
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
        this.updateLevel(l, m, i, this.getLevel(m), this.pendingUpdates.get(m) & 0xFF, bl);
        this.hasPendingUpdates = this.minPendingLevel < this.levelCount;
    }

    private void updateLevel(long l, long m, int i, int j, int k, boolean bl) {
        boolean bl2;
        if (this.isMarker(m)) {
            return;
        }
        i = MathHelper.clamp(i, 0, this.levelCount - 1);
        j = MathHelper.clamp(j, 0, this.levelCount - 1);
        if (k == 255) {
            bl2 = true;
            k = j;
        } else {
            bl2 = false;
        }
        int n = bl ? Math.min(k, i) : MathHelper.clamp(this.recalculateLevel(m, l, i), 0, this.levelCount - 1);
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

    protected final void propagateLevel(long l, long m, int i, boolean bl) {
        int j = this.pendingUpdates.get(m) & 0xFF;
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
        }
        while (this.minPendingLevel < this.levelCount && i > 0) {
            int k;
            --i;
            LongLinkedOpenHashSet longLinkedOpenHashSet = this.pendingIdUpdatesByLevel[this.minPendingLevel];
            long l = longLinkedOpenHashSet.removeFirstLong();
            int j = MathHelper.clamp(this.getLevel(l), 0, this.levelCount - 1);
            if (longLinkedOpenHashSet.isEmpty()) {
                this.increaseMinPendingLevel(this.levelCount);
            }
            if ((k = this.pendingUpdates.remove(l) & 0xFF) < j) {
                this.setLevel(l, k);
                this.propagateLevel(l, k, true);
                continue;
            }
            if (k <= j) continue;
            this.addPendingUpdate(l, k, this.minLevel(this.levelCount - 1, k));
            this.setLevel(l, this.levelCount - 1);
            this.propagateLevel(l, j, false);
        }
        this.hasPendingUpdates = this.minPendingLevel < this.levelCount;
        return i;
    }

    public int method_24208() {
        return this.pendingUpdates.size();
    }

    protected abstract boolean isMarker(long var1);

    protected abstract int recalculateLevel(long var1, long var3, int var5);

    protected abstract void propagateLevel(long var1, int var3, boolean var4);

    protected abstract int getLevel(long var1);

    protected abstract void setLevel(long var1, int var3);

    protected abstract int getPropagatedLevel(long var1, long var3, int var5);
}

