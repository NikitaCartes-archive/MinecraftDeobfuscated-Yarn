/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ComparatorBlock
extends AbstractRedstoneGateBlock
implements BlockEntityProvider {
    public static final EnumProperty<ComparatorMode> MODE = Properties.COMPARATOR_MODE;

    public ComparatorBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(POWERED, false)).with(MODE, ComparatorMode.COMPARE));
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return 2;
    }

    @Override
    protected int getOutputLevel(BlockView world, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ComparatorBlockEntity) {
            return ((ComparatorBlockEntity)blockEntity).getOutputSignal();
        }
        return 0;
    }

    private int calculateOutputSignal(World world, BlockPos pos, BlockState state) {
        int i = this.getPower(world, pos, state);
        if (i == 0) {
            return 0;
        }
        int j = this.getMaxInputLevelSides(world, pos, state);
        if (j > i) {
            return 0;
        }
        if (state.get(MODE) == ComparatorMode.SUBTRACT) {
            return i - j;
        }
        return i;
    }

    @Override
    protected boolean hasPower(World world, BlockPos pos, BlockState state) {
        int i = this.getPower(world, pos, state);
        if (i == 0) {
            return false;
        }
        int j = this.getMaxInputLevelSides(world, pos, state);
        if (i > j) {
            return true;
        }
        return i == j && state.get(MODE) == ComparatorMode.COMPARE;
    }

    @Override
    protected int getPower(World world, BlockPos pos, BlockState state) {
        int i = super.getPower(world, pos, state);
        Direction direction = state.get(FACING);
        BlockPos blockPos = pos.offset(direction);
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.hasComparatorOutput()) {
            i = blockState.getComparatorOutput(world, blockPos);
        } else if (i < 15 && blockState.isSolidBlock(world, blockPos)) {
            blockPos = blockPos.offset(direction);
            blockState = world.getBlockState(blockPos);
            ItemFrameEntity itemFrameEntity = this.getAttachedItemFrame(world, direction, blockPos);
            int j = Math.max(itemFrameEntity == null ? Integer.MIN_VALUE : itemFrameEntity.getComparatorPower(), blockState.hasComparatorOutput() ? blockState.getComparatorOutput(world, blockPos) : Integer.MIN_VALUE);
            if (j != Integer.MIN_VALUE) {
                i = j;
            }
        }
        return i;
    }

    @Nullable
    private ItemFrameEntity getAttachedItemFrame(World world, Direction facing, BlockPos pos) {
        List<ItemFrameEntity> list = world.getEntitiesByClass(ItemFrameEntity.class, new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1), itemFrameEntity -> itemFrameEntity != null && itemFrameEntity.getHorizontalFacing() == facing);
        if (list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.getAbilities().allowModifyWorld) {
            return ActionResult.PASS;
        }
        float f = (state = (BlockState)state.cycle(MODE)).get(MODE) == ComparatorMode.SUBTRACT ? 0.55f : 0.5f;
        world.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3f, f);
        world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
        this.update(world, pos, state);
        return ActionResult.success(world.isClient);
    }

    @Override
    protected void updatePowered(World world, BlockPos pos, BlockState state) {
        int j;
        if (world.getBlockTickScheduler().isTicking(pos, this)) {
            return;
        }
        int i = this.calculateOutputSignal(world, pos, state);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        int n = j = blockEntity instanceof ComparatorBlockEntity ? ((ComparatorBlockEntity)blockEntity).getOutputSignal() : 0;
        if (i != j || state.get(POWERED).booleanValue() != this.hasPower(world, pos, state)) {
            TickPriority tickPriority = this.isTargetNotAligned(world, pos, state) ? TickPriority.HIGH : TickPriority.NORMAL;
            world.getBlockTickScheduler().schedule(pos, this, 2, tickPriority);
        }
    }

    private void update(World world, BlockPos pos, BlockState state) {
        int i = this.calculateOutputSignal(world, pos, state);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        int j = 0;
        if (blockEntity instanceof ComparatorBlockEntity) {
            ComparatorBlockEntity comparatorBlockEntity = (ComparatorBlockEntity)blockEntity;
            j = comparatorBlockEntity.getOutputSignal();
            comparatorBlockEntity.setOutputSignal(i);
        }
        if (j != i || state.get(MODE) == ComparatorMode.COMPARE) {
            boolean bl = this.hasPower(world, pos, state);
            boolean bl2 = state.get(POWERED);
            if (bl2 && !bl) {
                world.setBlockState(pos, (BlockState)state.with(POWERED, false), Block.NOTIFY_LISTENERS);
            } else if (!bl2 && bl) {
                world.setBlockState(pos, (BlockState)state.with(POWERED, true), Block.NOTIFY_LISTENERS);
            }
            this.updateTarget(world, pos, state);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.update(world, pos, state);
    }

    @Override
    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        super.onSyncedBlockEvent(state, world, pos, type, data);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity != null && blockEntity.onSyncedBlockEvent(type, data);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ComparatorBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, MODE, POWERED);
    }
}

