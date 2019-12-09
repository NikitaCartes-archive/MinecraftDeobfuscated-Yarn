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
    protected int getPropagatedLevel(long sourceId, long targetId, int level) {
        boolean bl2;
        VoxelShape voxelShape;
        if (targetId == Long.MAX_VALUE) {
            return 15;
        }
        if (sourceId == Long.MAX_VALUE) {
            if (((SkyLightStorage)this.lightStorage).method_15565(targetId)) {
                level = 0;
            } else {
                return 15;
            }
        }
        if (level >= 15) {
            return level;
        }
        MutableInt mutableInt = new MutableInt();
        BlockState blockState = this.getStateForLighting(targetId, mutableInt);
        if (mutableInt.getValue() >= 15) {
            return 15;
        }
        int i = BlockPos.unpackLongX(sourceId);
        int j = BlockPos.unpackLongY(sourceId);
        int k = BlockPos.unpackLongZ(sourceId);
        int l = BlockPos.unpackLongX(targetId);
        int m = BlockPos.unpackLongY(targetId);
        int n = BlockPos.unpackLongZ(targetId);
        boolean bl = i == l && k == n;
        int o = Integer.signum(l - i);
        int p = Integer.signum(m - j);
        int q = Integer.signum(n - k);
        Direction direction = sourceId == Long.MAX_VALUE ? Direction.DOWN : Direction.fromVector(o, p, q);
        BlockState blockState2 = this.getStateForLighting(sourceId, null);
        if (direction != null) {
            VoxelShape voxelShape2;
            voxelShape = this.getOpaqueShape(blockState2, sourceId, direction);
            if (VoxelShapes.unionCoversFullCube(voxelShape, voxelShape2 = this.getOpaqueShape(blockState, targetId, direction.getOpposite()))) {
                return 15;
            }
        } else {
            voxelShape = this.getOpaqueShape(blockState2, sourceId, Direction.DOWN);
            if (VoxelShapes.unionCoversFullCube(voxelShape, VoxelShapes.empty())) {
                return 15;
            }
            int r = bl ? -1 : 0;
            Direction direction2 = Direction.fromVector(o, r, q);
            if (direction2 == null) {
                return 15;
            }
            VoxelShape voxelShape3 = this.getOpaqueShape(blockState, targetId, direction2.getOpposite());
            if (VoxelShapes.unionCoversFullCube(VoxelShapes.empty(), voxelShape3)) {
                return 15;
            }
        }
        boolean bl3 = bl2 = sourceId == Long.MAX_VALUE || bl && j > m;
        if (bl2 && level == 0 && mutableInt.getValue() == 0) {
            return 0;
        }
        return level + Math.max(1, mutableInt.getValue());
    }

    @Override
    protected void propagateLevel(long id, int level, boolean decrease) {
        long q;
        long r;
        int m;
        long l = ChunkSectionPos.fromGlobalPos(id);
        int i = BlockPos.unpackLongY(id);
        int j = ChunkSectionPos.getLocalCoord(i);
        int k = ChunkSectionPos.getSectionCoord(i);
        if (j != 0) {
            m = 0;
        } else {
            int n = 0;
            while (!((SkyLightStorage)this.lightStorage).hasLight(ChunkSectionPos.offset(l, 0, -n - 1, 0)) && ((SkyLightStorage)this.lightStorage).isAboveMinimumHeight(k - n - 1)) {
                ++n;
            }
            m = n;
        }
        long o = BlockPos.add(id, 0, -1 - m * 16, 0);
        long p = ChunkSectionPos.fromGlobalPos(o);
        if (l == p || ((SkyLightStorage)this.lightStorage).hasLight(p)) {
            this.propagateLevel(id, o, level, decrease);
        }
        if (l == (r = ChunkSectionPos.fromGlobalPos(q = BlockPos.offset(id, Direction.UP))) || ((SkyLightStorage)this.lightStorage).hasLight(r)) {
            this.propagateLevel(id, q, level, decrease);
        }
        block1: for (Direction direction : HORIZONTAL_DIRECTIONS) {
            int s = 0;
            do {
                long t;
                long u;
                if (l == (u = ChunkSectionPos.fromGlobalPos(t = BlockPos.add(id, direction.getOffsetX(), -s, direction.getOffsetZ())))) {
                    this.propagateLevel(id, t, level, decrease);
                    continue block1;
                }
                if (!((SkyLightStorage)this.lightStorage).hasLight(u)) continue;
                this.propagateLevel(id, t, level, decrease);
            } while (++s <= m * 16);
        }
    }

    @Override
    protected int recalculateLevel(long id, long excludedId, int maxLevel) {
        int i = maxLevel;
        if (Long.MAX_VALUE != excludedId) {
            int j = this.getPropagatedLevel(Long.MAX_VALUE, id, 0);
            if (i > j) {
                i = j;
            }
            if (i == 0) {
                return i;
            }
        }
        long l = ChunkSectionPos.fromGlobalPos(id);
        ChunkNibbleArray chunkNibbleArray = ((SkyLightStorage)this.lightStorage).getLightArray(l, true);
        for (Direction direction : DIRECTIONS) {
            int o;
            long m = BlockPos.offset(id, direction);
            long n = ChunkSectionPos.fromGlobalPos(m);
            ChunkNibbleArray chunkNibbleArray2 = l == n ? chunkNibbleArray : ((SkyLightStorage)this.lightStorage).getLightArray(n, true);
            if (chunkNibbleArray2 != null) {
                if (m == excludedId) continue;
                int k = this.getPropagatedLevel(m, id, this.getCurrentLevelFromArray(chunkNibbleArray2, m));
                if (i > k) {
                    i = k;
                }
                if (i != 0) continue;
                return i;
            }
            if (direction == Direction.DOWN) continue;
            m = BlockPos.removeChunkSectionLocalY(m);
            while (!((SkyLightStorage)this.lightStorage).hasLight(n) && !((SkyLightStorage)this.lightStorage).isAboveTopmostLightArray(n)) {
                n = ChunkSectionPos.offset(n, Direction.UP);
                m = BlockPos.add(m, 0, 16, 0);
            }
            ChunkNibbleArray chunkNibbleArray3 = ((SkyLightStorage)this.lightStorage).getLightArray(n, true);
            if (m == excludedId) continue;
            if (chunkNibbleArray3 != null) {
                o = this.getPropagatedLevel(m, id, this.getCurrentLevelFromArray(chunkNibbleArray3, m));
            } else {
                int n2 = o = ((SkyLightStorage)this.lightStorage).isLightEnabled(n) ? 0 : 15;
            }
            if (i > o) {
                i = o;
            }
            if (i != 0) continue;
            return i;
        }
        return i;
    }

    @Override
    protected void resetLevel(long id) {
        ((SkyLightStorage)this.lightStorage).updateAll();
        long l = ChunkSectionPos.fromGlobalPos(id);
        if (((SkyLightStorage)this.lightStorage).hasLight(l)) {
            super.resetLevel(id);
        } else {
            id = BlockPos.removeChunkSectionLocalY(id);
            while (!((SkyLightStorage)this.lightStorage).hasLight(l) && !((SkyLightStorage)this.lightStorage).isAboveTopmostLightArray(l)) {
                l = ChunkSectionPos.offset(l, Direction.UP);
                id = BlockPos.add(id, 0, 16, 0);
            }
            if (((SkyLightStorage)this.lightStorage).hasLight(l)) {
                super.resetLevel(id);
            }
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public String method_22875(long l) {
        return super.method_22875(l) + (((SkyLightStorage)this.lightStorage).isAboveTopmostLightArray(l) ? "*" : "");
    }
}

