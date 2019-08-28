/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class CoralWallFanBlock
extends DeadCoralWallFanBlock {
    private final Block deadCoralBlock;

    protected CoralWallFanBlock(Block block, Block.Settings settings) {
        super(settings);
        this.deadCoralBlock = block;
    }

    @Override
    public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        this.checkLivingConditions(blockState, world, blockPos);
    }

    @Override
    public void onScheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
        if (!CoralWallFanBlock.isInWater(blockState, serverWorld, blockPos)) {
            serverWorld.setBlockState(blockPos, (BlockState)((BlockState)this.deadCoralBlock.getDefaultState().with(WATERLOGGED, false)).with(FACING, blockState.get(FACING)), 2);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
        if (direction.getOpposite() == blockState.get(FACING) && !blockState.canPlaceAt(iWorld, blockPos)) {
            return Blocks.AIR.getDefaultState();
        }
        if (blockState.get(WATERLOGGED).booleanValue()) {
            iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
        }
        this.checkLivingConditions(blockState, iWorld, blockPos);
        return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
    }
}

