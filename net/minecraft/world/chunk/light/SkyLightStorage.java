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
import net.minecraft.world.chunk.ChunkToNibbleArrayMap;
import net.minecraft.world.chunk.ColumnChunkNibbleArray;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.chunk.light.LightStorage;

public class SkyLightStorage
extends LightStorage<Data> {
    private static final Direction[] LIGHT_REDUCTION_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
    private final LongSet field_15820 = new LongOpenHashSet();
    private final LongSet pendingSkylightUpdates = new LongOpenHashSet();
    private final LongSet field_15816 = new LongOpenHashSet();
    private final LongSet lightEnabled = new LongOpenHashSet();
    private volatile boolean hasSkyLightUpdates;

    protected SkyLightStorage(ChunkProvider chunkProvider) {
        super(LightType.SKY, chunkProvider, new Data(new Long2ObjectOpenHashMap<ChunkNibbleArray>(), new Long2IntOpenHashMap(), Integer.MAX_VALUE));
    }

    @Override
    protected int getLight(long blockPos) {
        long l = ChunkSectionPos.fromGlobalPos(blockPos);
        int i = ChunkSectionPos.getY(l);
        Data data = (Data)this.uncachedLightArrays;
        int j = data.topArraySectionY.get(ChunkSectionPos.withZeroZ(l));
        if (j == data.defaultTopArraySectionY || i >= j) {
            return 15;
        }
        ChunkNibbleArray chunkNibbleArray = this.getLightArray(data, l);
        if (chunkNibbleArray == null) {
            blockPos = BlockPos.removeChunkSectionLocalY(blockPos);
            while (chunkNibbleArray == null) {
                l = ChunkSectionPos.offset(l, Direction.UP);
                if (++i >= j) {
                    return 15;
                }
                blockPos = BlockPos.add(blockPos, 0, 16, 0);
                chunkNibbleArray = this.getLightArray(data, l);
            }
        }
        return chunkNibbleArray.get(ChunkSectionPos.getLocalCoord(BlockPos.unpackLongX(blockPos)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongY(blockPos)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongZ(blockPos)));
    }

    @Override
    protected void onLightArrayCreated(long blockPos) {
        int i = ChunkSectionPos.getY(blockPos);
        if (((Data)this.lightArrays).defaultTopArraySectionY > i) {
            ((Data)this.lightArrays).defaultTopArraySectionY = i;
            ((Data)this.lightArrays).topArraySectionY.defaultReturnValue(((Data)this.lightArrays).defaultTopArraySectionY);
        }
        long l = ChunkSectionPos.withZeroZ(blockPos);
        int j = ((Data)this.lightArrays).topArraySectionY.get(l);
        if (j < i + 1) {
            ((Data)this.lightArrays).topArraySectionY.put(l, i + 1);
            if (this.lightEnabled.contains(l)) {
                this.method_20810(blockPos);
                if (j > ((Data)this.lightArrays).defaultTopArraySectionY) {
                    long m = ChunkSectionPos.asLong(ChunkSectionPos.getX(blockPos), j - 1, ChunkSectionPos.getZ(blockPos));
                    this.method_20809(m);
                }
                this.checkForUpdates();
            }
        }
    }

    private void method_20809(long l) {
        this.field_15816.add(l);
        this.pendingSkylightUpdates.remove(l);
    }

    private void method_20810(long l) {
        this.pendingSkylightUpdates.add(l);
        this.field_15816.remove(l);
    }

    private void checkForUpdates() {
        this.hasSkyLightUpdates = !this.pendingSkylightUpdates.isEmpty() || !this.field_15816.isEmpty();
    }

    @Override
    protected void onChunkRemoved(long l) {
        long m = ChunkSectionPos.withZeroZ(l);
        boolean bl = this.lightEnabled.contains(m);
        if (bl) {
            this.method_20809(l);
        }
        int i = ChunkSectionPos.getY(l);
        if (((Data)this.lightArrays).topArraySectionY.get(m) == i + 1) {
            long n = l;
            while (!this.hasLight(n) && this.isAboveMinimumHeight(i)) {
                --i;
                n = ChunkSectionPos.offset(n, Direction.DOWN);
            }
            if (this.hasLight(n)) {
                ((Data)this.lightArrays).topArraySectionY.put(m, i + 1);
                if (bl) {
                    this.method_20810(n);
                }
            } else {
                ((Data)this.lightArrays).topArraySectionY.remove(m);
            }
        }
        if (bl) {
            this.checkForUpdates();
        }
    }

    @Override
    protected void setLightEnabled(long l, boolean bl) {
        this.updateAll();
        if (bl && this.lightEnabled.add(l)) {
            int i = ((Data)this.lightArrays).topArraySectionY.get(l);
            if (i != ((Data)this.lightArrays).defaultTopArraySectionY) {
                long m = ChunkSectionPos.asLong(ChunkSectionPos.getX(l), i - 1, ChunkSectionPos.getZ(l));
                this.method_20810(m);
                this.checkForUpdates();
            }
        } else if (!bl) {
            this.lightEnabled.remove(l);
        }
    }

    @Override
    protected boolean hasLightUpdates() {
        return super.hasLightUpdates() || this.hasSkyLightUpdates;
    }

    @Override
    protected ChunkNibbleArray createLightArray(long pos) {
        ChunkNibbleArray chunkNibbleArray2;
        ChunkNibbleArray chunkNibbleArray = (ChunkNibbleArray)this.lightArraysToAdd.get(pos);
        if (chunkNibbleArray != null) {
            return chunkNibbleArray;
        }
        long l = ChunkSectionPos.offset(pos, Direction.UP);
        int i = ((Data)this.lightArrays).topArraySectionY.get(ChunkSectionPos.withZeroZ(pos));
        if (i == ((Data)this.lightArrays).defaultTopArraySectionY || ChunkSectionPos.getY(l) >= i) {
            return new ChunkNibbleArray();
        }
        while ((chunkNibbleArray2 = this.getLightArray(l, true)) == null) {
            l = ChunkSectionPos.offset(l, Direction.UP);
        }
        return new ChunkNibbleArray(new ColumnChunkNibbleArray(chunkNibbleArray2, 0).asByteArray());
    }

    @Override
    protected void updateLightArrays(ChunkLightProvider<Data, ?> lightProvider, boolean doSkylight, boolean skipEdgeLightPropagation) {
        int j;
        int i;
        long l;
        LongIterator longIterator;
        super.updateLightArrays(lightProvider, doSkylight, skipEdgeLightPropagation);
        if (!doSkylight) {
            return;
        }
        if (!this.pendingSkylightUpdates.isEmpty()) {
            longIterator = this.pendingSkylightUpdates.iterator();
            while (longIterator.hasNext()) {
                int k;
                l = (Long)longIterator.next();
                i = this.getLevel(l);
                if (i == 2 || this.field_15816.contains(l) || !this.field_15820.add(l)) continue;
                if (i == 1) {
                    long n;
                    this.removeChunkData(lightProvider, l);
                    if (this.field_15802.add(l)) {
                        ((Data)this.lightArrays).replaceWithCopy(l);
                    }
                    Arrays.fill(this.getLightArray(l, true).asByteArray(), (byte)-1);
                    j = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(l));
                    k = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(l));
                    int m = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(l));
                    for (Direction direction : LIGHT_REDUCTION_DIRECTIONS) {
                        n = ChunkSectionPos.offset(l, direction);
                        if (!this.field_15816.contains(n) && (this.field_15820.contains(n) || this.pendingSkylightUpdates.contains(n)) || !this.hasLight(n)) continue;
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
                                lightProvider.updateLevel(q, r, lightProvider.getPropagatedLevel(q, r, 0), true);
                            }
                        }
                    }
                    for (int s = 0; s < 16; ++s) {
                        for (int t = 0; t < 16; ++t) {
                            long u = BlockPos.asLong(ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(l)) + s, ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(l)), ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(l)) + t);
                            n = BlockPos.asLong(ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(l)) + s, ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(l)) - 1, ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(l)) + t);
                            lightProvider.updateLevel(u, n, lightProvider.getPropagatedLevel(u, n, 0), true);
                        }
                    }
                    continue;
                }
                for (j = 0; j < 16; ++j) {
                    for (k = 0; k < 16; ++k) {
                        long v = BlockPos.asLong(ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(l)) + j, ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(l)) + 16 - 1, ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(l)) + k);
                        lightProvider.updateLevel(Long.MAX_VALUE, v, 0, true);
                    }
                }
            }
        }
        this.pendingSkylightUpdates.clear();
        if (!this.field_15816.isEmpty()) {
            longIterator = this.field_15816.iterator();
            while (longIterator.hasNext()) {
                l = (Long)longIterator.next();
                if (!this.field_15820.remove(l) || !this.hasLight(l)) continue;
                for (i = 0; i < 16; ++i) {
                    for (j = 0; j < 16; ++j) {
                        long w = BlockPos.asLong(ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(l)) + i, ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(l)) + 16 - 1, ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(l)) + j);
                        lightProvider.updateLevel(Long.MAX_VALUE, w, 15, false);
                    }
                }
            }
        }
        this.field_15816.clear();
        this.hasSkyLightUpdates = false;
    }

    protected boolean isAboveMinimumHeight(int blockY) {
        return blockY >= ((Data)this.lightArrays).defaultTopArraySectionY;
    }

    protected boolean method_15565(long l) {
        int i = BlockPos.unpackLongY(l);
        if ((i & 0xF) != 15) {
            return false;
        }
        long m = ChunkSectionPos.fromGlobalPos(l);
        long n = ChunkSectionPos.withZeroZ(m);
        if (!this.lightEnabled.contains(n)) {
            return false;
        }
        int j = ((Data)this.lightArrays).topArraySectionY.get(n);
        return ChunkSectionPos.getWorldCoord(j) == i + 16;
    }

    protected boolean isAboveTopmostLightArray(long pos) {
        long l = ChunkSectionPos.withZeroZ(pos);
        int i = ((Data)this.lightArrays).topArraySectionY.get(l);
        return i == ((Data)this.lightArrays).defaultTopArraySectionY || ChunkSectionPos.getY(pos) >= i;
    }

    protected boolean isLightEnabled(long sectionPos) {
        long l = ChunkSectionPos.withZeroZ(sectionPos);
        return this.lightEnabled.contains(l);
    }

    public static final class Data
    extends ChunkToNibbleArrayMap<Data> {
        private int defaultTopArraySectionY;
        private final Long2IntOpenHashMap topArraySectionY;

        public Data(Long2ObjectOpenHashMap<ChunkNibbleArray> long2ObjectOpenHashMap, Long2IntOpenHashMap long2IntOpenHashMap, int i) {
            super(long2ObjectOpenHashMap);
            this.topArraySectionY = long2IntOpenHashMap;
            long2IntOpenHashMap.defaultReturnValue(i);
            this.defaultTopArraySectionY = i;
        }

        @Override
        public Data copy() {
            return new Data((Long2ObjectOpenHashMap<ChunkNibbleArray>)this.arrays.clone(), this.topArraySectionY.clone(), this.defaultTopArraySectionY);
        }

        @Override
        public /* synthetic */ ChunkToNibbleArrayMap copy() {
            return this.copy();
        }
    }
}

