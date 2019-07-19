/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.container.BlockContext;
import net.minecraft.container.LoomContainer;
import net.minecraft.container.NameableContainerFactory;
import net.minecraft.container.SimpleNamedContainerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LoomBlock
extends HorizontalFacingBlock {
    private static final TranslatableText CONTAINER_NAME = new TranslatableText("container.loom", new Object[0]);

    protected LoomBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (world.isClient) {
            return true;
        }
        playerEntity.openContainer(blockState.createContainerFactory(world, blockPos));
        playerEntity.incrementStat(Stats.INTERACT_WITH_LOOM);
        return true;
    }

    @Override
    public NameableContainerFactory createContainerFactory(BlockState blockState, World world, BlockPos blockPos) {
        return new SimpleNamedContainerFactory((i, playerInventory, playerEntity) -> new LoomContainer(i, playerInventory, BlockContext.create(world, blockPos)), CONTAINER_NAME);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        return (BlockState)this.getDefaultState().with(FACING, itemPlacementContext.getPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}

