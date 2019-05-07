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
import net.minecraft.util.SectionRelativeLevelPropagator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.WorldNibbleStorage;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import org.jetbrains.annotations.Nullable;

public abstract class LightStorage<M extends WorldNibbleStorage<M>>
extends SectionRelativeLevelPropagator {
    protected static final ChunkNibbleArray EMPTY = new ChunkNibbleArray();
    private static final Direction[] DIRECTIONS = Direction.values();
    private final LightType lightType;
    private final ChunkProvider chunkProvider;
    protected final LongSet field_15808 = new LongOpenHashSet();
    protected final LongSet field_15797 = new LongOpenHashSet();
    protected final LongSet field_15804 = new LongOpenHashSet();
    protected volatile M dataStorageUncached;
    protected final M dataStorage;
    protected final LongSet field_15802 = new LongOpenHashSet();
    protected final LongSet toNotify = new LongOpenHashSet();
    protected final Long2ObjectMap<ChunkNibbleArray> toUpdate = new Long2ObjectOpenHashMap<ChunkNibbleArray>();
    private final LongSet toRemove = new LongOpenHashSet();
    protected volatile boolean hasLightUpdates;

    protected LightStorage(LightType lightType, ChunkProvider chunkProvider, M worldNibbleStorage) {
        super(3, 16, 256);
        this.lightType = lightType;
        this.chunkProvider = chunkProvider;
        this.dataStorage = worldNibbleStorage;
        this.dataStorageUncached = ((WorldNibbleStorage)worldNibbleStorage).copy();
        ((WorldNibbleStorage)this.dataStorageUncached).disableCache();
    }

    protected boolean hasChunk(long l) {
        return this.getDataForChunk(l, true) != null;
    }

    @Nullable
    protected ChunkNibbleArray getDataForChunk(long l, boolean bl) {
        return this.getDataForChunk(bl ? this.dataStorage : this.dataStorageUncached, l);
    }

    @Nullable
    protected ChunkNibbleArray getDataForChunk(M worldNibbleStorage, long l) {
        return ((WorldNibbleStorage)worldNibbleStorage).getDataForChunk(l);
    }

    @Nullable
    public ChunkNibbleArray method_20533(long l) {
        ChunkNibbleArray chunkNibbleArray = (ChunkNibbleArray)this.toUpdate.get(l);
        if (chunkNibbleArray != null) {
            return chunkNibbleArray;
        }
        return this.getDataForChunk(l, false);
    }

    protected abstract int getLight(long var1);

    protected int get(long l) {
        long m = ChunkSectionPos.toChunkLong(l);
        ChunkNibbleArray chunkNibbleArray = this.getDataForChunk(m, true);
        return chunkNibbleArray.get(ChunkSectionPos.toLocalCoord(BlockPos.unpackLongX(l)), ChunkSectionPos.toLocalCoord(BlockPos.unpackLongY(l)), ChunkSectionPos.toLocalCoord(BlockPos.unpackLongZ(l)));
    }

    protected void set(long l, int i) {
        long m = ChunkSectionPos.toChunkLong(l);
        if (this.field_15802.add(m)) {
            ((WorldNibbleStorage)this.dataStorage).cloneChunkData(m);
        }
        ChunkNibbleArray chunkNibbleArray = this.getDataForChunk(m, true);
        chunkNibbleArray.set(ChunkSectionPos.toLocalCoord(BlockPos.unpackLongX(l)), ChunkSectionPos.toLocalCoord(BlockPos.unpackLongY(l)), ChunkSectionPos.toLocalCoord(BlockPos.unpackLongZ(l)), i);
        for (int j = -1; j <= 1; ++j) {
            for (int k = -1; k <= 1; ++k) {
                for (int n = -1; n <= 1; ++n) {
                    this.toNotify.add(ChunkSectionPos.toChunkLong(BlockPos.add(l, k, n, j)));
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
        if (!this.toRemove.contains(l) && ((WorldNibbleStorage)this.dataStorage).hasChunk(l)) {
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
            if (this.toRemove.contains(l)) {
                this.toRemove.remove(l);
            } else {
                ((WorldNibbleStorage)this.dataStorage).addForChunk(l, this.getDataForChunk(l));
                this.field_15802.add(l);
                this.method_15523(l);
                for (int k = -1; k <= 1; ++k) {
                    for (int m = -1; m <= 1; ++m) {
                        for (int n = -1; n <= 1; ++n) {
                            this.toNotify.add(ChunkSectionPos.toChunkLong(BlockPos.add(l, m, n, k)));
                        }
                    }
                }
            }
        }
        if (j != 2 && i >= 2) {
            this.toRemove.add(l);
        }
        this.hasLightUpdates = !this.toRemove.isEmpty();
    }

    protected ChunkNibbleArray getDataForChunk(long l) {
        ChunkNibbleArray chunkNibbleArray = (ChunkNibbleArray)this.toUpdate.get(l);
        if (chunkNibbleArray != null) {
            return chunkNibbleArray;
        }
        return new ChunkNibbleArray();
    }

    protected void removeChunkData(ChunkLightProvider<?, ?> chunkLightProvider, long l) {
        int i = ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongX(l));
        int j = ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongY(l));
        int k = ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongZ(l));
        for (int m = 0; m < 16; ++m) {
            for (int n = 0; n < 16; ++n) {
                for (int o = 0; o < 16; ++o) {
                    long p = BlockPos.asLong(i + m, j + n, k + o);
                    chunkLightProvider.remove(p);
                }
            }
        }
    }

    protected boolean hasLightUpdates() {
        return this.hasLightUpdates;
    }

    protected void processUpdates(ChunkLightProvider<M, ?> chunkLightProvider, boolean bl, boolean bl2) {
        long m;
        long l;
        if (!this.hasLightUpdates() && this.toUpdate.isEmpty()) {
            return;
        }
        Iterator<Long> iterator = this.toRemove.iterator();
        while (iterator.hasNext()) {
            l = (Long)iterator.next();
            this.toUpdate.remove(l);
            this.removeChunkData(chunkLightProvider, l);
            ((WorldNibbleStorage)this.dataStorage).removeChunk(l);
        }
        ((WorldNibbleStorage)this.dataStorage).clearCache();
        iterator = this.toRemove.iterator();
        while (iterator.hasNext()) {
            l = (Long)iterator.next();
            this.onChunkRemoved(l);
        }
        this.toRemove.clear();
        this.hasLightUpdates = false;
        for (Long2ObjectMap.Entry entry : this.toUpdate.long2ObjectEntrySet()) {
            m = entry.getLongKey();
            if (!this.hasChunk(m)) continue;
            ChunkNibbleArray chunkNibbleArray = (ChunkNibbleArray)entry.getValue();
            if (((WorldNibbleStorage)this.dataStorage).getDataForChunk(m) == chunkNibbleArray) continue;
            this.removeChunkData(chunkLightProvider, m);
            ((WorldNibbleStorage)this.dataStorage).addForChunk(m, chunkNibbleArray);
            this.field_15802.add(m);
        }
        ((WorldNibbleStorage)this.dataStorage).clearCache();
        if (!bl2) {
            for (long l2 : this.toUpdate.keySet()) {
                if (!this.hasChunk(l2)) continue;
                int i = ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongX(l2));
                int j = ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongY(l2));
                int k = ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongZ(l2));
                for (Direction direction : DIRECTIONS) {
                    long n = ChunkSectionPos.offsetPacked(l2, direction);
                    if (this.toUpdate.containsKey(n) || !this.hasChunk(n)) continue;
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
                            chunkLightProvider.update(q, r, chunkLightProvider.getPropagatedLevel(q, r, chunkLightProvider.getLevel(q)), false);
                            chunkLightProvider.update(r, q, chunkLightProvider.getPropagatedLevel(r, q, chunkLightProvider.getLevel(r)), false);
                        }
                    }
                }
            }
        }
        Iterator objectIterator = this.toUpdate.long2ObjectEntrySet().iterator();
        while (objectIterator.hasNext()) {
            Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)objectIterator.next();
            m = entry.getLongKey();
            if (!this.hasChunk(m)) continue;
            objectIterator.remove();
        }
    }

    protected void method_15523(long l) {
    }

    protected void onChunkRemoved(long l) {
    }

    protected void method_15535(long l, boolean bl) {
    }

    protected void scheduleToUpdate(long l, ChunkNibbleArray chunkNibbleArray) {
        this.toUpdate.put(l, chunkNibbleArray);
    }

    protected void scheduleChunkLightUpdate(long l, boolean bl) {
        boolean bl2 = this.field_15808.contains(l);
        if (!bl2 && !bl) {
            this.field_15804.add(l);
            this.update(Long.MAX_VALUE, l, 0, true);
        }
        if (bl2 && bl) {
            this.field_15797.add(l);
            this.update(Long.MAX_VALUE, l, 2, false);
        }
    }

    protected void updateAll() {
        if (this.hasLevelUpdates()) {
            this.updateAllRecursively(Integer.MAX_VALUE);
        }
    }

    protected void notifyChunkProvider() {
        if (!this.field_15802.isEmpty()) {
            Object worldNibbleStorage = ((WorldNibbleStorage)this.dataStorage).copy();
            ((WorldNibbleStorage)worldNibbleStorage).disableCache();
            this.dataStorageUncached = worldNibbleStorage;
            this.field_15802.clear();
        }
        if (!this.toNotify.isEmpty()) {
            LongIterator longIterator = this.toNotify.iterator();
            while (longIterator.hasNext()) {
                long l = longIterator.nextLong();
                this.chunkProvider.onLightUpdate(this.lightType, ChunkSectionPos.from(l));
            }
            this.toNotify.clear();
        }
    }
}

