/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import org.jetbrains.annotations.Nullable;

public class CoralBlockBlock
extends Block {
    private final Block deadCoralBlock;

    public CoralBlockBlock(Block block, Block.Settings settings) {
        super(settings);
        this.deadCoralBlock = block;
    }

    @Override
    public void onScheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
        if (!this.isInWater(serverWorld, blockPos)) {
            serverWorld.setBlockState(blockPos, this.deadCoralBlock.getDefaultState(), 2);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
        if (!this.isInWater(iWorld, blockPos)) {
            iWorld.getBlockTickScheduler().schedule(blockPos, this, 60 + iWorld.getRandom().nextInt(40));
        }
        return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
    }

    protected boolean isInWater(BlockView blockView, BlockPos blockPos) {
        for (Direction direction : Direction.values()) {
            FluidState fluidState = blockView.getFluidState(blockPos.offset(direction));
            if (!fluidState.matches(FluidTags.WATER)) continue;
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        if (!this.isInWater(itemPlacementContext.getWorld(), itemPlacementContext.getBlockPos())) {
            itemPlacementContext.getWorld().getBlockTickScheduler().schedule(itemPlacementContext.getBlockPos(), this, 60 + itemPlacementContext.getWorld().getRandom().nextInt(40));
        }
        return this.getDefaultState();
    }
}

