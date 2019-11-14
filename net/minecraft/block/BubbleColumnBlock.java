/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidDrainable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class BubbleColumnBlock
extends Block
implements FluidDrainable {
    public static final BooleanProperty DRAG = Properties.DRAG;

    public BubbleColumnBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(DRAG, true));
    }

    @Override
    public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
        BlockState blockState2 = world.getBlockState(blockPos.up());
        if (blockState2.isAir()) {
            entity.onBubbleColumnSurfaceCollision(blockState.get(DRAG));
            if (!world.isClient) {
                ServerWorld serverWorld = (ServerWorld)world;
                for (int i = 0; i < 2; ++i) {
                    serverWorld.spawnParticles(ParticleTypes.SPLASH, (float)blockPos.getX() + world.random.nextFloat(), blockPos.getY() + 1, (float)blockPos.getZ() + world.random.nextFloat(), 1, 0.0, 0.0, 0.0, 1.0);
                    serverWorld.spawnParticles(ParticleTypes.BUBBLE, (float)blockPos.getX() + world.random.nextFloat(), blockPos.getY() + 1, (float)blockPos.getZ() + world.random.nextFloat(), 1, 0.0, 0.01, 0.0, 0.2);
                }
            }
        } else {
            entity.onBubbleColumnCollision(blockState.get(DRAG));
        }
    }

    @Override
    public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        BubbleColumnBlock.update(world, blockPos.up(), BubbleColumnBlock.calculateDrag(world, blockPos.method_10074()));
    }

    @Override
    public void scheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
        BubbleColumnBlock.update(serverWorld, blockPos.up(), BubbleColumnBlock.calculateDrag(serverWorld, blockPos));
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        return Fluids.WATER.getStill(false);
    }

    public static void update(IWorld iWorld, BlockPos blockPos, boolean bl) {
        if (BubbleColumnBlock.isStillWater(iWorld, blockPos)) {
            iWorld.setBlockState(blockPos, (BlockState)Blocks.BUBBLE_COLUMN.getDefaultState().with(DRAG, bl), 2);
        }
    }

    public static boolean isStillWater(IWorld iWorld, BlockPos blockPos) {
        FluidState fluidState = iWorld.getFluidState(blockPos);
        return iWorld.getBlockState(blockPos).getBlock() == Blocks.WATER && fluidState.getLevel() >= 8 && fluidState.isStill();
    }

    private static boolean calculateDrag(BlockView blockView, BlockPos blockPos) {
        BlockState blockState = blockView.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (block == Blocks.BUBBLE_COLUMN) {
            return blockState.get(DRAG);
        }
        return block != Blocks.SOUL_SAND;
    }

    @Override
    public int getTickRate(WorldView worldView) {
        return 5;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        double d = blockPos.getX();
        double e = blockPos.getY();
        double f = blockPos.getZ();
        if (blockState.get(DRAG).booleanValue()) {
            world.addImportantParticle(ParticleTypes.CURRENT_DOWN, d + 0.5, e + 0.8, f, 0.0, 0.0, 0.0);
            if (random.nextInt(200) == 0) {
                world.playSound(d, e, f, SoundEvents.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundCategory.BLOCKS, 0.2f + random.nextFloat() * 0.2f, 0.9f + random.nextFloat() * 0.15f, false);
            }
        } else {
            world.addImportantParticle(ParticleTypes.BUBBLE_COLUMN_UP, d + 0.5, e, f + 0.5, 0.0, 0.04, 0.0);
            world.addImportantParticle(ParticleTypes.BUBBLE_COLUMN_UP, d + (double)random.nextFloat(), e + (double)random.nextFloat(), f + (double)random.nextFloat(), 0.0, 0.04, 0.0);
            if (random.nextInt(200) == 0) {
                world.playSound(d, e, f, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 0.2f + random.nextFloat() * 0.2f, 0.9f + random.nextFloat() * 0.15f, false);
            }
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
        if (!blockState.canPlaceAt(iWorld, blockPos)) {
            return Blocks.WATER.getDefaultState();
        }
        if (direction == Direction.DOWN) {
            iWorld.setBlockState(blockPos, (BlockState)Blocks.BUBBLE_COLUMN.getDefaultState().with(DRAG, BubbleColumnBlock.calculateDrag(iWorld, blockPos2)), 2);
        } else if (direction == Direction.UP && blockState2.getBlock() != Blocks.BUBBLE_COLUMN && BubbleColumnBlock.isStillWater(iWorld, blockPos2)) {
            iWorld.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(iWorld));
        }
        iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
        return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
    }

    @Override
    public boolean canPlaceAt(BlockState blockState, WorldView worldView, BlockPos blockPos) {
        Block block = worldView.getBlockState(blockPos.method_10074()).getBlock();
        return block == Blocks.BUBBLE_COLUMN || block == Blocks.MAGMA_BLOCK || block == Blocks.SOUL_SAND;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        return VoxelShapes.empty();
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(DRAG);
    }

    @Override
    public Fluid tryDrainFluid(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
        iWorld.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 11);
        return Fluids.WATER;
    }
}

