/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.SnowyBlock;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.CollisionView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.light.ChunkLightProvider;

public abstract class SpreadableBlock
extends SnowyBlock {
    protected SpreadableBlock(Block.Settings settings) {
        super(settings);
    }

    private static boolean canSurvive(BlockState blockState, CollisionView collisionView, BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.up();
        BlockState blockState2 = collisionView.getBlockState(blockPos2);
        if (blockState2.getBlock() == Blocks.SNOW && blockState2.get(SnowBlock.LAYERS) == 1) {
            return true;
        }
        int i = ChunkLightProvider.method_20049(collisionView, blockState, blockPos, blockState2, blockPos2, Direction.UP, blockState2.getOpacity(collisionView, blockPos2));
        return i < collisionView.getMaxLightLevel();
    }

    private static boolean canSpread(BlockState blockState, CollisionView collisionView, BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.up();
        return SpreadableBlock.canSurvive(blockState, collisionView, blockPos) && !collisionView.getFluidState(blockPos2).matches(FluidTags.WATER);
    }

    @Override
    public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        if (world.isClient) {
            return;
        }
        if (!SpreadableBlock.canSurvive(blockState, world, blockPos)) {
            world.setBlockState(blockPos, Blocks.DIRT.getDefaultState());
            return;
        }
        if (world.getLightLevel(blockPos.up()) >= 9) {
            BlockState blockState2 = this.getDefaultState();
            for (int i = 0; i < 4; ++i) {
                BlockPos blockPos2 = blockPos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                if (world.getBlockState(blockPos2).getBlock() != Blocks.DIRT || !SpreadableBlock.canSpread(blockState2, world, blockPos2)) continue;
                world.setBlockState(blockPos2, (BlockState)blockState2.with(SNOWY, world.getBlockState(blockPos2.up()).getBlock() == Blocks.SNOW));
            }
        }
    }
}

