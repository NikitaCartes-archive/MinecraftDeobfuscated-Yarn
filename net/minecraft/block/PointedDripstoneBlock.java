/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import com.google.common.annotations.VisibleForTesting;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LandingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.Thickness;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class PointedDripstoneBlock
extends Block
implements LandingBlock,
Waterloggable {
    public static final DirectionProperty VERTICAL_DIRECTION = Properties.VERTICAL_DIRECTION;
    public static final EnumProperty<Thickness> THICKNESS = Properties.THICKNESS;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private static final VoxelShape TIP_MERGE_SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
    private static final VoxelShape UP_TIP_SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 11.0, 11.0);
    private static final VoxelShape DOWN_TIP_SHAPE = Block.createCuboidShape(5.0, 5.0, 5.0, 11.0, 16.0, 11.0);
    private static final VoxelShape FRUSTUM_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
    private static final VoxelShape MIDDLE_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
    private static final VoxelShape BASE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    public PointedDripstoneBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(VERTICAL_DIRECTION, Direction.UP)).with(THICKNESS, Thickness.TIP)).with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(VERTICAL_DIRECTION, THICKNESS, WATERLOGGED);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return PointedDripstoneBlock.method_32781(world, pos, state.get(VERTICAL_DIRECTION));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        boolean bl;
        Thickness thickness;
        Direction direction2;
        if (direction != Direction.UP && direction != Direction.DOWN) {
            return state;
        }
        if (state.get(WATERLOGGED).booleanValue()) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (direction == (direction2 = state.get(VERTICAL_DIRECTION)).getOpposite() && !PointedDripstoneBlock.method_32781(world, pos, direction2)) {
            if (direction2 == Direction.DOWN) {
                this.method_32773(state, world, pos);
                return state;
            }
            world.breakBlock(pos, true);
            return state;
        }
        if (direction2 != PointedDripstoneBlock.method_32777(world, pos, direction2)) {
            world.breakBlock(pos, true);
        }
        if ((thickness = PointedDripstoneBlock.method_32770(world, pos, direction2, bl = state.get(THICKNESS) == Thickness.TIP_MERGE)) == null) {
            world.breakBlock(pos, true);
            return state;
        }
        return (BlockState)state.with(THICKNESS, thickness);
    }

    @Override
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        if (projectile instanceof TridentEntity && projectile.getVelocity().length() > 0.6) {
            world.breakBlock(hit.getBlockPos(), true);
        }
    }

    @Override
    public void onLandedUpon(World world, BlockPos pos, Entity entity, float distance) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.get(VERTICAL_DIRECTION) == Direction.UP && blockState.get(THICKNESS) == Thickness.TIP) {
            entity.handleFallDamage(distance * 4.0f, 1.0f);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        float f = random.nextFloat();
        if (f < 0.02f && PointedDripstoneBlock.method_32783(state)) {
            PointedDripstoneBlock.createParticle(world, pos, state);
            return;
        }
        if (f < 0.12f && PointedDripstoneBlock.method_32783(state) && PointedDripstoneBlock.isFluidStill(world, pos)) {
            PointedDripstoneBlock.createParticle(world, pos, state);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        PointedDripstoneBlock.method_32771(state, world, pos);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        PointedDripstoneBlock.method_32772(state, world, pos, random.nextFloat());
    }

    @VisibleForTesting
    public static void method_32772(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, float f) {
        if (f > 0.29296875f && f > 0.17578125f) {
            return;
        }
        if (!PointedDripstoneBlock.method_32778(blockState, serverWorld, blockPos)) {
            return;
        }
        Fluid fluid = PointedDripstoneBlock.getDripFluid(serverWorld, blockPos);
        if (fluid == Fluids.EMPTY) {
            return;
        }
        BlockPos blockPos2 = PointedDripstoneBlock.method_32782(blockState, serverWorld, blockPos);
        if (blockPos2 == null) {
            return;
        }
        BlockPos blockPos3 = null;
        if (fluid == Fluids.WATER && f < 0.29296875f || fluid == Fluids.LAVA && f < 0.17578125f) {
            blockPos3 = PointedDripstoneBlock.method_32769(serverWorld, blockPos, fluid);
        }
        if (blockPos3 == null) {
            return;
        }
        serverWorld.syncWorldEvent(1504, blockPos2, 0);
        int i = blockPos2.getY() - blockPos3.getY();
        int j = 50 + i;
        BlockState blockState2 = serverWorld.getBlockState(blockPos3);
        serverWorld.getBlockTickScheduler().schedule(blockPos3, blockState2.getBlock(), j);
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.DESTROY;
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction;
        BlockPos blockPos;
        World worldAccess = ctx.getWorld();
        Direction direction2 = PointedDripstoneBlock.method_32777(worldAccess, blockPos = ctx.getBlockPos(), direction = ctx.method_32760().getOpposite());
        if (direction2 == null) {
            return null;
        }
        boolean bl = !ctx.shouldCancelInteraction();
        Thickness thickness = PointedDripstoneBlock.method_32770(worldAccess, blockPos, direction2, bl);
        if (thickness == null) {
            return null;
        }
        return (BlockState)((BlockState)((BlockState)this.getDefaultState().with(VERTICAL_DIRECTION, direction2)).with(THICKNESS, thickness)).with(WATERLOGGED, worldAccess.getFluidState(blockPos).getFluid() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.get(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Thickness thickness = state.get(THICKNESS);
        VoxelShape voxelShape = thickness == Thickness.TIP_MERGE ? TIP_MERGE_SHAPE : (thickness == Thickness.TIP ? (state.get(VERTICAL_DIRECTION) == Direction.DOWN ? DOWN_TIP_SHAPE : UP_TIP_SHAPE) : (thickness == Thickness.FRUSTUM ? FRUSTUM_SHAPE : (thickness == Thickness.MIDDLE ? MIDDLE_SHAPE : BASE_SHAPE)));
        Vec3d vec3d = state.getModelOffset(world, pos);
        return voxelShape.offset(vec3d.x, 0.0, vec3d.z);
    }

    @Override
    public AbstractBlock.OffsetType getOffsetType() {
        return AbstractBlock.OffsetType.XZ;
    }

    @Override
    public void onDestroyedOnLanding(World world, BlockPos pos, FallingBlockEntity fallingBlockEntity) {
        if (!fallingBlockEntity.isSilent()) {
            world.syncWorldEvent(1045, pos, 0);
        }
    }

    private void method_32773(BlockState blockState, WorldAccess worldAccess, BlockPos blockPos) {
        BlockPos blockPos2 = PointedDripstoneBlock.method_32782(blockState, worldAccess, blockPos);
        if (blockPos2 == null) {
            return;
        }
        BlockPos.Mutable mutable = blockPos2.mutableCopy();
        while (PointedDripstoneBlock.isPointingDown(worldAccess.getBlockState(mutable))) {
            worldAccess.getBlockTickScheduler().schedule(mutable, this, 2);
            mutable.move(Direction.UP);
        }
    }

    private static void method_32771(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos) {
        Vec3d vec3d = Vec3d.ofBottomCenter(blockPos);
        FallingBlockEntity fallingBlockEntity = new FallingBlockEntity(serverWorld, vec3d.x, vec3d.y, vec3d.z, blockState);
        fallingBlockEntity.setHurtEntities(true);
        fallingBlockEntity.dropItem = true;
        serverWorld.spawnEntity(fallingBlockEntity);
    }

    @Environment(value=EnvType.CLIENT)
    public static void createParticle(World world, BlockPos pos, BlockState state) {
        DripType dripType = PointedDripstoneBlock.getDripType(world, pos, state);
        if (dripType == null) {
            return;
        }
        Vec3d vec3d = state.getModelOffset(world, pos);
        double d = 0.0625;
        double e = (double)pos.getX() + 0.5 + vec3d.x;
        double f = (double)((float)(pos.getY() + 1) - 0.6875f) - 0.0625;
        double g = (double)pos.getZ() + 0.5 + vec3d.z;
        world.addParticle(dripType.getParticle(), e, f, g, 0.0, 0.0, 0.0);
    }

    @Nullable
    private static BlockPos method_32782(BlockState blockState, WorldAccess worldAccess, BlockPos blockPos) {
        Direction direction = blockState.get(VERTICAL_DIRECTION);
        BlockPos.Mutable mutable = blockPos.mutableCopy();
        BlockState blockState2 = blockState;
        for (int i = 0; i < 50; ++i) {
            if (PointedDripstoneBlock.hasTipThickness(blockState2)) {
                return mutable;
            }
            if (!blockState2.isOf(Blocks.POINTED_DRIPSTONE) || blockState2.get(VERTICAL_DIRECTION) != direction) {
                return null;
            }
            mutable.move(direction);
            blockState2 = worldAccess.getBlockState(mutable);
        }
        return null;
    }

    @Nullable
    private static Direction method_32777(WorldView worldView, BlockPos blockPos, Direction direction) {
        Direction direction2;
        if (PointedDripstoneBlock.method_32781(worldView, blockPos, direction)) {
            direction2 = direction;
        } else if (PointedDripstoneBlock.method_32781(worldView, blockPos, direction.getOpposite())) {
            direction2 = direction.getOpposite();
        } else {
            return null;
        }
        return direction2;
    }

    @Nullable
    private static Thickness method_32770(WorldView worldView, BlockPos blockPos, Direction direction, boolean bl) {
        Direction direction2 = direction.getOpposite();
        BlockState blockState = worldView.getBlockState(blockPos.offset(direction));
        if (PointedDripstoneBlock.isPointedDripstone(blockState, direction2)) {
            if (bl || blockState.get(THICKNESS) == Thickness.TIP_MERGE) {
                return Thickness.TIP_MERGE;
            }
            return Thickness.TIP;
        }
        if (!PointedDripstoneBlock.isPointedDripstone(blockState, direction)) {
            return Thickness.TIP;
        }
        Thickness thickness = blockState.get(THICKNESS);
        if (thickness == Thickness.TIP || thickness == Thickness.TIP_MERGE) {
            return Thickness.FRUSTUM;
        }
        BlockState blockState2 = worldView.getBlockState(blockPos.offset(direction2));
        if (thickness == Thickness.FRUSTUM || thickness == Thickness.MIDDLE) {
            if (PointedDripstoneBlock.isPointedDripstone(blockState2, direction)) {
                return Thickness.MIDDLE;
            }
            return Thickness.BASE;
        }
        return null;
    }

    public static boolean method_32783(BlockState blockState) {
        return PointedDripstoneBlock.isPointingDown(blockState) && blockState.get(THICKNESS) == Thickness.TIP && blockState.get(WATERLOGGED) == false;
    }

    @Nullable
    private static BlockPos getSupportingPos(World world, BlockPos pos, BlockState state) {
        Direction direction = state.get(VERTICAL_DIRECTION).getOpposite();
        BlockPos.Mutable mutable = pos.mutableCopy();
        for (int i = 0; i < 50; ++i) {
            mutable.move(direction);
            BlockState blockState = world.getBlockState(mutable);
            if (blockState.isOf(Blocks.POINTED_DRIPSTONE)) continue;
            return mutable;
        }
        return null;
    }

    private static boolean method_32781(WorldView worldView, BlockPos blockPos, Direction direction) {
        BlockPos blockPos2 = blockPos.offset(direction.getOpposite());
        BlockState blockState = worldView.getBlockState(blockPos2);
        return Block.sideCoversSmallSquare(worldView, blockPos.offset(direction.getOpposite()), direction) || PointedDripstoneBlock.isPointedDripstone(blockState, direction);
    }

    private static boolean hasTipThickness(BlockState state) {
        if (!state.isOf(Blocks.POINTED_DRIPSTONE)) {
            return false;
        }
        Thickness thickness = state.get(THICKNESS);
        return thickness == Thickness.TIP || thickness == Thickness.TIP_MERGE;
    }

    private static boolean isPointingDown(BlockState state) {
        return PointedDripstoneBlock.isPointedDripstone(state, Direction.DOWN);
    }

    private static boolean method_32778(BlockState blockState, WorldView worldView, BlockPos blockPos) {
        return PointedDripstoneBlock.isPointingDown(blockState) && !worldView.getBlockState(blockPos.up()).isOf(Blocks.POINTED_DRIPSTONE);
    }

    private static boolean isPointedDripstone(BlockState state, Direction direction) {
        return state.isOf(Blocks.POINTED_DRIPSTONE) && state.get(VERTICAL_DIRECTION) == direction;
    }

    @Environment(value=EnvType.CLIENT)
    private static boolean isFluidStill(World world, BlockPos pos) {
        return world.getBlockState(pos.up(2)).getFluidState().isStill();
    }

    @Nullable
    private static BlockPos method_32769(World world, BlockPos blockPos, Fluid fluid) {
        BlockPos.Mutable mutable = blockPos.mutableCopy();
        for (int i = 0; i < 10; ++i) {
            AbstractCauldronBlock abstractCauldronBlock;
            mutable.move(Direction.DOWN);
            BlockState blockState = world.getBlockState(mutable);
            if (blockState.isAir()) continue;
            if (blockState.getBlock() instanceof AbstractCauldronBlock && (abstractCauldronBlock = (AbstractCauldronBlock)blockState.getBlock()).method_32765(fluid)) {
                return mutable;
            }
            return null;
        }
        return null;
    }

    @Nullable
    public static BlockPos method_32767(World world, BlockPos blockPos) {
        BlockPos.Mutable mutable = blockPos.mutableCopy();
        for (int i = 0; i < 10; ++i) {
            mutable.move(Direction.UP);
            BlockState blockState = world.getBlockState(mutable);
            if (blockState.isAir()) continue;
            if (PointedDripstoneBlock.method_32783(blockState)) {
                return mutable;
            }
            return null;
        }
        return null;
    }

    public static Fluid getDripFluid(World world, BlockPos pos) {
        DripType dripType = PointedDripstoneBlock.getDripType(world, pos, world.getBlockState(pos));
        if (dripType != null && dripType.isLiquid()) {
            return dripType.getFluid();
        }
        return Fluids.EMPTY;
    }

    @Nullable
    private static DripType getDripType(World world, BlockPos pos, BlockState state) {
        if (!PointedDripstoneBlock.isPointingDown(state)) {
            return null;
        }
        BlockPos blockPos = PointedDripstoneBlock.getSupportingPos(world, pos, state);
        if (blockPos == null) {
            return null;
        }
        FluidState fluidState = world.getFluidState(blockPos.up());
        return new DripType(fluidState.getFluid());
    }

    static class DripType {
        private final Fluid fluid;

        DripType(Fluid fluid) {
            this.fluid = fluid;
        }

        Fluid getFluid() {
            return this.fluid.isIn(FluidTags.LAVA) ? Fluids.LAVA : Fluids.WATER;
        }

        boolean isLiquid() {
            return this.fluid == Fluids.LAVA || this.fluid == Fluids.WATER;
        }

        @Environment(value=EnvType.CLIENT)
        ParticleEffect getParticle() {
            return this.getFluid() == Fluids.WATER ? ParticleTypes.DRIPPING_DRIPSTONE_WATER : ParticleTypes.DRIPPING_DRIPSTONE_LAVA;
        }
    }
}

