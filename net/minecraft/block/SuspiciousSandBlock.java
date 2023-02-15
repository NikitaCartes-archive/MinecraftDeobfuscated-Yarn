/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.LandingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SuspiciousSandBlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

public class SuspiciousSandBlock
extends BlockWithEntity
implements LandingBlock {
    private static final IntProperty DUSTED = Properties.DUSTED;
    public static final int field_42773 = 2;

    public SuspiciousSandBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(DUSTED, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(DUSTED);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SuspiciousSandBlockEntity(pos, state);
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.DESTROY;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        world.scheduleBlockTick(pos, this, 2);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        world.scheduleBlockTick(pos, this, 2);
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SuspiciousSandBlockEntity) {
            SuspiciousSandBlockEntity suspiciousSandBlockEntity = (SuspiciousSandBlockEntity)blockEntity;
            suspiciousSandBlockEntity.scheduledTick();
        }
        if (!FallingBlock.canFallThrough(world.getBlockState(pos.down())) || pos.getY() < world.getBottomY()) {
            return;
        }
        FallingBlockEntity fallingBlockEntity = FallingBlockEntity.spawnFromBlock(world, pos, state);
        fallingBlockEntity.setDestroyedOnLanding();
    }

    @Override
    public void onDestroyedOnLanding(World world, BlockPos pos, FallingBlockEntity fallingBlockEntity) {
        Vec3d vec3d = fallingBlockEntity.getBoundingBox().getCenter();
        world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, new BlockPos(vec3d), Block.getRawIdFromState(fallingBlockEntity.getBlockState()));
        world.emitGameEvent((Entity)fallingBlockEntity, GameEvent.BLOCK_DESTROY, vec3d);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        BlockPos blockPos;
        if (random.nextInt(16) == 0 && FallingBlock.canFallThrough(world.getBlockState(blockPos = pos.down()))) {
            double d = (double)pos.getX() + random.nextDouble();
            double e = (double)pos.getY() - 0.05;
            double f = (double)pos.getZ() + random.nextDouble();
            world.addParticle(new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, state), d, e, f, 0.0, 0.0, 0.0);
        }
    }
}

