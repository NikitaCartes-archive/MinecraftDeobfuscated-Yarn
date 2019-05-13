/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralFanBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class CoralFanBlock
extends DeadCoralFanBlock {
    private final Block deadCoralBlock;

    protected CoralFanBlock(Block block, Block.Settings settings) {
        super(settings);
        this.deadCoralBlock = block;
    }

    @Override
    public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        this.checkLivingConditions(blockState, world, blockPos);
    }

    @Override
    public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        if (!CoralFanBlock.isInWater(blockState, world, blockPos)) {
            world.setBlockState(blockPos, (BlockState)this.deadCoralBlock.getDefaultState().with(WATERLOGGED, false), 2);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
        if (direction == Direction.DOWN && !blockState.canPlaceAt(iWorld, blockPos)) {
            return Blocks.AIR.getDefaultState();
        }
        this.checkLivingConditions(blockState, iWorld, blockPos);
        if (blockState.get(WATERLOGGED).booleanValue()) {
            iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
        }
        return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
    }
}

