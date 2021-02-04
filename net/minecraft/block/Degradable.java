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

public interface Degradable<T extends Enum<T>> {
    public BlockState getDegradationResult(BlockState var1);

    public float getDegradationChanceMultiplier();

    default public void tickDegradation(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        float f = 0.05688889f;
        if (random.nextFloat() < 0.05688889f) {
            this.tryDegrade(state, world, pos, random);
        }
    }

    public T getDegradationLevel();

    default public void tryDegrade(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockPos blockPos;
        int l;
        int i = ((Enum)this.getDegradationLevel()).ordinal();
        int j = 0;
        int k = 0;
        Iterator<BlockPos> iterator = BlockPos.iterateOutwards(pos, 4, 4, 4).iterator();
        while (iterator.hasNext() && (l = (blockPos = iterator.next()).getManhattanDistance(pos)) <= 4) {
            BlockState blockState;
            Block block;
            if (blockPos.equals(pos) || !((block = (blockState = world.getBlockState(blockPos)).getBlock()) instanceof Degradable)) continue;
            T enum_ = ((Degradable)((Object)block)).getDegradationLevel();
            if (this.getDegradationLevel().getClass() != enum_.getClass()) continue;
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
        float g = f * f * this.getDegradationChanceMultiplier();
        if (random.nextFloat() < g) {
            world.setBlockState(pos, this.getDegradationResult(state));
        }
    }
}

