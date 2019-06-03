/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk.light;

import java.util.concurrent.atomic.AtomicInteger;
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

public final class ChunkBlockLightProvider
extends ChunkLightProvider<BlockLightStorage.Data, BlockLightStorage> {
    private static final Direction[] DIRECTIONS_BLOCKLIGHT = Direction.values();
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
        AtomicInteger atomicInteger = new AtomicInteger();
        BlockState blockState = this.method_20479(m, atomicInteger);
        if (atomicInteger.get() >= 15) {
            return 15;
        }
        BlockState blockState2 = this.method_20479(l, null);
        VoxelShape voxelShape = this.method_20710(blockState2, l, direction);
        if (VoxelShapes.method_20713(voxelShape, voxelShape2 = this.method_20710(blockState, m, direction.getOpposite()))) {
            return 15;
        }
        return i + Math.max(1, atomicInteger.get());
    }

    @Override
    protected void updateNeighborsRecursively(long l, int i, boolean bl) {
        long m = ChunkSectionPos.toChunkLong(l);
        for (Direction direction : DIRECTIONS_BLOCKLIGHT) {
            long n = BlockPos.offset(l, direction);
            long o = ChunkSectionPos.toChunkLong(n);
            if (m != o && !((BlockLightStorage)this.lightStorage).hasChunk(o)) continue;
            this.updateRecursively(l, n, i, bl);
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
        ChunkNibbleArray chunkNibbleArray = ((BlockLightStorage)this.lightStorage).getDataForChunk(n, true);
        for (Direction direction : DIRECTIONS_BLOCKLIGHT) {
            long p;
            ChunkNibbleArray chunkNibbleArray2;
            long o = BlockPos.offset(l, direction);
            if (o == m || (chunkNibbleArray2 = n == (p = ChunkSectionPos.toChunkLong(o)) ? chunkNibbleArray : ((BlockLightStorage)this.lightStorage).getDataForChunk(p, true)) == null) continue;
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
    public void method_15514(BlockPos blockPos, int i) {
        ((BlockLightStorage)this.lightStorage).updateAll();
        this.update(Long.MAX_VALUE, blockPos.asLong(), 15 - i, true);
    }
}

