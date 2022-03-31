/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Optional;
import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SculkShriekerBlockEntity;
import net.minecraft.block.entity.SculkShriekerWarningManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

public class SculkShriekerBlock
extends BlockWithEntity
implements Waterloggable {
    public static final BooleanProperty SHRIEKING = Properties.SHRIEKING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final BooleanProperty CAN_SUMMON = Properties.CAN_SUMMON;
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    private static final int SHRIEK_DURATION = 90;
    public static final double TOP = SHAPE.getMax(Direction.Axis.Y);

    public SculkShriekerBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(SHRIEKING, false)).with(WATERLOGGED, false)).with(CAN_SUMMON, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SHRIEKING);
        builder.add(WATERLOGGED);
        builder.add(CAN_SUMMON);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof PlayerEntity && world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            SculkShriekerBlock.shriek(serverWorld, state, pos);
        }
        super.onSteppedOn(world, pos, state, entity);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(SHRIEKING).booleanValue()) {
            world.setBlockState(pos, (BlockState)state.with(SHRIEKING, false), Block.NOTIFY_ALL);
            if (state.get(CAN_SUMMON).booleanValue()) {
                SculkShriekerBlock.getClosestPlayerWarningManager(world, pos).ifPresent(warningManager -> warningManager.warn(world, pos));
            }
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            if (world.isReceivingRedstonePower(pos)) {
                SculkShriekerBlock.shriek(serverWorld, state, pos);
            }
        }
    }

    public static boolean canShriek(ServerWorld world, BlockPos pos, BlockState state) {
        return state.get(SHRIEKING) == false && (state.get(CAN_SUMMON) == false || SculkShriekerBlock.getClosestPlayerWarningManager(world, pos).map(warningManager -> warningManager.canIncreaseWarningLevel(world, pos)).orElse(false) != false);
    }

    public static void shriek(ServerWorld world, BlockState state, BlockPos pos) {
        if (!SculkShriekerBlock.canShriek(world, pos, state)) {
            return;
        }
        if (!state.get(CAN_SUMMON).booleanValue() || SculkShriekerBlock.getClosestPlayerWarningManager(world, pos).filter(warningManager -> warningManager.warnNearbyPlayers(world, pos)).isPresent()) {
            world.setBlockState(pos, (BlockState)state.with(SHRIEKING, true), Block.NOTIFY_LISTENERS);
            world.createAndScheduleBlockTick(pos, state.getBlock(), 90);
            world.syncWorldEvent(WorldEvents.SCULK_SHRIEKS, pos, 0);
        }
    }

    private static Optional<SculkShriekerWarningManager> getClosestPlayerWarningManager(ServerWorld world, BlockPos pos) {
        PlayerEntity playerEntity = world.getClosestPlayer((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 16.0, EntityPredicates.EXCEPT_SPECTATOR.and(Entity::isAlive));
        return playerEntity == null ? Optional.empty() : Optional.of(playerEntity.getSculkShriekerWarningManager());
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return SHAPE;
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    @Nullable
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SculkShriekerBlockEntity(pos, state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED).booleanValue()) {
            world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.get(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    @Override
    public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack) {
        super.onStacksDropped(state, world, pos, stack);
        this.dropExperienceWhenMined(world, pos, stack, ConstantIntProvider.create(5));
    }

    @Override
    @Nullable
    public <T extends BlockEntity> GameEventListener getGameEventListener(ServerWorld world, T blockEntity) {
        if (blockEntity instanceof SculkShriekerBlockEntity) {
            SculkShriekerBlockEntity sculkShriekerBlockEntity = (SculkShriekerBlockEntity)blockEntity;
            return sculkShriekerBlockEntity.getVibrationListener();
        }
        return null;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world2, BlockState state2, BlockEntityType<T> type) {
        if (!world2.isClient) {
            return BlockWithEntity.checkType(type, BlockEntityType.SCULK_SHRIEKER, (world, pos, state, blockEntity) -> blockEntity.getVibrationListener().tick(world));
        }
        return null;
    }
}

