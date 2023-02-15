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
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class JukeboxBlock
extends BlockWithEntity {
    public static final BooleanProperty HAS_RECORD = Properties.HAS_RECORD;

    protected JukeboxBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(HAS_RECORD, false));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(itemStack);
        if (nbtCompound != null && nbtCompound.contains("RecordItem")) {
            world.setBlockState(pos, (BlockState)state.with(HAS_RECORD, true), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity;
        if (state.get(HAS_RECORD).booleanValue() && (blockEntity = world.getBlockEntity(pos)) instanceof JukeboxBlockEntity) {
            JukeboxBlockEntity jukeboxBlockEntity = (JukeboxBlockEntity)blockEntity;
            jukeboxBlockEntity.dropRecord();
            return ActionResult.success(world.isClient);
        }
        return ActionResult.PASS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof JukeboxBlockEntity) {
            JukeboxBlockEntity jukeboxBlockEntity = (JukeboxBlockEntity)blockEntity;
            jukeboxBlockEntity.dropRecord();
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new JukeboxBlockEntity(pos, state);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        JukeboxBlockEntity jukeboxBlockEntity;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof JukeboxBlockEntity && (jukeboxBlockEntity = (JukeboxBlockEntity)blockEntity).isPlayingRecord()) {
            return 15;
        }
        return 0;
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        JukeboxBlockEntity jukeboxBlockEntity;
        Object object = world.getBlockEntity(pos);
        if (object instanceof JukeboxBlockEntity && (object = (jukeboxBlockEntity = (JukeboxBlockEntity)object).getStack().getItem()) instanceof MusicDiscItem) {
            MusicDiscItem musicDiscItem = (MusicDiscItem)object;
            return musicDiscItem.getComparatorOutput();
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

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (state.get(HAS_RECORD).booleanValue()) {
            return JukeboxBlock.checkType(type, BlockEntityType.JUKEBOX, JukeboxBlockEntity::tick);
        }
        return null;
    }
}

