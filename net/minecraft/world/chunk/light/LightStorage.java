/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk.light;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Iterator;
import net.minecraft.util.SectionDistanceLevelPropagator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkToNibbleArrayMap;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import org.jetbrains.annotations.Nullable;

public abstract class LightStorage<M extends ChunkToNibbleArrayMap<M>>
extends SectionDistanceLevelPropagator {
    protected static final ChunkNibbleArray EMPTY = new ChunkNibbleArray();
    private static final Direction[] DIRECTIONS = Direction.values();
    private final LightType lightType;
    private final ChunkProvider chunkProvider;
    protected final LongSet field_15808 = new LongOpenHashSet();
    protected final LongSet field_15797 = new LongOpenHashSet();
    protected final LongSet field_15804 = new LongOpenHashSet();
    protected volatile M uncachedLightArrays;
    protected final M lightArrays;
    protected final LongSet field_15802 = new LongOpenHashSet();
    protected final LongSet dirtySections = new LongOpenHashSet();
    protected final Long2ObjectMap<ChunkNibbleArray> lightArraysToAdd = new Long2ObjectOpenHashMap<ChunkNibbleArray>();
    private final LongSet field_19342 = new LongOpenHashSet();
    private final LongSet lightArraysToRemove = new LongOpenHashSet();
    protected volatile boolean hasLightUpdates;

    protected LightStorage(LightType lightType, ChunkProvider chunkProvider, M chunkToNibbleArrayMap) {
        super(3, 16, 256);
        this.lightType = lightType;
        this.chunkProvider = chunkProvider;
        this.lightArrays = chunkToNibbleArrayMap;
        this.uncachedLightArrays = ((ChunkToNibbleArrayMap)chunkToNibbleArrayMap).copy();
        ((ChunkToNibbleArrayMap)this.uncachedLightArrays).disableCache();
    }

    protected boolean hasLight(long l) {
        return this.getLightArray(l, true) != null;
    }

    @Nullable
    protected ChunkNibbleArray getLightArray(long l, boolean bl) {
        return this.getLightArray(bl ? this.lightArrays : this.uncachedLightArrays, l);
    }

    @Nullable
    protected ChunkNibbleArray getLightArray(M chunkToNibbleArrayMap, long l) {
        return ((ChunkToNibbleArrayMap)chunkToNibbleArrayMap).get(l);
    }

    @Nullable
    public ChunkNibbleArray method_20533(long l) {
        ChunkNibbleArray chunkNibbleArray = (ChunkNibbleArray)this.lightArraysToAdd.get(l);
        if (chunkNibbleArray != null) {
            return chunkNibbleArray;
        }
        return this.getLightArray(l, false);
    }

    protected abstract int getLight(long var1);

    protected int get(long l) {
        long m = ChunkSectionPos.fromGlobalPos(l);
        ChunkNibbleArray chunkNibbleArray = this.getLightArray(m, true);
        return chunkNibbleArray.get(ChunkSectionPos.getLocalCoord(BlockPos.unpackLongX(l)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongY(l)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongZ(l)));
    }

    protected void set(long l, int i) {
        long m = ChunkSectionPos.fromGlobalPos(l);
        if (this.field_15802.add(m)) {
            ((ChunkToNibbleArrayMap)this.lightArrays).replaceWithCopy(m);
        }
        ChunkNibbleArray chunkNibbleArray = this.getLightArray(m, true);
        chunkNibbleArray.set(ChunkSectionPos.getLocalCoord(BlockPos.unpackLongX(l)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongY(l)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongZ(l)), i);
        for (int j = -1; j <= 1; ++j) {
            for (int k = -1; k <= 1; ++k) {
                for (int n = -1; n <= 1; ++n) {
                    this.dirtySections.add(ChunkSectionPos.fromGlobalPos(BlockPos.add(l, k, n, j)));
                }
            }
        }
    }

    @Override
    protected int getLevel(long l) {
        if (l == Long.MAX_VALUE) {
            return 2;
        }
        if (this.field_15808.contains(l)) {
            return 0;
        }
        if (!this.lightArraysToRemove.contains(l) && ((ChunkToNibbleArrayMap)this.lightArrays).containsKey(l)) {
            return 1;
        }
        return 2;
    }

    @Override
    protected int getInitialLevel(long l) {
        if (this.field_15797.contains(l)) {
            return 2;
        }
        if (this.field_15808.contains(l) || this.field_15804.contains(l)) {
            return 0;
        }
        return 2;
    }

    @Override
    protected void setLevel(long l, int i) {
        int j = this.getLevel(l);
        if (j != 0 && i == 0) {
            this.field_15808.add(l);
            this.field_15804.remove(l);
        }
        if (j == 0 && i != 0) {
            this.field_15808.remove(l);
            this.field_15797.remove(l);
        }
        if (j >= 2 && i != 2) {
            if (this.lightArraysToRemove.contains(l)) {
                this.lightArraysToRemove.remove(l);
            } else {
                ((ChunkToNibbleArrayMap)this.lightArrays).put(l, this.createLightArray(l));
                this.field_15802.add(l);
                this.onLightArrayCreated(l);
                for (int k = -1; k <= 1; ++k) {
                    for (int m = -1; m <= 1; ++m) {
                        for (int n = -1; n <= 1; ++n) {
                            this.dirtySections.add(ChunkSectionPos.fromGlobalPos(BlockPos.add(l, m, n, k)));
                        }
                    }
                }
            }
        }
        if (j != 2 && i >= 2) {
            this.lightArraysToRemove.add(l);
        }
        this.hasLightUpdates = !this.lightArraysToRemove.isEmpty();
    }

    protected ChunkNibbleArray createLightArray(long l) {
        ChunkNibbleArray chunkNibbleArray = (ChunkNibbleArray)this.lightArraysToAdd.get(l);
        if (chunkNibbleArray != null) {
            return chunkNibbleArray;
        }
        return new ChunkNibbleArray();
    }

    protected void removeChunkData(ChunkLightProvider<?, ?> chunkLightProvider, long l) {
        int i = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(l));
        int j = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(l));
        int k = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(l));
        for (int m = 0; m < 16; ++m) {
            for (int n = 0; n < 16; ++n) {
                for (int o = 0; o < 16; ++o) {
                    long p = BlockPos.asLong(i + m, j + n, k + o);
                    chunkLightProvider.removePendingUpdate(p);
                }
            }
        }
    }

    protected boolean hasLightUpdates() {
        return this.hasLightUpdates;
    }

    protected void updateLightArrays(ChunkLightProvider<M, ?> chunkLightProvider, boolean bl, boolean bl2) {
        long m;
        ChunkNibbleArray chunkNibbleArray2;
        long l;
        if (!this.hasLightUpdates() && this.lightArraysToAdd.isEmpty()) {
            return;
        }
        Iterator<Long> iterator = this.lightArraysToRemove.iterator();
        while (iterator.hasNext()) {
            l = (Long)iterator.next();
            this.removeChunkData(chunkLightProvider, l);
            ChunkNibbleArray chunkNibbleArray = (ChunkNibbleArray)this.lightArraysToAdd.remove(l);
            chunkNibbleArray2 = ((ChunkToNibbleArrayMap)this.lightArrays).removeChunk(l);
            if (!this.field_19342.contains(ChunkSectionPos.withZeroZ(l))) continue;
            if (chunkNibbleArray != null) {
                this.lightArraysToAdd.put(l, chunkNibbleArray);
                continue;
            }
            if (chunkNibbleArray2 == null) continue;
            this.lightArraysToAdd.put(l, chunkNibbleArray2);
        }
        ((ChunkToNibbleArrayMap)this.lightArrays).clearCache();
        iterator = this.lightArraysToRemove.iterator();
        while (iterator.hasNext()) {
            l = (Long)iterator.next();
            this.onChunkRemoved(l);
        }
        this.lightArraysToRemove.clear();
        this.hasLightUpdates = false;
        for (Long2ObjectMap.Entry entry : this.lightArraysToAdd.long2ObjectEntrySet()) {
            m = entry.getLongKey();
            if (!this.hasLight(m)) continue;
            chunkNibbleArray2 = (ChunkNibbleArray)entry.getValue();
            if (((ChunkToNibbleArrayMap)this.lightArrays).get(m) == chunkNibbleArray2) continue;
            this.removeChunkData(chunkLightProvider, m);
            ((ChunkToNibbleArrayMap)this.lightArrays).put(m, chunkNibbleArray2);
            this.field_15802.add(m);
        }
        ((ChunkToNibbleArrayMap)this.lightArrays).clearCache();
        if (!bl2) {
            for (long l2 : this.lightArraysToAdd.keySet()) {
                if (!this.hasLight(l2)) continue;
                int i = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(l2));
                int j = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(l2));
                int k = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(l2));
                for (Direction direction : DIRECTIONS) {
                    long n = ChunkSectionPos.offset(l2, direction);
                    if (this.lightArraysToAdd.containsKey(n) || !this.hasLight(n)) continue;
                    for (int o = 0; o < 16; ++o) {
                        for (int p = 0; p < 16; ++p) {
                            long r;
                            long q;
                            switch (direction) {
                                case DOWN: {
                                    q = BlockPos.asLong(i + p, j, k + o);
                                    r = BlockPos.asLong(i + p, j - 1, k + o);
                                    break;
                                }
                                case UP: {
                                    q = BlockPos.asLong(i + p, j + 16 - 1, k + o);
                                    r = BlockPos.asLong(i + p, j + 16, k + o);
                                    break;
                                }
                                case NORTH: {
                                    q = BlockPos.asLong(i + o, j + p, k);
                                    r = BlockPos.asLong(i + o, j + p, k - 1);
                                    break;
                                }
                                case SOUTH: {
                                    q = BlockPos.asLong(i + o, j + p, k + 16 - 1);
                                    r = BlockPos.asLong(i + o, j + p, k + 16);
                                    break;
                                }
                                case WEST: {
                                    q = BlockPos.asLong(i, j + o, k + p);
                                    r = BlockPos.asLong(i - 1, j + o, k + p);
                                    break;
                                }
                                default: {
                                    q = BlockPos.asLong(i + 16 - 1, j + o, k + p);
                                    r = BlockPos.asLong(i + 16, j + o, k + p);
                                }
                            }
                            chunkLightProvider.updateLevel(q, r, chunkLightProvider.getPropagatedLevel(q, r, chunkLightProvider.getLevel(q)), false);
                            chunkLightProvider.updateLevel(r, q, chunkLightProvider.getPropagatedLevel(r, q, chunkLightProvider.getLevel(r)), false);
                        }
                    }
                }
            }
        }
        Iterator objectIterator = this.lightArraysToAdd.long2ObjectEntrySet().iterator();
        while (objectIterator.hasNext()) {
            Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)objectIterator.next();
            m = entry.getLongKey();
            if (!this.hasLight(m)) continue;
            objectIterator.remove();
        }
    }

    protected void onLightArrayCreated(long l) {
    }

    protected void onChunkRemoved(long l) {
    }

    protected void method_15535(long l, boolean bl) {
    }

    public void method_20600(long l, boolean bl) {
        if (bl) {
            this.field_19342.add(l);
        } else {
            this.field_19342.remove(l);
        }
    }

    protected void setLightArray(long l, @Nullable ChunkNibbleArray chunkNibbleArray) {
        if (chunkNibbleArray != null) {
            this.lightArraysToAdd.put(l, chunkNibbleArray);
        } else {
            this.lightArraysToAdd.remove(l);
        }
    }

    protected void updateSectionStatus(long l, boolean bl) {
        boolean bl2 = this.field_15808.contains(l);
        if (!bl2 && !bl) {
            this.field_15804.add(l);
            this.updateLevel(Long.MAX_VALUE, l, 0, true);
        }
        if (bl2 && bl) {
            this.field_15797.add(l);
            this.updateLevel(Long.MAX_VALUE, l, 2, false);
        }
    }

    protected void updateAll() {
        if (this.hasPendingUpdates()) {
            this.applyPendingUpdates(Integer.MAX_VALUE);
        }
    }

    protected void notifyChunkProvider() {
        if (!this.field_15802.isEmpty()) {
            Object chunkToNibbleArrayMap = ((ChunkToNibbleArrayMap)this.lightArrays).copy();
            ((ChunkToNibbleArrayMap)chunkToNibbleArrayMap).disableCache();
            this.uncachedLightArrays = chunkToNibbleArrayMap;
            this.field_15802.clear();
        }
        if (!this.dirtySections.isEmpty()) {
            LongIterator longIterator = this.dirtySections.iterator();
            while (longIterator.hasNext()) {
                long l = longIterator.nextLong();
                this.chunkProvider.onLightUpdate(this.lightType, ChunkSectionPos.from(l));
            }
            this.dirtySections.clear();
        }
    }
}

