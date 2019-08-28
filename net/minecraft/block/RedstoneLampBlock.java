/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RedstoneLampBlock
extends Block {
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

    public RedstoneLampBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)this.getDefaultState().with(LIT, false));
    }

    @Override
    public int getLuminance(BlockState blockState) {
        return blockState.get(LIT) != false ? super.getLuminance(blockState) : 0;
    }

    @Override
    public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        super.onBlockAdded(blockState, world, blockPos, blockState2, bl);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        return (BlockState)this.getDefaultState().with(LIT, itemPlacementContext.getWorld().isReceivingRedstonePower(itemPlacementContext.getBlockPos()));
    }

    @Override
    public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        if (world.isClient) {
            return;
        }
        boolean bl2 = blockState.get(LIT);
        if (bl2 != world.isReceivingRedstonePower(blockPos)) {
            if (bl2) {
                world.getBlockTickScheduler().schedule(blockPos, this, 4);
            } else {
                world.setBlockState(blockPos, (BlockState)blockState.cycle(LIT), 2);
            }
        }
    }

    @Override
    public void onScheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
        if (blockState.get(LIT).booleanValue() && !serverWorld.isReceivingRedstonePower(blockPos)) {
            serverWorld.setBlockState(blockPos, (BlockState)blockState.cycle(LIT), 2);
        }
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }
}

