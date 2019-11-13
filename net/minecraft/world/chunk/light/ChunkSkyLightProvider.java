/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk.light;

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
import org.apache.commons.lang3.mutable.MutableInt;

public final class ChunkSkyLightProvider
extends ChunkLightProvider<SkyLightStorage.Data, SkyLightStorage> {
    private static final Direction[] DIRECTIONS = Direction.values();
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
        MutableInt mutableInt = new MutableInt();
        BlockState blockState = this.getStateForLighting(m, mutableInt);
        if (mutableInt.getValue() >= 15) {
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
        BlockState blockState2 = this.getStateForLighting(l, null);
        if (direction != null) {
            VoxelShape voxelShape2;
            voxelShape = this.getOpaqueShape(blockState2, l, direction);
            if (VoxelShapes.unionCoversFullCube(voxelShape, voxelShape2 = this.getOpaqueShape(blockState, m, direction.getOpposite()))) {
                return 15;
            }
        } else {
            voxelShape = this.getOpaqueShape(blockState2, l, Direction.DOWN);
            if (VoxelShapes.unionCoversFullCube(voxelShape, VoxelShapes.empty())) {
                return 15;
            }
            int u = bl ? -1 : 0;
            Direction direction2 = Direction.fromVector(r, u, t);
            if (direction2 == null) {
                return 15;
            }
            VoxelShape voxelShape3 = this.getOpaqueShape(blockState, m, direction2.getOpposite());
            if (VoxelShapes.unionCoversFullCube(VoxelShapes.empty(), voxelShape3)) {
                return 15;
            }
        }
        boolean bl3 = bl2 = l == Long.MAX_VALUE || bl && k > p;
        if (bl2 && i == 0 && mutableInt.getValue() == 0) {
            return 0;
        }
        return i + Math.max(1, mutableInt.getValue());
    }

    @Override
    protected void propagateLevel(long l, int i, boolean bl) {
        long s;
        long t;
        int o;
        long m = ChunkSectionPos.fromGlobalPos(l);
        int j = BlockPos.unpackLongY(l);
        int k = ChunkSectionPos.getLocalCoord(j);
        int n = ChunkSectionPos.getSectionCoord(j);
        if (k != 0) {
            o = 0;
        } else {
            int p = 0;
            while (!((SkyLightStorage)this.lightStorage).hasLight(ChunkSectionPos.offset(m, 0, -p - 1, 0)) && ((SkyLightStorage)this.lightStorage).isAboveMinimumHeight(n - p - 1)) {
                ++p;
            }
            o = p;
        }
        long q = BlockPos.add(l, 0, -1 - o * 16, 0);
        long r = ChunkSectionPos.fromGlobalPos(q);
        if (m == r || ((SkyLightStorage)this.lightStorage).hasLight(r)) {
            this.propagateLevel(l, q, i, bl);
        }
        if (m == (t = ChunkSectionPos.fromGlobalPos(s = BlockPos.offset(l, Direction.UP))) || ((SkyLightStorage)this.lightStorage).hasLight(t)) {
            this.propagateLevel(l, s, i, bl);
        }
        block1: for (Direction direction : HORIZONTAL_DIRECTIONS) {
            int u = 0;
            do {
                long v;
                long w;
                if (m == (w = ChunkSectionPos.fromGlobalPos(v = BlockPos.add(l, direction.getOffsetX(), -u, direction.getOffsetZ())))) {
                    this.propagateLevel(l, v, i, bl);
                    continue block1;
                }
                if (!((SkyLightStorage)this.lightStorage).hasLight(w)) continue;
                this.propagateLevel(l, v, i, bl);
            } while (++u <= o * 16);
        }
    }

    @Override
    protected int recalculateLevel(long l, long m, int i) {
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
        long n = ChunkSectionPos.fromGlobalPos(l);
        ChunkNibbleArray chunkNibbleArray = ((SkyLightStorage)this.lightStorage).getLightArray(n, true);
        for (Direction direction : DIRECTIONS) {
            int r;
            long o = BlockPos.offset(l, direction);
            long p = ChunkSectionPos.fromGlobalPos(o);
            ChunkNibbleArray chunkNibbleArray2 = n == p ? chunkNibbleArray : ((SkyLightStorage)this.lightStorage).getLightArray(p, true);
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
            while (!((SkyLightStorage)this.lightStorage).hasLight(p) && !((SkyLightStorage)this.lightStorage).isAboveTopmostLightArray(p)) {
                p = ChunkSectionPos.offset(p, Direction.UP);
                o = BlockPos.add(o, 0, 16, 0);
            }
            ChunkNibbleArray chunkNibbleArray3 = ((SkyLightStorage)this.lightStorage).getLightArray(p, true);
            if (o == m) continue;
            if (chunkNibbleArray3 != null) {
                r = this.getPropagatedLevel(o, l, this.getCurrentLevelFromArray(chunkNibbleArray3, o));
            } else {
                int n2 = r = ((SkyLightStorage)this.lightStorage).isLightEnabled(p) ? 0 : 15;
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
    protected void resetLevel(long l) {
        ((SkyLightStorage)this.lightStorage).updateAll();
        long m = ChunkSectionPos.fromGlobalPos(l);
        if (((SkyLightStorage)this.lightStorage).hasLight(m)) {
            super.resetLevel(l);
        } else {
            l = BlockPos.removeChunkSectionLocalY(l);
            while (!((SkyLightStorage)this.lightStorage).hasLight(m) && !((SkyLightStorage)this.lightStorage).isAboveTopmostLightArray(m)) {
                m = ChunkSectionPos.offset(m, Direction.UP);
                l = BlockPos.add(l, 0, 16, 0);
            }
            if (((SkyLightStorage)this.lightStorage).hasLight(m)) {
                super.resetLevel(l);
            }
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public String method_22875(long l) {
        return super.method_22875(l) + (((SkyLightStorage)this.lightStorage).isAboveTopmostLightArray(l) ? "*" : "");
    }
}

