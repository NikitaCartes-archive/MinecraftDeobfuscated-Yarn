/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Optional;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CampfireBlock
extends BlockWithEntity
implements Waterloggable {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 7.0, 16.0);
    public static final BooleanProperty LIT = Properties.LIT;
    public static final BooleanProperty SIGNAL_FIRE = Properties.SIGNAL_FIRE;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final DirectionProperty FACING = Properties.FACING_HORIZONTAL;

    public CampfireBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateFactory.getDefaultState()).with(LIT, true)).with(SIGNAL_FIRE, false)).with(WATERLOGGED, false)).with(FACING, Direction.NORTH));
    }

    @Override
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        ItemStack itemStack;
        CampfireBlockEntity campfireBlockEntity;
        Optional<CampfireCookingRecipe> optional;
        BlockEntity blockEntity;
        if (blockState.get(LIT).booleanValue() && (blockEntity = world.getBlockEntity(blockPos)) instanceof CampfireBlockEntity && (optional = (campfireBlockEntity = (CampfireBlockEntity)blockEntity).getRecipeFor(itemStack = playerEntity.getStackInHand(hand))).isPresent()) {
            if (!world.isClient && campfireBlockEntity.addItem(playerEntity.abilities.creativeMode ? itemStack.copy() : itemStack, optional.get().getCookTime())) {
                playerEntity.incrementStat(Stats.INTERACT_WITH_CAMPFIRE);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
        if (!entity.isFireImmune() && blockState.get(LIT).booleanValue() && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entity)) {
            entity.damage(DamageSource.IN_FIRE, 1.0f);
        }
        super.onEntityCollision(blockState, world, blockPos, entity);
    }

    @Override
    public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState.getBlock() == blockState2.getBlock()) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity instanceof CampfireBlockEntity) {
            ItemScatterer.spawn(world, blockPos, ((CampfireBlockEntity)blockEntity).getItemsBeingCooked());
        }
        super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        BlockPos blockPos;
        World iWorld = itemPlacementContext.getWorld();
        boolean bl = iWorld.getFluidState(blockPos = itemPlacementContext.getBlockPos()).getFluid() == Fluids.WATER;
        return (BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(WATERLOGGED, bl)).with(SIGNAL_FIRE, this.doesBlockCauseSignalFire(iWorld.getBlockState(blockPos.down())))).with(LIT, !bl)).with(FACING, itemPlacementContext.getPlayerHorizontalFacing());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
        if (blockState.get(WATERLOGGED).booleanValue()) {
            iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
        }
        if (direction == Direction.DOWN) {
            return (BlockState)blockState.with(SIGNAL_FIRE, this.doesBlockCauseSignalFire(blockState2));
        }
        return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
    }

    private boolean doesBlockCauseSignalFire(BlockState blockState) {
        return blockState.getBlock() == Blocks.HAY_BLOCK;
    }

    @Override
    public int getLuminance(BlockState blockState) {
        return blockState.get(LIT) != false ? super.getLuminance(blockState) : 0;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        if (!blockState.get(LIT).booleanValue()) {
            return;
        }
        if (random.nextInt(10) == 0) {
            world.playSound((float)blockPos.getX() + 0.5f, (double)((float)blockPos.getY() + 0.5f), (double)((float)blockPos.getZ() + 0.5f), SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 0.5f + random.nextFloat(), random.nextFloat() * 0.7f + 0.6f, false);
        }
        if (random.nextInt(5) == 0) {
            for (int i = 0; i < random.nextInt(1) + 1; ++i) {
                world.addParticle(ParticleTypes.LAVA, (float)blockPos.getX() + 0.5f, (float)blockPos.getY() + 0.5f, (float)blockPos.getZ() + 0.5f, random.nextFloat() / 2.0f, 5.0E-5, random.nextFloat() / 2.0f);
            }
        }
    }

    @Override
    public boolean tryFillWithFluid(IWorld iWorld, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
        if (!blockState.get(Properties.WATERLOGGED).booleanValue() && fluidState.getFluid() == Fluids.WATER) {
            boolean bl = blockState.get(LIT);
            if (bl) {
                if (iWorld.isClient()) {
                    for (int i = 0; i < 20; ++i) {
                        CampfireBlock.spawnSmokeParticle(iWorld.getWorld(), blockPos, blockState.get(SIGNAL_FIRE), true);
                    }
                } else {
                    iWorld.playSound(null, blockPos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                }
                BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
                if (blockEntity instanceof CampfireBlockEntity) {
                    ((CampfireBlockEntity)blockEntity).spawnItemsBeingCooked();
                }
            }
            iWorld.setBlockState(blockPos, (BlockState)((BlockState)blockState.with(WATERLOGGED, true)).with(LIT, false), 3);
            iWorld.getFluidTickScheduler().schedule(blockPos, fluidState.getFluid(), fluidState.getFluid().getTickRate(iWorld));
            return true;
        }
        return false;
    }

    @Override
    public void onProjectileHit(World world, BlockState blockState, BlockHitResult blockHitResult, Entity entity) {
        ProjectileEntity projectileEntity;
        if (!world.isClient && entity instanceof ProjectileEntity && (projectileEntity = (ProjectileEntity)entity).isOnFire() && !blockState.get(LIT).booleanValue() && !blockState.get(WATERLOGGED).booleanValue()) {
            BlockPos blockPos = blockHitResult.getBlockPos();
            world.setBlockState(blockPos, (BlockState)blockState.with(Properties.LIT, true), 11);
        }
    }

    public static void spawnSmokeParticle(World world, BlockPos blockPos, boolean bl, boolean bl2) {
        Random random = world.getRandom();
        DefaultParticleType defaultParticleType = bl ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
        world.addImportantParticle(defaultParticleType, true, (double)blockPos.getX() + 0.5 + random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1), (double)blockPos.getY() + random.nextDouble() + random.nextDouble(), (double)blockPos.getZ() + 0.5 + random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1), 0.0, 0.07, 0.0);
        if (bl2) {
            world.addParticle(ParticleTypes.SMOKE, (double)blockPos.getX() + 0.25 + random.nextDouble() / 2.0 * (double)(random.nextBoolean() ? 1 : -1), (double)blockPos.getY() + 0.4, (double)blockPos.getZ() + 0.25 + random.nextDouble() / 2.0 * (double)(random.nextBoolean() ? 1 : -1), 0.0, 0.005, 0.0);
        }
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        if (blockState.get(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(blockState);
    }

    @Override
    public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
        return (BlockState)blockState.with(FACING, blockRotation.rotate(blockState.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
        return blockState.rotate(blockMirror.getRotation(blockState.get(FACING)));
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        builder.add(LIT, SIGNAL_FIRE, WATERLOGGED, FACING);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new CampfireBlockEntity();
    }

    @Override
    public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
        return false;
    }
}

