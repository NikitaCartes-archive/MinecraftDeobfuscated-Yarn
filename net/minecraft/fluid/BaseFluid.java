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
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class BaseFluid
extends Fluid {
    public static final BooleanProperty FALLING = Properties.FALLING;
    public static final IntProperty LEVEL = Properties.LEVEL_1_8;
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
        try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get();){
            for (Direction direction : Direction.Type.HORIZONTAL) {
                pooledMutable.set(pos).setOffset(direction);
                FluidState fluidState = world.getFluidState(pooledMutable);
                if (!this.method_15748(fluidState)) continue;
                float f = fluidState.getHeight();
                float g = 0.0f;
                if (f == 0.0f) {
                    Vec3i blockPos;
                    FluidState fluidState2;
                    if (!world.getBlockState(pooledMutable).getMaterial().blocksMovement() && this.method_15748(fluidState2 = world.getFluidState((BlockPos)(blockPos = pooledMutable.down()))) && (f = fluidState2.getHeight()) > 0.0f) {
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
                    pooledMutable.set(pos).setOffset(direction2);
                    if (!this.method_15749(world, pooledMutable, direction2) && !this.method_15749(world, pooledMutable.up(), direction2)) continue;
                    vec3d = vec3d.normalize().add(0.0, -6.0, 0.0);
                    break;
                }
            }
            Vec3d vec3d2 = vec3d.normalize();
            return vec3d2;
        }
    }

    private boolean method_15748(FluidState fluidState) {
        return fluidState.isEmpty() || fluidState.getFluid().matchesType(this);
    }

    protected boolean method_15749(BlockView blockView, BlockPos blockPos, Direction direction) {
        BlockState blockState = blockView.getBlockState(blockPos);
        FluidState fluidState = blockView.getFluidState(blockPos);
        if (fluidState.getFluid().matchesType(this)) {
            return false;
        }
        if (direction == Direction.UP) {
            return true;
        }
        if (blockState.getMaterial() == Material.ICE) {
            return false;
        }
        return blockState.isSideSolidFullSquare(blockView, blockPos, direction);
    }

    protected void tryFlow(IWorld world, BlockPos fluidPos, FluidState state) {
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

    private void method_15744(IWorld iWorld, BlockPos blockPos, FluidState fluidState, BlockState blockState) {
        int i = fluidState.getLevel() - this.getLevelDecreasePerBlock(iWorld);
        if (fluidState.get(FALLING).booleanValue()) {
            i = 7;
        }
        if (i <= 0) {
            return;
        }
        Map<Direction, FluidState> map = this.method_15726(iWorld, blockPos, blockState);
        for (Map.Entry<Direction, FluidState> entry : map.entrySet()) {
            BlockState blockState2;
            Direction direction = entry.getKey();
            FluidState fluidState2 = entry.getValue();
            BlockPos blockPos2 = blockPos.offset(direction);
            if (!this.canFlow(iWorld, blockPos, blockState, direction, blockPos2, blockState2 = iWorld.getBlockState(blockPos2), iWorld.getFluidState(blockPos2), fluidState2.getFluid())) continue;
            this.flow(iWorld, blockPos2, blockState2, direction, fluidState2);
        }
    }

    protected FluidState getUpdatedState(WorldView view, BlockPos pos, BlockState state) {
        BlockPos blockPos2;
        BlockState blockState3;
        FluidState fluidState3;
        int i = 0;
        int j = 0;
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos blockPos = pos.offset(direction);
            BlockState blockState = view.getBlockState(blockPos);
            FluidState fluidState = blockState.getFluidState();
            if (!fluidState.getFluid().matchesType(this) || !this.receivesFlow(direction, view, pos, state, blockPos, blockState)) continue;
            if (fluidState.isStill()) {
                ++j;
            }
            i = Math.max(i, fluidState.getLevel());
        }
        if (this.isInfinite() && j >= 2) {
            BlockState blockState2 = view.getBlockState(pos.down());
            FluidState fluidState2 = blockState2.getFluidState();
            if (blockState2.getMaterial().isSolid() || this.method_15752(fluidState2)) {
                return this.getStill(false);
            }
        }
        if (!(fluidState3 = (blockState3 = view.getBlockState(blockPos2 = pos.up())).getFluidState()).isEmpty() && fluidState3.getFluid().matchesType(this) && this.receivesFlow(Direction.UP, view, pos, state, blockPos2, blockState3)) {
            return this.getFlowing(8, true);
        }
        int k = i - this.getLevelDecreasePerBlock(view);
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

    protected void flow(IWorld world, BlockPos pos, BlockState state, Direction direction, FluidState fluidState) {
        if (state.getBlock() instanceof FluidFillable) {
            ((FluidFillable)((Object)state.getBlock())).tryFillWithFluid(world, pos, state, fluidState);
        } else {
            if (!state.isAir()) {
                this.beforeBreakingBlock(world, pos, state);
            }
            world.setBlockState(pos, fluidState.getBlockState(), 3);
        }
    }

    protected abstract void beforeBreakingBlock(IWorld var1, BlockPos var2, BlockState var3);

    private static short method_15747(BlockPos blockPos, BlockPos blockPos2) {
        int i = blockPos2.getX() - blockPos.getX();
        int j = blockPos2.getZ() - blockPos.getZ();
        return (short)((i + 128 & 0xFF) << 8 | j + 128 & 0xFF);
    }

    protected int method_15742(WorldView worldView, BlockPos blockPos, int i2, Direction direction, BlockState blockState, BlockPos blockPos2, Short2ObjectMap<Pair<BlockState, FluidState>> short2ObjectMap, Short2BooleanMap short2BooleanMap) {
        int j = 1000;
        for (Direction direction2 : Direction.Type.HORIZONTAL) {
            int k;
            if (direction2 == direction) continue;
            BlockPos blockPos3 = blockPos.offset(direction2);
            short s = BaseFluid.method_15747(blockPos2, blockPos3);
            Pair pair = short2ObjectMap.computeIfAbsent(s, i -> {
                BlockState blockState = worldView.getBlockState(blockPos3);
                return Pair.of(blockState, blockState.getFluidState());
            });
            BlockState blockState2 = (BlockState)pair.getFirst();
            FluidState fluidState = (FluidState)pair.getSecond();
            if (!this.method_15746(worldView, this.getFlowing(), blockPos, blockState, direction2, blockPos3, blockState2, fluidState)) continue;
            boolean bl = short2BooleanMap.computeIfAbsent(s, i -> {
                BlockPos blockPos2 = blockPos3.down();
                BlockState blockState2 = worldView.getBlockState(blockPos2);
                return this.method_15736(worldView, this.getFlowing(), blockPos3, blockState2, blockPos2, blockState2);
            });
            if (bl) {
                return i2;
            }
            if (i2 >= this.method_15733(worldView) || (k = this.method_15742(worldView, blockPos3, i2 + 1, direction2.getOpposite(), blockState2, blockPos2, short2ObjectMap, short2BooleanMap)) >= j) continue;
            j = k;
        }
        return j;
    }

    private boolean method_15736(BlockView blockView, Fluid fluid, BlockPos blockPos, BlockState blockState, BlockPos blockPos2, BlockState blockState2) {
        if (!this.receivesFlow(Direction.DOWN, blockView, blockPos, blockState, blockPos2, blockState2)) {
            return false;
        }
        if (blockState2.getFluidState().getFluid().matchesType(this)) {
            return true;
        }
        return this.method_15754(blockView, blockPos2, blockState2, fluid);
    }

    private boolean method_15746(BlockView blockView, Fluid fluid, BlockPos blockPos, BlockState blockState, Direction direction, BlockPos blockPos2, BlockState blockState2, FluidState fluidState) {
        return !this.method_15752(fluidState) && this.receivesFlow(direction, blockView, blockPos, blockState, blockPos2, blockState2) && this.method_15754(blockView, blockPos2, blockState2, fluid);
    }

    private boolean method_15752(FluidState fluidState) {
        return fluidState.getFluid().matchesType(this) && fluidState.isStill();
    }

    protected abstract int method_15733(WorldView var1);

    private int method_15740(WorldView worldView, BlockPos blockPos) {
        int i = 0;
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos blockPos2 = blockPos.offset(direction);
            FluidState fluidState = worldView.getFluidState(blockPos2);
            if (!this.method_15752(fluidState)) continue;
            ++i;
        }
        return i;
    }

    protected Map<Direction, FluidState> method_15726(WorldView worldView, BlockPos blockPos, BlockState blockState) {
        int i2 = 1000;
        EnumMap<Direction, FluidState> map = Maps.newEnumMap(Direction.class);
        Short2ObjectOpenHashMap<Pair<BlockState, FluidState>> short2ObjectMap = new Short2ObjectOpenHashMap<Pair<BlockState, FluidState>>();
        Short2BooleanOpenHashMap short2BooleanMap = new Short2BooleanOpenHashMap();
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos blockPos2 = blockPos.offset(direction);
            short s = BaseFluid.method_15747(blockPos, blockPos2);
            Pair pair = short2ObjectMap.computeIfAbsent(s, i -> {
                BlockState blockState = worldView.getBlockState(blockPos2);
                return Pair.of(blockState, blockState.getFluidState());
            });
            BlockState blockState2 = (BlockState)pair.getFirst();
            FluidState fluidState = (FluidState)pair.getSecond();
            FluidState fluidState2 = this.getUpdatedState(worldView, blockPos2, blockState2);
            if (!this.method_15746(worldView, fluidState2.getFluid(), blockPos, blockState, direction, blockPos2, blockState2, fluidState)) continue;
            BlockPos blockPos3 = blockPos2.down();
            boolean bl = short2BooleanMap.computeIfAbsent(s, i -> {
                BlockState blockState2 = worldView.getBlockState(blockPos3);
                return this.method_15736(worldView, this.getFlowing(), blockPos2, blockState2, blockPos3, blockState2);
            });
            int j = bl ? 0 : this.method_15742(worldView, blockPos2, 1, direction.getOpposite(), blockState2, blockPos, short2ObjectMap, short2BooleanMap);
            if (j < i2) {
                map.clear();
            }
            if (j > i2) continue;
            map.put(direction, fluidState2);
            i2 = j;
        }
        return map;
    }

    private boolean method_15754(BlockView blockView, BlockPos blockPos, BlockState blockState, Fluid fluid) {
        Block block = blockState.getBlock();
        if (block instanceof FluidFillable) {
            return ((FluidFillable)((Object)block)).canFillWithFluid(blockView, blockPos, blockState, fluid);
        }
        if (block instanceof DoorBlock || block.matches(BlockTags.SIGNS) || block == Blocks.LADDER || block == Blocks.SUGAR_CANE || block == Blocks.BUBBLE_COLUMN) {
            return false;
        }
        Material material = blockState.getMaterial();
        if (material == Material.PORTAL || material == Material.STRUCTURE_VOID || material == Material.UNDERWATER_PLANT || material == Material.SEAGRASS) {
            return false;
        }
        return !material.blocksMovement();
    }

    protected boolean canFlow(BlockView view, BlockPos fluidPos, BlockState fluidBlockState, Direction flowDirection, BlockPos flowTo, BlockState flowToBlockState, FluidState fluidState, Fluid fluid) {
        return fluidState.method_15764(view, flowTo, fluid, flowDirection) && this.receivesFlow(flowDirection, view, fluidPos, fluidBlockState, flowTo, flowToBlockState) && this.method_15754(view, flowTo, flowToBlockState, fluid);
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
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
            } else if (!fluidState.equals(state)) {
                state = fluidState;
                BlockState blockState = state.getBlockState();
                world.setBlockState(pos, blockState, 2);
                world.getFluidTickScheduler().schedule(pos, state.getFluid(), i);
                world.updateNeighborsAlways(pos, blockState.getBlock());
            }
        }
        this.tryFlow(world, pos, state);
    }

    protected static int method_15741(FluidState fluidState) {
        if (fluidState.isStill()) {
            return 0;
        }
        return 8 - Math.min(fluidState.getLevel(), 8) + (fluidState.get(FALLING) != false ? 8 : 0);
    }

    private static boolean isFluidAboveEqual(FluidState state, BlockView view, BlockPos pos) {
        return state.getFluid().matchesType(view.getFluidState(pos.up()).getFluid());
    }

    @Override
    public float getHeight(FluidState fluidState, BlockView blockView, BlockPos blockPos) {
        if (BaseFluid.isFluidAboveEqual(fluidState, blockView, blockPos)) {
            return 1.0f;
        }
        return fluidState.getHeight();
    }

    @Override
    public float getHeight(FluidState fluidState) {
        return (float)fluidState.getLevel() / 9.0f;
    }

    @Override
    public VoxelShape getShape(FluidState fluidState2, BlockView blockView, BlockPos blockPos) {
        if (fluidState2.getLevel() == 9 && BaseFluid.isFluidAboveEqual(fluidState2, blockView, blockPos)) {
            return VoxelShapes.fullCube();
        }
        return this.shapeCache.computeIfAbsent(fluidState2, fluidState -> VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, fluidState.getHeight(blockView, blockPos), 1.0));
    }
}

