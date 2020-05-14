/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
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
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class JukeboxBlock
extends BlockWithEntity {
    public static final BooleanProperty HAS_RECORD = Properties.HAS_RECORD;

    protected JukeboxBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(HAS_RECORD, false));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (state.get(HAS_RECORD).booleanValue()) {
            this.removeRecord(world, pos);
            state = (BlockState)state.with(HAS_RECORD, false);
            world.setBlockState(pos, state, 2);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public void setRecord(WorldAccess world, BlockPos pos, BlockState state, ItemStack stack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof JukeboxBlockEntity)) {
            return;
        }
        ((JukeboxBlockEntity)blockEntity).setRecord(stack.copy());
        world.setBlockState(pos, (BlockState)state.with(HAS_RECORD, true), 2);
    }

    private void removeRecord(World world, BlockPos pos) {
        if (world.isClient) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof JukeboxBlockEntity)) {
            return;
        }
        JukeboxBlockEntity jukeboxBlockEntity = (JukeboxBlockEntity)blockEntity;
        ItemStack itemStack = jukeboxBlockEntity.getRecord();
        if (itemStack.isEmpty()) {
            return;
        }
        world.syncWorldEvent(1010, pos, 0);
        jukeboxBlockEntity.clear();
        float f = 0.7f;
        double d = (double)(world.random.nextFloat() * 0.7f) + (double)0.15f;
        double e = (double)(world.random.nextFloat() * 0.7f) + 0.06000000238418579 + 0.6;
        double g = (double)(world.random.nextFloat() * 0.7f) + (double)0.15f;
        ItemStack itemStack2 = itemStack.copy();
        ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + d, (double)pos.getY() + e, (double)pos.getZ() + g, itemStack2);
        itemEntity.setToDefaultPickupDelay();
        world.spawnEntity(itemEntity);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean notify) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        this.removeRecord(world, pos);
        super.onStateReplaced(state, world, pos, newState, notify);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new JukeboxBlockEntity();
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        Item item;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof JukeboxBlockEntity && (item = ((JukeboxBlockEntity)blockEntity).getRecord().getItem()) instanceof MusicDiscItem) {
            return ((MusicDiscItem)item).getComparatorOutput();
        }
        return 0;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HAS_RECORD);
    }
}

