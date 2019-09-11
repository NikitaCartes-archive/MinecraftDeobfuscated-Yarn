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
import net.minecraft.class_4538;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.light.ChunkLightProvider;

public abstract class SpreadableBlock
extends SnowyBlock {
    protected SpreadableBlock(Block.Settings settings) {
        super(settings);
    }

    private static boolean canSurvive(BlockState blockState, class_4538 arg, BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.up();
        BlockState blockState2 = arg.getBlockState(blockPos2);
        if (blockState2.getBlock() == Blocks.SNOW && blockState2.get(SnowBlock.LAYERS) == 1) {
            return true;
        }
        int i = ChunkLightProvider.getRealisticOpacity(arg, blockState, blockPos, blockState2, blockPos2, Direction.UP, blockState2.getOpacity(arg, blockPos2));
        return i < arg.getMaxLightLevel();
    }

    private static boolean canSpread(BlockState blockState, class_4538 arg, BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.up();
        return SpreadableBlock.canSurvive(blockState, arg, blockPos) && !arg.getFluidState(blockPos2).matches(FluidTags.WATER);
    }

    @Override
    public void onScheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
        if (!SpreadableBlock.canSurvive(blockState, serverWorld, blockPos)) {
            serverWorld.setBlockState(blockPos, Blocks.DIRT.getDefaultState());
            return;
        }
        if (serverWorld.getLightLevel(blockPos.up()) >= 9) {
            BlockState blockState2 = this.getDefaultState();
            for (int i = 0; i < 4; ++i) {
                BlockPos blockPos2 = blockPos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                if (serverWorld.getBlockState(blockPos2).getBlock() != Blocks.DIRT || !SpreadableBlock.canSpread(blockState2, serverWorld, blockPos2)) continue;
                serverWorld.setBlockState(blockPos2, (BlockState)blockState2.with(SNOWY, serverWorld.getBlockState(blockPos2.up()).getBlock() == Blocks.SNOW));
            }
        }
    }
}

