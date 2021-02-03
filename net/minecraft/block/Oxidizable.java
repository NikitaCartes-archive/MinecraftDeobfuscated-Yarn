/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public interface Oxidizable<T extends Enum<T>> {
    public BlockState getOxidationResult(BlockState var1);

    public float method_33620();

    default public void method_33621(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
        float f = 0.05688889f;
        if (random.nextFloat() < 0.05688889f) {
            this.method_33623(blockState, serverWorld, blockPos, random);
        }
    }

    public T method_33622();

    default public void method_33623(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
        BlockPos blockPos2;
        int l;
        int i = ((Enum)this.method_33622()).ordinal();
        int j = 0;
        int k = 0;
        Iterator<BlockPos> iterator = BlockPos.iterateOutwards(blockPos, 4, 4, 4).iterator();
        while (iterator.hasNext() && (l = (blockPos2 = iterator.next()).getManhattanDistance(blockPos)) <= 4) {
            BlockState blockState2;
            Block block;
            if (blockPos2.equals(blockPos) || !((block = (blockState2 = serverWorld.getBlockState(blockPos2)).getBlock()) instanceof Oxidizable)) continue;
            T enum_ = ((Oxidizable)((Object)block)).method_33622();
            if (this.method_33622().getClass() != enum_.getClass()) continue;
            int m = ((Enum)enum_).ordinal();
            if (m < i) {
                return;
            }
            if (m > i) {
                ++k;
                continue;
            }
            ++j;
        }
        float f = (float)(k + 1) / (float)(k + j + 1);
        float g = f * f * this.method_33620();
        if (random.nextFloat() < g) {
            serverWorld.setBlockState(blockPos, this.getOxidationResult(blockState));
        }
    }
}

