/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.class_5816;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class class_5801
extends HorizontalFacingBlock
implements Fertilizable,
Waterloggable {
    private static final BooleanProperty field_28660 = Properties.WATERLOGGED;
    private static final EnumProperty<class_5816> field_28661 = Properties.field_28717;
    private static final Object2IntMap<class_5816> field_28662 = Util.make(new Object2IntArrayMap(), object2IntArrayMap -> {
        object2IntArrayMap.defaultReturnValue(-1);
        object2IntArrayMap.put(class_5816.field_28719, 20);
        object2IntArrayMap.put(class_5816.field_28720, 10);
        object2IntArrayMap.put(class_5816.field_28721, 100);
    });
    private static final Box field_28663 = Block.createCuboidShape(0.0, 11.0, 0.0, 16.0, 16.0, 16.0).getBoundingBox();
    private static final Map<class_5816, VoxelShape> field_28664 = ImmutableMap.of(class_5816.field_28718, Block.createCuboidShape(0.0, 11.0, 0.0, 16.0, 15.0, 16.0), class_5816.field_28719, Block.createCuboidShape(0.0, 11.0, 0.0, 16.0, 15.0, 16.0), class_5816.field_28720, Block.createCuboidShape(0.0, 11.0, 0.0, 16.0, 13.0, 16.0), class_5816.field_28721, VoxelShapes.empty());
    private static final Map<Direction, VoxelShape> field_28665 = ImmutableMap.of(Direction.NORTH, Block.createCuboidShape(5.0, 0.0, 8.0, 11.0, 11.0, 14.0), Direction.SOUTH, Block.createCuboidShape(5.0, 0.0, 2.0, 11.0, 11.0, 8.0), Direction.EAST, Block.createCuboidShape(2.0, 0.0, 5.0, 8.0, 11.0, 11.0), Direction.WEST, Block.createCuboidShape(8.0, 0.0, 5.0, 14.0, 11.0, 11.0));
    private final Map<BlockState, VoxelShape> field_28666;

    protected class_5801(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(field_28660, false)).with(FACING, Direction.NORTH)).with(field_28661, class_5816.field_28718));
        this.field_28666 = this.method_33615(class_5801::method_33611);
    }

    protected static VoxelShape method_33611(BlockState blockState) {
        return VoxelShapes.union(class_5801.method_33613(blockState), class_5801.method_33612(blockState));
    }

    private static VoxelShape method_33612(BlockState blockState) {
        return field_28665.get(blockState.get(FACING));
    }

    private static VoxelShape method_33613(BlockState blockState) {
        return field_28664.get(blockState.get(field_28661));
    }

    protected static void method_33603(World world, Random random, BlockPos blockPos) {
        int i = world.getTopHeightLimit() - blockPos.getY();
        int j = 1 + random.nextInt(5);
        int k = Math.min(j, i);
        Direction direction = Direction.Type.HORIZONTAL.random(random);
        BlockPos.Mutable mutable = blockPos.mutableCopy();
        for (int l = 0; l < k; ++l) {
            Block block = l == k - 1 ? Blocks.BIG_DRIPLEAF : Blocks.BIG_DRIPLEAF_STEM;
            BlockState blockState = (BlockState)((BlockState)block.getDefaultState().with(field_28660, world.getFluidState(mutable).getFluid() == Fluids.WATER)).with(HorizontalFacingBlock.FACING, direction);
            world.setBlockState(mutable, blockState, 2);
            mutable.move(Direction.UP);
        }
    }

    @Override
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        world.breakBlock(hit.getBlockPos(), true, projectile);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.get(field_28660).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.isOf(Blocks.BIG_DRIPLEAF_STEM) || blockState.isSideSolidFullSquare(world, blockPos, Direction.UP);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
        }
        if (state.get(field_28660).booleanValue()) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        BlockState blockState = world.getBlockState(pos.up());
        return blockState.isAir() || blockState.getFluidState().isIn(FluidTags.WATER);
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        boolean bl;
        BlockPos blockPos = pos.up();
        if (!world.isInBuildLimit(blockPos)) {
            return;
        }
        BlockState blockState = world.getBlockState(blockPos);
        Fluid fluid = blockState.getFluidState().getFluid();
        if (blockState.isAir() || fluid == Fluids.FLOWING_WATER) {
            bl = false;
        } else if (fluid == Fluids.WATER) {
            bl = true;
        } else {
            return;
        }
        world.setBlockState(blockPos, (BlockState)((BlockState)Blocks.BIG_DRIPLEAF.getDefaultState().with(FACING, state.get(FACING))).with(field_28660, bl), 2);
        world.setBlockState(pos, (BlockState)((BlockState)Blocks.BIG_DRIPLEAF_STEM.getDefaultState().with(FACING, state.get(FACING))).with(field_28660, state.get(field_28660)), 2);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isClient) {
            return;
        }
        if (state.get(field_28661) == class_5816.field_28718 && class_5801.method_33607(pos, entity, true)) {
            this.method_33605(state, world, pos, class_5816.field_28719, null);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        class_5816 lv = state.get(field_28661);
        if (lv == class_5816.field_28719) {
            if (class_5801.method_33602(world, pos, true)) {
                this.method_33605(state, world, pos, class_5816.field_28720, SoundEvents.BLOCK_BIG_DRIPLEAF_TILT_DOWN);
            } else {
                this.method_33610(state, world, pos);
            }
        } else if (lv == class_5816.field_28720) {
            if (class_5801.method_33602(world, pos, false)) {
                this.method_33605(state, world, pos, class_5816.field_28721, SoundEvents.BLOCK_BIG_DRIPLEAF_TILT_DOWN);
            } else {
                this.method_33605(state, world, pos, class_5816.field_28719, SoundEvents.BLOCK_BIG_DRIPLEAF_TILT_UP);
            }
        } else if (lv == class_5816.field_28721) {
            this.method_33610(state, world, pos);
        }
    }

    private void method_33601(World world, BlockPos blockPos, SoundEvent soundEvent) {
        float f = MathHelper.nextBetween(world.random, 0.8f, 1.2f);
        world.playSound(null, blockPos, soundEvent, SoundCategory.BLOCKS, 1.0f, f);
    }

    private static boolean method_33602(World world, BlockPos blockPos, boolean bl) {
        Predicate<Entity> predicate = EntityPredicates.EXCEPT_SPECTATOR.and(entity -> class_5801.method_33607(blockPos, entity, bl));
        return !world.getOtherEntities(null, field_28663.offset(blockPos), predicate).isEmpty();
    }

    private static boolean method_33607(BlockPos blockPos, Entity entity, boolean bl) {
        if (bl && entity.bypassesSteppingEffects()) {
            return false;
        }
        return class_5801.method_33606(blockPos, entity);
    }

    private static boolean method_33606(BlockPos blockPos, Entity entity) {
        return entity.getPos().y > (double)((float)blockPos.getY() + 0.6875f);
    }

    private void method_33605(BlockState blockState, World world, BlockPos blockPos, class_5816 arg, @Nullable SoundEvent soundEvent) {
        int i;
        this.method_33604(blockState, world, blockPos, arg);
        if (soundEvent != null) {
            this.method_33601(world, blockPos, soundEvent);
        }
        if ((i = field_28662.getInt(arg)) != -1) {
            world.getBlockTickScheduler().schedule(blockPos, this, i);
        }
    }

    private void method_33610(BlockState blockState, World world, BlockPos blockPos) {
        this.method_33604(blockState, world, blockPos, class_5816.field_28718);
        this.method_33601(world, blockPos, SoundEvents.BLOCK_BIG_DRIPLEAF_TILT_UP);
    }

    private void method_33604(BlockState blockState, World world, BlockPos blockPos, class_5816 arg) {
        world.setBlockState(blockPos, (BlockState)blockState.with(field_28661, arg), 2);
        if (arg.method_33636()) {
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos);
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return class_5801.method_33613(state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.field_28666.get(state);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return (BlockState)((BlockState)this.getDefaultState().with(field_28660, fluidState.method_33659(Fluids.WATER))).with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(field_28660, FACING, field_28661);
    }
}

