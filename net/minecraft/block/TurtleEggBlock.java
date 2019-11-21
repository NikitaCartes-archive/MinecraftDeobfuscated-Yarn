/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TurtleEggBlock
extends Block {
    private static final VoxelShape SMALL_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 12.0, 7.0, 12.0);
    private static final VoxelShape LARGE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 7.0, 15.0);
    public static final IntProperty HATCH = Properties.HATCH;
    public static final IntProperty EGGS = Properties.EGGS;

    public TurtleEggBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(HATCH, 0)).with(EGGS, 1));
    }

    @Override
    public void onSteppedOn(World world, BlockPos blockPos, Entity entity) {
        this.tryBreakEgg(world, blockPos, entity, 100);
        super.onSteppedOn(world, blockPos, entity);
    }

    @Override
    public void onLandedUpon(World world, BlockPos blockPos, Entity entity, float f) {
        if (!(entity instanceof ZombieEntity)) {
            this.tryBreakEgg(world, blockPos, entity, 3);
        }
        super.onLandedUpon(world, blockPos, entity, f);
    }

    private void tryBreakEgg(World world, BlockPos blockPos, Entity entity, int i) {
        if (!this.breaksEgg(world, entity)) {
            super.onSteppedOn(world, blockPos, entity);
            return;
        }
        if (!world.isClient && world.random.nextInt(i) == 0) {
            this.breakEgg(world, blockPos, world.getBlockState(blockPos));
        }
    }

    private void breakEgg(World world, BlockPos blockPos, BlockState blockState) {
        world.playSound(null, blockPos, SoundEvents.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7f, 0.9f + world.random.nextFloat() * 0.2f);
        int i = blockState.get(EGGS);
        if (i <= 1) {
            world.breakBlock(blockPos, false);
        } else {
            world.setBlockState(blockPos, (BlockState)blockState.with(EGGS, i - 1), 2);
            world.playLevelEvent(2001, blockPos, Block.getRawIdFromState(blockState));
        }
    }

    @Override
    public void scheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
        if (this.shouldHatchProgress(serverWorld) && this.isSand(serverWorld, blockPos)) {
            int i = blockState.get(HATCH);
            if (i < 2) {
                serverWorld.playSound(null, blockPos, SoundEvents.ENTITY_TURTLE_EGG_CRACK, SoundCategory.BLOCKS, 0.7f, 0.9f + random.nextFloat() * 0.2f);
                serverWorld.setBlockState(blockPos, (BlockState)blockState.with(HATCH, i + 1), 2);
            } else {
                serverWorld.playSound(null, blockPos, SoundEvents.ENTITY_TURTLE_EGG_HATCH, SoundCategory.BLOCKS, 0.7f, 0.9f + random.nextFloat() * 0.2f);
                serverWorld.removeBlock(blockPos, false);
                for (int j = 0; j < blockState.get(EGGS); ++j) {
                    serverWorld.playLevelEvent(2001, blockPos, Block.getRawIdFromState(blockState));
                    TurtleEntity turtleEntity = EntityType.TURTLE.create(serverWorld);
                    turtleEntity.setBreedingAge(-24000);
                    turtleEntity.setHomePos(blockPos);
                    turtleEntity.setPositionAndAngles((double)blockPos.getX() + 0.3 + (double)j * 0.2, blockPos.getY(), (double)blockPos.getZ() + 0.3, 0.0f, 0.0f);
                    serverWorld.spawnEntity(turtleEntity);
                }
            }
        }
    }

    private boolean isSand(BlockView blockView, BlockPos blockPos) {
        return blockView.getBlockState(blockPos.down()).getBlock() == Blocks.SAND;
    }

    @Override
    public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (this.isSand(world, blockPos) && !world.isClient) {
            world.playLevelEvent(2005, blockPos, 0);
        }
    }

    private boolean shouldHatchProgress(World world) {
        float f = world.getSkyAngle(1.0f);
        if ((double)f < 0.69 && (double)f > 0.65) {
            return true;
        }
        return world.random.nextInt(500) == 0;
    }

    @Override
    public void afterBreak(World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack) {
        super.afterBreak(world, playerEntity, blockPos, blockState, blockEntity, itemStack);
        this.breakEgg(world, blockPos, blockState);
    }

    @Override
    public boolean canReplace(BlockState blockState, ItemPlacementContext itemPlacementContext) {
        if (itemPlacementContext.getStack().getItem() == this.asItem() && blockState.get(EGGS) < 4) {
            return true;
        }
        return super.canReplace(blockState, itemPlacementContext);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        BlockState blockState = itemPlacementContext.getWorld().getBlockState(itemPlacementContext.getBlockPos());
        if (blockState.getBlock() == this) {
            return (BlockState)blockState.with(EGGS, Math.min(4, blockState.get(EGGS) + 1));
        }
        return super.getPlacementState(itemPlacementContext);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        if (blockState.get(EGGS) > 1) {
            return LARGE_SHAPE;
        }
        return SMALL_SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HATCH, EGGS);
    }

    private boolean breaksEgg(World world, Entity entity) {
        if (entity instanceof TurtleEntity) {
            return false;
        }
        if (entity instanceof LivingEntity && !(entity instanceof PlayerEntity)) {
            return world.getGameRules().getBoolean(GameRules.MOB_GRIEFING);
        }
        return true;
    }
}

