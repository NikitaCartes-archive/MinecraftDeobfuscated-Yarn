/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk.light;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Iterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.SectionDistanceLevelPropagator;
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
    protected final LongSet nonEmptySections = new LongOpenHashSet();
    protected final LongSet field_15797 = new LongOpenHashSet();
    protected final LongSet field_15804 = new LongOpenHashSet();
    protected volatile M uncachedLightArrays;
    protected final M lightArrays;
    protected final LongSet field_15802 = new LongOpenHashSet();
    protected final LongSet dirtySections = new LongOpenHashSet();
    protected final Long2ObjectMap<ChunkNibbleArray> lightArraysToAdd = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap());
    private final LongSet field_25621 = new LongOpenHashSet();
    private final LongSet field_19342 = new LongOpenHashSet();
    private final LongSet lightArraysToRemove = new LongOpenHashSet();
    protected volatile boolean hasLightUpdates;

    protected LightStorage(LightType lightType, ChunkProvider chunkProvider, M lightData) {
        super(3, 16, 256);
        this.lightType = lightType;
        this.chunkProvider = chunkProvider;
        this.lightArrays = lightData;
        this.uncachedLightArrays = ((ChunkToNibbleArrayMap)lightData).copy();
        ((ChunkToNibbleArrayMap)this.uncachedLightArrays).disableCache();
    }

    protected boolean hasLight(long sectionPos) {
        return this.getLightArray(sectionPos, true) != null;
    }

    @Nullable
    protected ChunkNibbleArray getLightArray(long sectionPos, boolean cached) {
        return this.getLightArray(cached ? this.lightArrays : this.uncachedLightArrays, sectionPos);
    }

    @Nullable
    protected ChunkNibbleArray getLightArray(M storage, long sectionPos) {
        return ((ChunkToNibbleArrayMap)storage).get(sectionPos);
    }

    @Nullable
    public ChunkNibbleArray getLightArray(long sectionPos) {
        ChunkNibbleArray chunkNibbleArray = (ChunkNibbleArray)this.lightArraysToAdd.get(sectionPos);
        if (chunkNibbleArray != null) {
            return chunkNibbleArray;
        }
        return this.getLightArray(sectionPos, false);
    }

    protected abstract int getLight(long var1);

    protected int get(long blockPos) {
        long l = ChunkSectionPos.fromGlobalPos(blockPos);
        ChunkNibbleArray chunkNibbleArray = this.getLightArray(l, true);
        return chunkNibbleArray.get(ChunkSectionPos.getLocalCoord(BlockPos.unpackLongX(blockPos)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongY(blockPos)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongZ(blockPos)));
    }

    protected void set(long blockPos, int value) {
        long l = ChunkSectionPos.fromGlobalPos(blockPos);
        if (this.field_15802.add(l)) {
            ((ChunkToNibbleArrayMap)this.lightArrays).replaceWithCopy(l);
        }
        ChunkNibbleArray chunkNibbleArray = this.getLightArray(l, true);
        chunkNibbleArray.set(ChunkSectionPos.getLocalCoord(BlockPos.unpackLongX(blockPos)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongY(blockPos)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongZ(blockPos)), value);
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                for (int k = -1; k <= 1; ++k) {
                    this.dirtySections.add(ChunkSectionPos.fromGlobalPos(BlockPos.add(blockPos, j, k, i)));
                }
            }
        }
    }

    @Override
    protected int getLevel(long id) {
        if (id == Long.MAX_VALUE) {
            return 2;
        }
        if (this.nonEmptySections.contains(id)) {
            return 0;
        }
        if (!this.lightArraysToRemove.contains(id) && ((ChunkToNibbleArrayMap)this.lightArrays).containsKey(id)) {
            return 1;
        }
        return 2;
    }

    @Override
    protected int getInitialLevel(long id) {
        if (this.field_15797.contains(id)) {
            return 2;
        }
        if (this.nonEmptySections.contains(id) || this.field_15804.contains(id)) {
            return 0;
        }
        return 2;
    }

    @Override
    protected void setLevel(long id, int level) {
        int i = this.getLevel(id);
        if (i != 0 && level == 0) {
            this.nonEmptySections.add(id);
            this.field_15804.remove(id);
        }
        if (i == 0 && level != 0) {
            this.nonEmptySections.remove(id);
            this.field_15797.remove(id);
        }
        if (i >= 2 && level != 2) {
            if (this.lightArraysToRemove.contains(id)) {
                this.lightArraysToRemove.remove(id);
            } else {
                ((ChunkToNibbleArrayMap)this.lightArrays).put(id, this.createLightArray(id));
                this.field_15802.add(id);
                this.onLightArrayCreated(id);
                for (int j = -1; j <= 1; ++j) {
                    for (int k = -1; k <= 1; ++k) {
                        for (int l = -1; l <= 1; ++l) {
                            this.dirtySections.add(ChunkSectionPos.fromGlobalPos(BlockPos.add(id, k, l, j)));
                        }
                    }
                }
            }
        }
        if (i != 2 && level >= 2) {
            this.lightArraysToRemove.add(id);
        }
        this.hasLightUpdates = !this.lightArraysToRemove.isEmpty();
    }

    protected ChunkNibbleArray createLightArray(long pos) {
        ChunkNibbleArray chunkNibbleArray = (ChunkNibbleArray)this.lightArraysToAdd.get(pos);
        if (chunkNibbleArray != null) {
            return chunkNibbleArray;
        }
        return new ChunkNibbleArray();
    }

    protected void removeChunkData(ChunkLightProvider<?, ?> storage, long blockChunkPos) {
        if (storage.method_24208() < 8192) {
            storage.method_24206(m -> ChunkSectionPos.fromGlobalPos(m) == blockChunkPos);
            return;
        }
        int i = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(blockChunkPos));
        int j = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(blockChunkPos));
        int k = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(blockChunkPos));
        for (int l = 0; l < 16; ++l) {
            for (int m2 = 0; m2 < 16; ++m2) {
                for (int n = 0; n < 16; ++n) {
                    long o = BlockPos.asLong(i + l, j + m2, k + n);
                    storage.removePendingUpdate(o);
                }
            }
        }
    }

    protected boolean hasLightUpdates() {
        return this.hasLightUpdates;
    }

    protected void updateLightArrays(ChunkLightProvider<M, ?> chunkLightProvider, boolean doSkylight, boolean skipEdgeLightPropagation) {
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
        if (!skipEdgeLightPropagation) {
            for (long l2 : this.lightArraysToAdd.keySet()) {
                this.method_29967(chunkLightProvider, l2);
            }
        } else {
            for (long l3 : this.field_25621) {
                this.method_29967(chunkLightProvider, l3);
            }
        }
        this.field_25621.clear();
        Iterator objectIterator = this.lightArraysToAdd.long2ObjectEntrySet().iterator();
        while (objectIterator.hasNext()) {
            Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)objectIterator.next();
            m = entry.getLongKey();
            if (!this.hasLight(m)) continue;
            objectIterator.remove();
        }
    }

    private void method_29967(ChunkLightProvider<M, ?> chunkLightProvider, long l) {
        if (!this.hasLight(l)) {
            return;
        }
        int i = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(l));
        int j = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(l));
        int k = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(l));
        for (Direction direction : DIRECTIONS) {
            long m = ChunkSectionPos.offset(l, direction);
            if (this.lightArraysToAdd.containsKey(m) || !this.hasLight(m)) continue;
            for (int n = 0; n < 16; ++n) {
                for (int o = 0; o < 16; ++o) {
                    long q;
                    long p;
                    switch (direction) {
                        case DOWN: {
                            p = BlockPos.asLong(i + o, j, k + n);
                            q = BlockPos.asLong(i + o, j - 1, k + n);
                            break;
                        }
                        case UP: {
                            p = BlockPos.asLong(i + o, j + 16 - 1, k + n);
                            q = BlockPos.asLong(i + o, j + 16, k + n);
                            break;
                        }
                        case NORTH: {
                            p = BlockPos.asLong(i + n, j + o, k);
                            q = BlockPos.asLong(i + n, j + o, k - 1);
                            break;
                        }
                        case SOUTH: {
                            p = BlockPos.asLong(i + n, j + o, k + 16 - 1);
                            q = BlockPos.asLong(i + n, j + o, k + 16);
                            break;
                        }
                        case WEST: {
                            p = BlockPos.asLong(i, j + n, k + o);
                            q = BlockPos.asLong(i - 1, j + n, k + o);
                            break;
                        }
                        default: {
                            p = BlockPos.asLong(i + 16 - 1, j + n, k + o);
                            q = BlockPos.asLong(i + 16, j + n, k + o);
                        }
                    }
                    chunkLightProvider.updateLevel(p, q, chunkLightProvider.getPropagatedLevel(p, q, chunkLightProvider.getLevel(p)), false);
                    chunkLightProvider.updateLevel(q, p, chunkLightProvider.getPropagatedLevel(q, p, chunkLightProvider.getLevel(q)), false);
                }
            }
        }
    }

    protected void onLightArrayCreated(long blockPos) {
    }

    protected void onChunkRemoved(long l) {
    }

    protected void setLightEnabled(long l, boolean bl) {
    }

    public void setRetainData(long l, boolean bl) {
        if (bl) {
            this.field_19342.add(l);
        } else {
            this.field_19342.remove(l);
        }
    }

    protected void setLightArray(long pos, @Nullable ChunkNibbleArray array, boolean bl) {
        if (array != null) {
            this.lightArraysToAdd.put(pos, array);
            if (!bl) {
                this.field_25621.add(pos);
            }
        } else {
            this.lightArraysToAdd.remove(pos);
        }
    }

    protected void updateSectionStatus(long pos, boolean empty) {
        boolean bl = this.nonEmptySections.contains(pos);
        if (!bl && !empty) {
            this.field_15804.add(pos);
            this.updateLevel(Long.MAX_VALUE, pos, 0, true);
        }
        if (bl && empty) {
            this.field_15797.add(pos);
            this.updateLevel(Long.MAX_VALUE, pos, 2, false);
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

