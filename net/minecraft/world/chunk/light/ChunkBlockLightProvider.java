/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk.light;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.light.BlockLightStorage;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import org.apache.commons.lang3.mutable.MutableInt;

public final class ChunkBlockLightProvider
extends ChunkLightProvider<BlockLightStorage.Data, BlockLightStorage> {
    private static final Direction[] DIRECTIONS = Direction.values();
    private final BlockPos.Mutable mutablePos = new BlockPos.Mutable();

    public ChunkBlockLightProvider(ChunkProvider chunkProvider) {
        super(chunkProvider, LightType.BLOCK, new BlockLightStorage(chunkProvider));
    }

    private int getLightSourceLuminance(long l) {
        int i = BlockPos.unpackLongX(l);
        int j = BlockPos.unpackLongY(l);
        int k = BlockPos.unpackLongZ(l);
        BlockView blockView = this.chunkProvider.getChunk(i >> 4, k >> 4);
        if (blockView != null) {
            return blockView.getLuminance(this.mutablePos.set(i, j, k));
        }
        return 0;
    }

    @Override
    protected int getPropagatedLevel(long l, long m, int i) {
        VoxelShape voxelShape2;
        int n;
        int k;
        if (m == Long.MAX_VALUE) {
            return 15;
        }
        if (l == Long.MAX_VALUE) {
            return i + 15 - this.getLightSourceLuminance(m);
        }
        if (i >= 15) {
            return i;
        }
        int j = Integer.signum(BlockPos.unpackLongX(m) - BlockPos.unpackLongX(l));
        Direction direction = Direction.fromVector(j, k = Integer.signum(BlockPos.unpackLongY(m) - BlockPos.unpackLongY(l)), n = Integer.signum(BlockPos.unpackLongZ(m) - BlockPos.unpackLongZ(l)));
        if (direction == null) {
            return 15;
        }
        MutableInt mutableInt = new MutableInt();
        BlockState blockState = this.getStateForLighting(m, mutableInt);
        if (mutableInt.getValue() >= 15) {
            return 15;
        }
        BlockState blockState2 = this.getStateForLighting(l, null);
        VoxelShape voxelShape = this.getOpaqueShape(blockState2, l, direction);
        if (VoxelShapes.unionCoversFullCube(voxelShape, voxelShape2 = this.getOpaqueShape(blockState, m, direction.getOpposite()))) {
            return 15;
        }
        return i + Math.max(1, mutableInt.getValue());
    }

    @Override
    protected void propagateLevel(long l, int i, boolean bl) {
        long m = ChunkSectionPos.fromGlobalPos(l);
        for (Direction direction : DIRECTIONS) {
            long n = BlockPos.offset(l, direction);
            long o = ChunkSectionPos.fromGlobalPos(n);
            if (m != o && !((BlockLightStorage)this.lightStorage).hasLight(o)) continue;
            this.propagateLevel(l, n, i, bl);
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
        ChunkNibbleArray chunkNibbleArray = ((BlockLightStorage)this.lightStorage).getLightArray(n, true);
        for (Direction direction : DIRECTIONS) {
            long p;
            ChunkNibbleArray chunkNibbleArray2;
            long o = BlockPos.offset(l, direction);
            if (o == m || (chunkNibbleArray2 = n == (p = ChunkSectionPos.fromGlobalPos(o)) ? chunkNibbleArray : ((BlockLightStorage)this.lightStorage).getLightArray(p, true)) == null) continue;
            int q = this.getPropagatedLevel(o, l, this.getCurrentLevelFromArray(chunkNibbleArray2, o));
            if (j > q) {
                j = q;
            }
            if (j != 0) continue;
            return j;
        }
        return j;
    }

    @Override
    public void addLightSource(BlockPos blockPos, int i) {
        ((BlockLightStorage)this.lightStorage).updateAll();
        this.updateLevel(Long.MAX_VALUE, blockPos.asLong(), 15 - i, true);
    }
}

