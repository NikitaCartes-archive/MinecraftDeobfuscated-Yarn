/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.fluid;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

/**
 * Represents a fluid which can flow.
 */
public abstract class FlowableFluid
extends Fluid {
    public static final BooleanProperty FALLING = Properties.FALLING;
    public static final IntProperty LEVEL = Properties.LEVEL_1_8;
    private static final int field_31726 = 200;
    private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.NeighborGroup>> field_15901 = ThreadLocal.withInitial(() -> {
        Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap = new Object2ByteLinkedOpenHashMap<Block.NeighborGroup>(200){

            @Override
            protected void rehash(int i) {
            }
        };
        object2ByteLinkedOpenHashMap.defaultReturnValue((byte)127);
        return object2ByteLinkedOpenHashMap;
    });
    private final Map<FluidState, VoxelShape> shapeCache = Maps.newIdentityHashMap();

    @Override
    protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
        builder.add(FALLING);
    }

    @Override
    public Vec3d getVelocity(BlockView world, BlockPos pos, FluidState state) {
        double d = 0.0;
        double e = 0.0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (Direction direction : Direction.Type.HORIZONTAL) {
            mutable.set((Vec3i)pos, direction);
            FluidState fluidState = world.getFluidState(mutable);
            if (!this.isEmptyOrThis(fluidState)) continue;
            float f = fluidState.getHeight();
            float g = 0.0f;
            if (f == 0.0f) {
                Vec3i blockPos;
                FluidState fluidState2;
                if (!world.getBlockState(mutable).getMaterial().blocksMovement() && this.isEmptyOrThis(fluidState2 = world.getFluidState((BlockPos)(blockPos = mutable.down()))) && (f = fluidState2.getHeight()) > 0.0f) {
                    g = state.getHeight() - (f - 0.8888889f);
                }
            } else if (f > 0.0f) {
                g = state.getHeight() - f;
            }
            if (g == 0.0f) continue;
            d += (double)((float)direction.getOffsetX() * g);
            e += (double)((float)direction.getOffsetZ() * g);
        }
        Vec3d vec3d = new Vec3d(d, 0.0, e);
        if (state.get(FALLING).booleanValue()) {
            for (Direction direction2 : Direction.Type.HORIZONTAL) {
                mutable.set((Vec3i)pos, direction2);
                if (!this.method_15749(world, mutable, direction2) && !this.method_15749(world, (BlockPos)mutable.up(), direction2)) continue;
                vec3d = vec3d.normalize().add(0.0, -6.0, 0.0);
                break;
            }
        }
        return vec3d.normalize();
    }

    private boolean isEmptyOrThis(FluidState state) {
        return state.isEmpty() || state.getFluid().matchesType(this);
    }

    protected boolean method_15749(BlockView world, BlockPos pos, Direction direction) {
        BlockState blockState = world.getBlockState(pos);
        FluidState fluidState = world.getFluidState(pos);
        if (fluidState.getFluid().matchesType(this)) {
            return false;
        }
        if (direction == Direction.UP) {
            return true;
        }
        if (blockState.getMaterial() == Material.ICE) {
            return false;
        }
        return blockState.isSideSolidFullSquare(world, pos, direction);
    }

    protected void tryFlow(WorldAccess world, BlockPos fluidPos, FluidState state) {
        if (state.isEmpty()) {
            return;
        }
        BlockState blockState = world.getBlockState(fluidPos);
        BlockPos blockPos = fluidPos.down();
        BlockState blockState2 = world.getBlockState(blockPos);
        FluidState fluidState = this.getUpdatedState(world, blockPos, blockState2);
        if (this.canFlow(world, fluidPos, blockState, Direction.DOWN, blockPos, blockState2, world.getFluidState(blockPos), fluidState.getFluid())) {
            this.flow(world, blockPos, blockState2, Direction.DOWN, fluidState);
            if (this.method_15740(world, fluidPos) >= 3) {
                this.method_15744(world, fluidPos, state, blockState);
            }
        } else if (state.isStill() || !this.method_15736(world, fluidState.getFluid(), fluidPos, blockState, blockPos, blockState2)) {
            this.method_15744(world, fluidPos, state, blockState);
        }
    }

    private void method_15744(WorldAccess world, BlockPos pos, FluidState fluidState, BlockState blockState) {
        int i = fluidState.getLevel() - this.getLevelDecreasePerBlock(world);
        if (fluidState.get(FALLING).booleanValue()) {
            i = 7;
        }
        if (i <= 0) {
            return;
        }
        Map<Direction, FluidState> map = this.getSpread(world, pos, blockState);
        for (Map.Entry<Direction, FluidState> entry : map.entrySet()) {
            BlockState blockState2;
            Direction direction = entry.getKey();
            FluidState fluidState2 = entry.getValue();
            BlockPos blockPos = pos.offset(direction);
            if (!this.canFlow(world, pos, blockState, direction, blockPos, blockState2 = world.getBlockState(blockPos), world.getFluidState(blockPos), fluidState2.getFluid())) continue;
            this.flow(world, blockPos, blockState2, direction, fluidState2);
        }
    }

    protected FluidState getUpdatedState(WorldView world, BlockPos pos, BlockState state) {
        BlockPos blockPos2;
        BlockState blockState3;
        FluidState fluidState3;
        int i = 0;
        int j = 0;
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos blockPos = pos.offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            FluidState fluidState = blockState.getFluidState();
            if (!fluidState.getFluid().matchesType(this) || !this.receivesFlow(direction, world, pos, state, blockPos, blockState)) continue;
            if (fluidState.isStill()) {
                ++j;
            }
            i = Math.max(i, fluidState.getLevel());
        }
        if (this.isInfinite() && j >= 2) {
            BlockState blockState2 = world.getBlockState(pos.down());
            FluidState fluidState2 = blockState2.getFluidState();
            if (blockState2.getMaterial().isSolid() || this.isMatchingAndStill(fluidState2)) {
                return this.getStill(false);
            }
        }
        if (!(fluidState3 = (blockState3 = world.getBlockState(blockPos2 = pos.up())).getFluidState()).isEmpty() && fluidState3.getFluid().matchesType(this) && this.receivesFlow(Direction.UP, world, pos, state, blockPos2, blockState3)) {
            return this.getFlowing(8, true);
        }
        int k = i - this.getLevelDecreasePerBlock(world);
        if (k <= 0) {
            return Fluids.EMPTY.getDefaultState();
        }
        return this.getFlowing(k, false);
    }

    private boolean receivesFlow(Direction face, BlockView world, BlockPos pos, BlockState state, BlockPos fromPos, BlockState fromState) {
        VoxelShape voxelShape2;
        VoxelShape voxelShape;
        boolean bl;
        Block.NeighborGroup neighborGroup;
        Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap = state.getBlock().hasDynamicBounds() || fromState.getBlock().hasDynamicBounds() ? null : field_15901.get();
        if (object2ByteLinkedOpenHashMap != null) {
            neighborGroup = new Block.NeighborGroup(state, fromState, face);
            byte b = object2ByteLinkedOpenHashMap.getAndMoveToFirst(neighborGroup);
            if (b != 127) {
                return b != 0;
            }
        } else {
            neighborGroup = null;
        }
        boolean bl2 = bl = !VoxelShapes.adjacentSidesCoverSquare(voxelShape = state.getCollisionShape(world, pos), voxelShape2 = fromState.getCollisionShape(world, fromPos), face);
        if (object2ByteLinkedOpenHashMap != null) {
            if (object2ByteLinkedOpenHashMap.size() == 200) {
                object2ByteLinkedOpenHashMap.removeLastByte();
            }
            object2ByteLinkedOpenHashMap.putAndMoveToFirst(neighborGroup, (byte)(bl ? 1 : 0));
        }
        return bl;
    }

    public abstract Fluid getFlowing();

    public FluidState getFlowing(int level, boolean falling) {
        return (FluidState)((FluidState)this.getFlowing().getDefaultState().with(LEVEL, level)).with(FALLING, falling);
    }

    public abstract Fluid getStill();

    public FluidState getStill(boolean falling) {
        return (FluidState)this.getStill().getDefaultState().with(FALLING, falling);
    }

    protected abstract boolean isInfinite();

    protected void flow(WorldAccess world, BlockPos pos, BlockState state, Direction direction, FluidState fluidState) {
        if (state.getBlock() instanceof FluidFillable) {
            ((FluidFillable)((Object)state.getBlock())).tryFillWithFluid(world, pos, state, fluidState);
        } else {
            if (!state.isAir()) {
                this.beforeBreakingBlock(world, pos, state);
            }
            world.setBlockState(pos, fluidState.getBlockState(), Block.NOTIFY_ALL);
        }
    }

    protected abstract void beforeBreakingBlock(WorldAccess var1, BlockPos var2, BlockState var3);

    private static short method_15747(BlockPos blockPos, BlockPos blockPos2) {
        int i = blockPos2.getX() - blockPos.getX();
        int j = blockPos2.getZ() - blockPos.getZ();
        return (short)((i + 128 & 0xFF) << 8 | j + 128 & 0xFF);
    }

    protected int method_15742(WorldView world, BlockPos blockPos, int i, Direction direction, BlockState blockState, BlockPos blockPos2, Short2ObjectMap<Pair<BlockState, FluidState>> short2ObjectMap, Short2BooleanMap short2BooleanMap) {
        int j = 1000;
        for (Direction direction2 : Direction.Type.HORIZONTAL) {
            int k;
            if (direction2 == direction) continue;
            BlockPos blockPos3 = blockPos.offset(direction2);
            short s2 = FlowableFluid.method_15747(blockPos2, blockPos3);
            Pair pair = short2ObjectMap.computeIfAbsent(s2, s -> {
                BlockState blockState = world.getBlockState(blockPos3);
                return Pair.of(blockState, blockState.getFluidState());
            });
            BlockState blockState2 = (BlockState)pair.getFirst();
            FluidState fluidState = (FluidState)pair.getSecond();
            if (!this.canFlowThrough(world, this.getFlowing(), blockPos, blockState, direction2, blockPos3, blockState2, fluidState)) continue;
            boolean bl = short2BooleanMap.computeIfAbsent(s2, s -> {
                BlockPos blockPos2 = blockPos3.down();
                BlockState blockState2 = world.getBlockState(blockPos2);
                return this.method_15736(world, this.getFlowing(), blockPos3, blockState2, blockPos2, blockState2);
            });
            if (bl) {
                return i;
            }
            if (i >= this.getFlowSpeed(world) || (k = this.method_15742(world, blockPos3, i + 1, direction2.getOpposite(), blockState2, blockPos2, short2ObjectMap, short2BooleanMap)) >= j) continue;
            j = k;
        }
        return j;
    }

    private boolean method_15736(BlockView world, Fluid fluid, BlockPos pos, BlockState state, BlockPos fromPos, BlockState fromState) {
        if (!this.receivesFlow(Direction.DOWN, world, pos, state, fromPos, fromState)) {
            return false;
        }
        if (fromState.getFluidState().getFluid().matchesType(this)) {
            return true;
        }
        return this.canFill(world, fromPos, fromState, fluid);
    }

    private boolean canFlowThrough(BlockView world, Fluid fluid, BlockPos pos, BlockState state, Direction face, BlockPos fromPos, BlockState fromState, FluidState fluidState) {
        return !this.isMatchingAndStill(fluidState) && this.receivesFlow(face, world, pos, state, fromPos, fromState) && this.canFill(world, fromPos, fromState, fluid);
    }

    private boolean isMatchingAndStill(FluidState state) {
        return state.getFluid().matchesType(this) && state.isStill();
    }

    protected abstract int getFlowSpeed(WorldView var1);

    private int method_15740(WorldView world, BlockPos pos) {
        int i = 0;
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos blockPos = pos.offset(direction);
            FluidState fluidState = world.getFluidState(blockPos);
            if (!this.isMatchingAndStill(fluidState)) continue;
            ++i;
        }
        return i;
    }

    protected Map<Direction, FluidState> getSpread(WorldView world, BlockPos pos, BlockState state) {
        int i = 1000;
        EnumMap<Direction, FluidState> map = Maps.newEnumMap(Direction.class);
        Short2ObjectOpenHashMap<Pair<BlockState, FluidState>> short2ObjectMap = new Short2ObjectOpenHashMap<Pair<BlockState, FluidState>>();
        Short2BooleanOpenHashMap short2BooleanMap = new Short2BooleanOpenHashMap();
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos blockPos = pos.offset(direction);
            short s2 = FlowableFluid.method_15747(pos, blockPos);
            Pair pair = short2ObjectMap.computeIfAbsent(s2, s -> {
                BlockState blockState = world.getBlockState(blockPos);
                return Pair.of(blockState, blockState.getFluidState());
            });
            BlockState blockState = (BlockState)pair.getFirst();
            FluidState fluidState = (FluidState)pair.getSecond();
            FluidState fluidState2 = this.getUpdatedState(world, blockPos, blockState);
            if (!this.canFlowThrough(world, fluidState2.getFluid(), pos, state, direction, blockPos, blockState, fluidState)) continue;
            BlockPos blockPos2 = blockPos.down();
            boolean bl = short2BooleanMap.computeIfAbsent(s2, s -> {
                BlockState blockState2 = world.getBlockState(blockPos2);
                return this.method_15736(world, this.getFlowing(), blockPos, blockState, blockPos2, blockState2);
            });
            int j = bl ? 0 : this.method_15742(world, blockPos, 1, direction.getOpposite(), blockState, pos, short2ObjectMap, short2BooleanMap);
            if (j < i) {
                map.clear();
            }
            if (j > i) continue;
            map.put(direction, fluidState2);
            i = j;
        }
        return map;
    }

    private boolean canFill(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        Block block = state.getBlock();
        if (block instanceof FluidFillable) {
            return ((FluidFillable)((Object)block)).canFillWithFluid(world, pos, state, fluid);
        }
        if (block instanceof DoorBlock || state.isIn(BlockTags.SIGNS) || state.isOf(Blocks.LADDER) || state.isOf(Blocks.SUGAR_CANE) || state.isOf(Blocks.BUBBLE_COLUMN)) {
            return false;
        }
        Material material = state.getMaterial();
        if (material == Material.PORTAL || material == Material.STRUCTURE_VOID || material == Material.UNDERWATER_PLANT || material == Material.REPLACEABLE_UNDERWATER_PLANT) {
            return false;
        }
        return !material.blocksMovement();
    }

    protected boolean canFlow(BlockView world, BlockPos fluidPos, BlockState fluidBlockState, Direction flowDirection, BlockPos flowTo, BlockState flowToBlockState, FluidState fluidState, Fluid fluid) {
        return fluidState.canBeReplacedWith(world, flowTo, fluid, flowDirection) && this.receivesFlow(flowDirection, world, fluidPos, fluidBlockState, flowTo, flowToBlockState) && this.canFill(world, flowTo, flowToBlockState, fluid);
    }

    protected abstract int getLevelDecreasePerBlock(WorldView var1);

    protected int getNextTickDelay(World world, BlockPos pos, FluidState oldState, FluidState newState) {
        return this.getTickRate(world);
    }

    @Override
    public void onScheduledTick(World world, BlockPos pos, FluidState state) {
        if (!state.isStill()) {
            FluidState fluidState = this.getUpdatedState(world, pos, world.getBlockState(pos));
            int i = this.getNextTickDelay(world, pos, state, fluidState);
            if (fluidState.isEmpty()) {
                state = fluidState;
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
            } else if (!fluidState.equals(state)) {
                state = fluidState;
                BlockState blockState = state.getBlockState();
                world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
                world.createAndScheduleFluidTick(pos, state.getFluid(), i);
                world.updateNeighborsAlways(pos, blockState.getBlock());
            }
        }
        this.tryFlow(world, pos, state);
    }

    protected static int getBlockStateLevel(FluidState state) {
        if (state.isStill()) {
            return 0;
        }
        return 8 - Math.min(state.getLevel(), 8) + (state.get(FALLING) != false ? 8 : 0);
    }

    private static boolean isFluidAboveEqual(FluidState state, BlockView world, BlockPos pos) {
        return state.getFluid().matchesType(world.getFluidState(pos.up()).getFluid());
    }

    @Override
    public float getHeight(FluidState state, BlockView world, BlockPos pos) {
        if (FlowableFluid.isFluidAboveEqual(state, world, pos)) {
            return 1.0f;
        }
        return state.getHeight();
    }

    @Override
    public float getHeight(FluidState state) {
        return (float)state.getLevel() / 9.0f;
    }

    @Override
    public abstract int getLevel(FluidState var1);

    @Override
    public VoxelShape getShape(FluidState state, BlockView world, BlockPos pos) {
        if (state.getLevel() == 9 && FlowableFluid.isFluidAboveEqual(state, world, pos)) {
            return VoxelShapes.fullCube();
        }
        return this.shapeCache.computeIfAbsent(state, fluidState -> VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, fluidState.getHeight(world, pos), 1.0));
    }
}

