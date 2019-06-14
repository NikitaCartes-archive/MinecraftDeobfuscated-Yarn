/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk.light;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Arrays;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ColumnChunkNibbleArray;
import net.minecraft.world.chunk.WorldNibbleStorage;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.chunk.light.LightStorage;

public class SkyLightStorage
extends LightStorage<Data> {
    private static final Direction[] DIRECTIONS_SKYLIGHT = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
    private final LongSet field_15820 = new LongOpenHashSet();
    private final LongSet field_15815 = new LongOpenHashSet();
    private final LongSet field_15816 = new LongOpenHashSet();
    private final LongSet field_15817 = new LongOpenHashSet();
    private volatile boolean hasSkyLightUpdates;

    protected SkyLightStorage(ChunkProvider chunkProvider) {
        super(LightType.SKY, chunkProvider, new Data(new Long2ObjectOpenHashMap<ChunkNibbleArray>(), new Long2IntOpenHashMap(), Integer.MAX_VALUE));
    }

    @Override
    protected int getLight(long l) {
        long m = ChunkSectionPos.toChunkLong(l);
        int i = ChunkSectionPos.unpackLongY(m);
        Data data = (Data)this.dataStorageUncached;
        int j = data.heightMap.get(ChunkSectionPos.toLightStorageIndex(m));
        if (j == data.defaultHeight || i >= j) {
            return 15;
        }
        ChunkNibbleArray chunkNibbleArray = this.getDataForChunk(data, m);
        if (chunkNibbleArray == null) {
            l = BlockPos.removeChunkSectionLocalY(l);
            while (chunkNibbleArray == null) {
                m = ChunkSectionPos.offsetPacked(m, Direction.UP);
                if (++i >= j) {
                    return 15;
                }
                l = BlockPos.add(l, 0, 16, 0);
                chunkNibbleArray = this.getDataForChunk(data, m);
            }
        }
        return chunkNibbleArray.get(ChunkSectionPos.toLocalCoord(BlockPos.unpackLongX(l)), ChunkSectionPos.toLocalCoord(BlockPos.unpackLongY(l)), ChunkSectionPos.toLocalCoord(BlockPos.unpackLongZ(l)));
    }

    @Override
    protected void method_15523(long l) {
        int i = ChunkSectionPos.unpackLongY(l);
        if (((Data)this.dataStorage).defaultHeight > i) {
            ((Data)this.dataStorage).defaultHeight = i;
            ((Data)this.dataStorage).heightMap.defaultReturnValue(((Data)this.dataStorage).defaultHeight);
        }
        long m = ChunkSectionPos.toLightStorageIndex(l);
        int j = ((Data)this.dataStorage).heightMap.get(m);
        if (j < i + 1) {
            ((Data)this.dataStorage).heightMap.put(m, i + 1);
            if (this.field_15817.contains(m)) {
                this.method_20810(l);
                if (j > ((Data)this.dataStorage).defaultHeight) {
                    long n = ChunkSectionPos.asLong(ChunkSectionPos.unpackLongX(l), j - 1, ChunkSectionPos.unpackLongZ(l));
                    this.method_20809(n);
                }
                this.checkForUpdates();
            }
        }
    }

    private void method_20809(long l) {
        this.field_15816.add(l);
        this.field_15815.remove(l);
    }

    private void method_20810(long l) {
        this.field_15815.add(l);
        this.field_15816.remove(l);
    }

    private void checkForUpdates() {
        this.hasSkyLightUpdates = !this.field_15815.isEmpty() || !this.field_15816.isEmpty();
    }

    @Override
    protected void onChunkRemoved(long l) {
        long m = ChunkSectionPos.toLightStorageIndex(l);
        boolean bl = this.field_15817.contains(m);
        if (bl) {
            this.method_20809(l);
        }
        int i = ChunkSectionPos.unpackLongY(l);
        if (((Data)this.dataStorage).heightMap.get(m) == i + 1) {
            long n = l;
            while (!this.hasChunk(n) && this.isAboveMinimumHeight(i)) {
                --i;
                n = ChunkSectionPos.offsetPacked(n, Direction.DOWN);
            }
            if (this.hasChunk(n)) {
                ((Data)this.dataStorage).heightMap.put(m, i + 1);
                if (bl) {
                    this.method_20810(n);
                }
            } else {
                ((Data)this.dataStorage).heightMap.remove(m);
            }
        }
        if (bl) {
            this.checkForUpdates();
        }
    }

    @Override
    protected void method_15535(long l, boolean bl) {
        if (bl && this.field_15817.add(l)) {
            int i = ((Data)this.dataStorage).heightMap.get(l);
            if (i != ((Data)this.dataStorage).defaultHeight) {
                long m = ChunkSectionPos.asLong(ChunkSectionPos.unpackLongX(l), i - 1, ChunkSectionPos.unpackLongZ(l));
                this.method_20810(m);
                this.checkForUpdates();
            }
        } else if (!bl) {
            this.field_15817.remove(l);
        }
    }

    @Override
    protected boolean hasLightUpdates() {
        return super.hasLightUpdates() || this.hasSkyLightUpdates;
    }

    @Override
    protected ChunkNibbleArray getDataForChunk(long l) {
        ChunkNibbleArray chunkNibbleArray2;
        ChunkNibbleArray chunkNibbleArray = (ChunkNibbleArray)this.toUpdate.get(l);
        if (chunkNibbleArray != null) {
            return chunkNibbleArray;
        }
        long m = ChunkSectionPos.offsetPacked(l, Direction.UP);
        int i = ((Data)this.dataStorage).heightMap.get(ChunkSectionPos.toLightStorageIndex(l));
        if (i == ((Data)this.dataStorage).defaultHeight || ChunkSectionPos.unpackLongY(m) >= i) {
            return new ChunkNibbleArray();
        }
        while ((chunkNibbleArray2 = this.getDataForChunk(m, true)) == null) {
            m = ChunkSectionPos.offsetPacked(m, Direction.UP);
        }
        return new ChunkNibbleArray(new ColumnChunkNibbleArray(chunkNibbleArray2, 0).asByteArray());
    }

    @Override
    protected void processUpdates(ChunkLightProvider<Data, ?> chunkLightProvider, boolean bl, boolean bl2) {
        int j;
        int i;
        long l;
        LongIterator longIterator;
        super.processUpdates(chunkLightProvider, bl, bl2);
        if (!bl) {
            return;
        }
        if (!this.field_15815.isEmpty()) {
            longIterator = this.field_15815.iterator();
            while (longIterator.hasNext()) {
                int k;
                l = (Long)longIterator.next();
                i = this.getLevel(l);
                if (i == 2 || this.field_15816.contains(l) || !this.field_15820.add(l)) continue;
                if (i == 1) {
                    long n;
                    this.removeChunkData(chunkLightProvider, l);
                    if (this.field_15802.add(l)) {
                        ((Data)this.dataStorage).cloneChunkData(l);
                    }
                    Arrays.fill(this.getDataForChunk(l, true).asByteArray(), (byte)-1);
                    j = ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongX(l));
                    k = ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongY(l));
                    int m = ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongZ(l));
                    for (Direction direction : DIRECTIONS_SKYLIGHT) {
                        n = ChunkSectionPos.offsetPacked(l, direction);
                        if (!this.field_15816.contains(n) && (this.field_15820.contains(n) || this.field_15815.contains(n)) || !this.hasChunk(n)) continue;
                        for (int o = 0; o < 16; ++o) {
                            for (int p = 0; p < 16; ++p) {
                                long r;
                                long q;
                                switch (direction) {
                                    case NORTH: {
                                        q = BlockPos.asLong(j + o, k + p, m);
                                        r = BlockPos.asLong(j + o, k + p, m - 1);
                                        break;
                                    }
                                    case SOUTH: {
                                        q = BlockPos.asLong(j + o, k + p, m + 16 - 1);
                                        r = BlockPos.asLong(j + o, k + p, m + 16);
                                        break;
                                    }
                                    case WEST: {
                                        q = BlockPos.asLong(j, k + o, m + p);
                                        r = BlockPos.asLong(j - 1, k + o, m + p);
                                        break;
                                    }
                                    default: {
                                        q = BlockPos.asLong(j + 16 - 1, k + o, m + p);
                                        r = BlockPos.asLong(j + 16, k + o, m + p);
                                    }
                                }
                                chunkLightProvider.update(q, r, chunkLightProvider.getPropagatedLevel(q, r, 0), true);
                            }
                        }
                    }
                    for (int s = 0; s < 16; ++s) {
                        for (int t = 0; t < 16; ++t) {
                            long u = BlockPos.asLong(ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongX(l)) + s, ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongY(l)), ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongZ(l)) + t);
                            n = BlockPos.asLong(ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongX(l)) + s, ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongY(l)) - 1, ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongZ(l)) + t);
                            chunkLightProvider.update(u, n, chunkLightProvider.getPropagatedLevel(u, n, 0), true);
                        }
                    }
                    continue;
                }
                for (j = 0; j < 16; ++j) {
                    for (k = 0; k < 16; ++k) {
                        long v = BlockPos.asLong(ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongX(l)) + j, ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongY(l)) + 16 - 1, ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongZ(l)) + k);
                        chunkLightProvider.update(Long.MAX_VALUE, v, 0, true);
                    }
                }
            }
        }
        this.field_15815.clear();
        if (!this.field_15816.isEmpty()) {
            longIterator = this.field_15816.iterator();
            while (longIterator.hasNext()) {
                l = (Long)longIterator.next();
                if (!this.field_15820.remove(l) || !this.hasChunk(l)) continue;
                for (i = 0; i < 16; ++i) {
                    for (j = 0; j < 16; ++j) {
                        long w = BlockPos.asLong(ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongX(l)) + i, ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongY(l)) + 16 - 1, ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongZ(l)) + j);
                        chunkLightProvider.update(Long.MAX_VALUE, w, 15, false);
                    }
                }
            }
        }
        this.field_15816.clear();
        this.hasSkyLightUpdates = false;
    }

    protected boolean isAboveMinimumHeight(int i) {
        return i >= ((Data)this.dataStorage).defaultHeight;
    }

    protected boolean method_15565(long l) {
        int i = BlockPos.unpackLongY(l);
        if ((i & 0xF) != 15) {
            return false;
        }
        long m = ChunkSectionPos.toChunkLong(l);
        long n = ChunkSectionPos.toLightStorageIndex(m);
        if (!this.field_15817.contains(n)) {
            return false;
        }
        int j = ((Data)this.dataStorage).heightMap.get(n);
        return ChunkSectionPos.fromChunkCoord(j) == i + 16;
    }

    protected boolean method_15568(long l) {
        long m = ChunkSectionPos.toLightStorageIndex(l);
        int i = ((Data)this.dataStorage).heightMap.get(m);
        return i == ((Data)this.dataStorage).defaultHeight || ChunkSectionPos.unpackLongY(l) >= i;
    }

    protected boolean method_15566(long l) {
        long m = ChunkSectionPos.toLightStorageIndex(l);
        return this.field_15817.contains(m);
    }

    public static final class Data
    extends WorldNibbleStorage<Data> {
        private int defaultHeight;
        private final Long2IntOpenHashMap heightMap;

        public Data(Long2ObjectOpenHashMap<ChunkNibbleArray> long2ObjectOpenHashMap, Long2IntOpenHashMap long2IntOpenHashMap, int i) {
            super(long2ObjectOpenHashMap);
            this.heightMap = long2IntOpenHashMap;
            long2IntOpenHashMap.defaultReturnValue(i);
            this.defaultHeight = i;
        }

        public Data method_15572() {
            return new Data((Long2ObjectOpenHashMap<ChunkNibbleArray>)this.arraysByChunk.clone(), this.heightMap.clone(), this.defaultHeight);
        }

        @Override
        public /* synthetic */ WorldNibbleStorage copy() {
            return this.method_15572();
        }
    }
}

