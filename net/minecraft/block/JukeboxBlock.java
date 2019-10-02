/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class JukeboxBlock
extends BlockWithEntity {
    public static final BooleanProperty HAS_RECORD = Properties.HAS_RECORD;

    protected JukeboxBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateFactory.getDefaultState()).with(HAS_RECORD, false));
    }

    @Override
    public boolean onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (blockState.get(HAS_RECORD).booleanValue()) {
            this.removeRecord(world, blockPos);
            blockState = (BlockState)blockState.with(HAS_RECORD, false);
            world.setBlockState(blockPos, blockState, 2);
            return true;
        }
        return false;
    }

    public void setRecord(IWorld iWorld, BlockPos blockPos, BlockState blockState, ItemStack itemStack) {
        BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
        if (!(blockEntity instanceof JukeboxBlockEntity)) {
            return;
        }
        ((JukeboxBlockEntity)blockEntity).setRecord(itemStack.copy());
        iWorld.setBlockState(blockPos, (BlockState)blockState.with(HAS_RECORD, true), 2);
    }

    private void removeRecord(World world, BlockPos blockPos) {
        if (world.isClient) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (!(blockEntity instanceof JukeboxBlockEntity)) {
            return;
        }
        JukeboxBlockEntity jukeboxBlockEntity = (JukeboxBlockEntity)blockEntity;
        ItemStack itemStack = jukeboxBlockEntity.getRecord();
        if (itemStack.isEmpty()) {
            return;
        }
        world.playLevelEvent(1010, blockPos, 0);
        jukeboxBlockEntity.clear();
        float f = 0.7f;
        double d = (double)(world.random.nextFloat() * 0.7f) + (double)0.15f;
        double e = (double)(world.random.nextFloat() * 0.7f) + 0.06000000238418579 + 0.6;
        double g = (double)(world.random.nextFloat() * 0.7f) + (double)0.15f;
        ItemStack itemStack2 = itemStack.copy();
        ItemEntity itemEntity = new ItemEntity(world, (double)blockPos.getX() + d, (double)blockPos.getY() + e, (double)blockPos.getZ() + g, itemStack2);
        itemEntity.setToDefaultPickupDelay();
        world.spawnEntity(itemEntity);
    }

    @Override
    public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState.getBlock() == blockState2.getBlock()) {
            return;
        }
        this.removeRecord(world, blockPos);
        super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new JukeboxBlockEntity();
    }

    @Override
    public boolean hasComparatorOutput(BlockState blockState) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
        Item item;
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity instanceof JukeboxBlockEntity && (item = ((JukeboxBlockEntity)blockEntity).getRecord().getItem()) instanceof MusicDiscItem) {
            return ((MusicDiscItem)item).getComparatorOutput();
        }
        return 0;
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HAS_RECORD);
    }
}

