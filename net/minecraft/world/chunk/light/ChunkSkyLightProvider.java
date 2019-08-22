/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk.light;

import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.chunk.light.SkyLightStorage;

public final class ChunkSkyLightProvider
extends ChunkLightProvider<SkyLightStorage.Data, SkyLightStorage> {
    private static final Direction[] DIRECTIONS_SKYLIGHT = Direction.values();
    private static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

    public ChunkSkyLightProvider(ChunkProvider chunkProvider) {
        super(chunkProvider, LightType.SKY, new SkyLightStorage(chunkProvider));
    }

    @Override
    protected int getPropagatedLevel(long l, long m, int i) {
        boolean bl2;
        VoxelShape voxelShape;
        if (m == Long.MAX_VALUE) {
            return 15;
        }
        if (l == Long.MAX_VALUE) {
            if (((SkyLightStorage)this.lightStorage).method_15565(m)) {
                i = 0;
            } else {
                return 15;
            }
        }
        if (i >= 15) {
            return i;
        }
        AtomicInteger atomicInteger = new AtomicInteger();
        BlockState blockState = this.method_20479(m, atomicInteger);
        if (atomicInteger.get() >= 15) {
            return 15;
        }
        int j = BlockPos.unpackLongX(l);
        int k = BlockPos.unpackLongY(l);
        int n = BlockPos.unpackLongZ(l);
        int o = BlockPos.unpackLongX(m);
        int p = BlockPos.unpackLongY(m);
        int q = BlockPos.unpackLongZ(m);
        boolean bl = j == o && n == q;
        int r = Integer.signum(o - j);
        int s = Integer.signum(p - k);
        int t = Integer.signum(q - n);
        Direction direction = l == Long.MAX_VALUE ? Direction.DOWN : Direction.fromVector(r, s, t);
        BlockState blockState2 = this.method_20479(l, null);
        if (direction != null) {
            VoxelShape voxelShape2;
            voxelShape = this.method_20710(blockState2, l, direction);
            if (VoxelShapes.method_20713(voxelShape, voxelShape2 = this.method_20710(blockState, m, direction.getOpposite()))) {
                return 15;
            }
        } else {
            voxelShape = this.method_20710(blockState2, l, Direction.DOWN);
            if (VoxelShapes.method_20713(voxelShape, VoxelShapes.empty())) {
                return 15;
            }
            int u = bl ? -1 : 0;
            Direction direction2 = Direction.fromVector(r, u, t);
            if (direction2 == null) {
                return 15;
            }
            VoxelShape voxelShape3 = this.method_20710(blockState, m, direction2.getOpposite());
            if (VoxelShapes.method_20713(VoxelShapes.empty(), voxelShape3)) {
                return 15;
            }
        }
        boolean bl3 = bl2 = l == Long.MAX_VALUE || bl && k > p;
        if (bl2 && i == 0 && atomicInteger.get() == 0) {
            return 0;
        }
        return i + Math.max(1, atomicInteger.get());
    }

    @Override
    protected void updateNeighborsRecursively(long l, int i, boolean bl) {
        long s;
        long t;
        int o;
        long m = ChunkSectionPos.toChunkLong(l);
        int j = BlockPos.unpackLongY(l);
        int k = ChunkSectionPos.toLocalCoord(j);
        int n = ChunkSectionPos.toChunkCoord(j);
        if (k != 0) {
            o = 0;
        } else {
            int p = 0;
            while (!((SkyLightStorage)this.lightStorage).hasChunk(ChunkSectionPos.offsetPacked(m, 0, -p - 1, 0)) && ((SkyLightStorage)this.lightStorage).isAboveMinimumHeight(n - p - 1)) {
                ++p;
            }
            o = p;
        }
        long q = BlockPos.add(l, 0, -1 - o * 16, 0);
        long r = ChunkSectionPos.toChunkLong(q);
        if (m == r || ((SkyLightStorage)this.lightStorage).hasChunk(r)) {
            this.updateRecursively(l, q, i, bl);
        }
        if (m == (t = ChunkSectionPos.toChunkLong(s = BlockPos.offset(l, Direction.UP))) || ((SkyLightStorage)this.lightStorage).hasChunk(t)) {
            this.updateRecursively(l, s, i, bl);
        }
        block1: for (Direction direction : HORIZONTAL_DIRECTIONS) {
            int u = 0;
            do {
                long v;
                long w;
                if (m == (w = ChunkSectionPos.toChunkLong(v = BlockPos.add(l, direction.getOffsetX(), -u, direction.getOffsetZ())))) {
                    this.updateRecursively(l, v, i, bl);
                    continue block1;
                }
                if (!((SkyLightStorage)this.lightStorage).hasChunk(w)) continue;
                this.updateRecursively(l, v, i, bl);
            } while (++u <= o * 16);
        }
    }

    @Override
    protected int getMergedLevel(long l, long m, int i) {
        int j = i;
        if (Long.MAX_VALUE != m) {
            int k = this.getPropagatedLevel(Long.MAX_VALUE, l, 0);
            if (j > k) {
                j = k;
            }
            if (j == 0) {
                return j;
            }
        }
        long n = ChunkSectionPos.toChunkLong(l);
        ChunkNibbleArray chunkNibbleArray = ((SkyLightStorage)this.lightStorage).getDataForChunk(n, true);
        for (Direction direction : DIRECTIONS_SKYLIGHT) {
            int r;
            long o = BlockPos.offset(l, direction);
            long p = ChunkSectionPos.toChunkLong(o);
            ChunkNibbleArray chunkNibbleArray2 = n == p ? chunkNibbleArray : ((SkyLightStorage)this.lightStorage).getDataForChunk(p, true);
            if (chunkNibbleArray2 != null) {
                if (o == m) continue;
                int q = this.getPropagatedLevel(o, l, this.getCurrentLevelFromArray(chunkNibbleArray2, o));
                if (j > q) {
                    j = q;
                }
                if (j != 0) continue;
                return j;
            }
            if (direction == Direction.DOWN) continue;
            o = BlockPos.removeChunkSectionLocalY(o);
            while (!((SkyLightStorage)this.lightStorage).hasChunk(p) && !((SkyLightStorage)this.lightStorage).method_15568(p)) {
                p = ChunkSectionPos.offsetPacked(p, Direction.UP);
                o = BlockPos.add(o, 0, 16, 0);
            }
            ChunkNibbleArray chunkNibbleArray3 = ((SkyLightStorage)this.lightStorage).getDataForChunk(p, true);
            if (o == m) continue;
            if (chunkNibbleArray3 != null) {
                r = this.getPropagatedLevel(o, l, this.getCurrentLevelFromArray(chunkNibbleArray3, o));
            } else {
                int n2 = r = ((SkyLightStorage)this.lightStorage).method_15566(p) ? 0 : 15;
            }
            if (j > r) {
                j = r;
            }
            if (j != 0) continue;
            return j;
        }
        return j;
    }

    @Override
    protected void fullyUpdate(long l) {
        ((SkyLightStorage)this.lightStorage).updateAll();
        long m = ChunkSectionPos.toChunkLong(l);
        if (((SkyLightStorage)this.lightStorage).hasChunk(m)) {
            super.fullyUpdate(l);
        } else {
            l = BlockPos.removeChunkSectionLocalY(l);
            while (!((SkyLightStorage)this.lightStorage).hasChunk(m) && !((SkyLightStorage)this.lightStorage).method_15568(m)) {
                m = ChunkSectionPos.offsetPacked(m, Direction.UP);
                l = BlockPos.add(l, 0, 16, 0);
            }
            if (((SkyLightStorage)this.lightStorage).hasChunk(m)) {
                super.fullyUpdate(l);
            }
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public String method_15520(long l) {
        return super.method_15520(l) + (((SkyLightStorage)this.lightStorage).method_15568(l) ? "*" : "");
    }
}

