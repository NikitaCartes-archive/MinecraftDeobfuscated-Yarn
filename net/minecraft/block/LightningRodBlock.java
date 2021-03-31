/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RodBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.client.util.ParticleUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;

public class LightningRodBlock
extends RodBlock
implements Waterloggable {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final BooleanProperty POWERED = Properties.POWERED;
    private static final int field_31192 = 8;
    public static final int field_31190 = 128;
    public static final int field_31191 = 200;

    public LightningRodBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.UP)).with(WATERLOGGED, false)).with(POWERED, false));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean bl = fluidState.getFluid() == Fluids.WATER;
        return (BlockState)((BlockState)this.getDefaultState().with(FACING, ctx.getSide())).with(WATERLOGGED, bl);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED).booleanValue()) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
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
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) != false ? 15 : 0;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (state.get(POWERED).booleanValue() && state.get(FACING) == direction) {
            return 15;
        }
        return 0;
    }

    public void setPowered(BlockState state, World world, BlockPos pos) {
        world.setBlockState(pos, (BlockState)state.with(POWERED, true), Block.NOTIFY_ALL);
        this.updateNeighbors(state, world, pos);
        world.getBlockTickScheduler().schedule(pos, this, 8);
        world.syncWorldEvent(WorldEvents.ELECTRICITY_SPARKS, pos, state.get(FACING).getAxis().ordinal());
    }

    private void updateNeighbors(BlockState state, World world, BlockPos pos) {
        world.updateNeighborsAlways(pos.offset(state.get(FACING).getOpposite()), this);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.setBlockState(pos, (BlockState)state.with(POWERED, false), Block.NOTIFY_ALL);
        this.updateNeighbors(state, world, pos);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!world.isThundering() || (long)world.random.nextInt(200) > world.getTime() % 200L || pos.getY() != world.getTopY(Heightmap.Type.WORLD_SURFACE, pos.getX(), pos.getZ()) - 1) {
            return;
        }
        ParticleUtil.spawnParticle(state.get(FACING).getAxis(), world, pos, 0.125, ParticleTypes.ELECTRIC_SPARK, UniformIntProvider.create(1, 2));
    }

    public static double method_35282(Random random, Direction.Axis axis, double d, Direction.Axis axis2) {
        double e = axis == axis2 ? 1.0 : 0.25;
        return d + random.nextDouble() * e - e / 2.0;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved || state.isOf(newState.getBlock())) {
            return;
        }
        if (state.get(POWERED).booleanValue()) {
            this.updateNeighbors(state, world, pos);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        BlockPos blockPos;
        if (world.isThundering() && projectile instanceof TridentEntity && ((TridentEntity)projectile).hasChanneling() && world.isSkyVisible(blockPos = hit.getBlockPos())) {
            LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(world);
            lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos.up()));
            Entity entity = projectile.getOwner();
            lightningEntity.setChanneler(entity instanceof ServerPlayerEntity ? (ServerPlayerEntity)entity : null);
            world.spawnEntity(lightningEntity);
            world.playSound(null, blockPos, SoundEvents.ITEM_TRIDENT_THUNDER, SoundCategory.WEATHER, 5.0f, 1.0f);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, WATERLOGGED);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }
}

