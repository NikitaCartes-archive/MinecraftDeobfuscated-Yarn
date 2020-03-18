/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ObserverBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class RedstoneWireBlock
extends Block {
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_NORTH = Properties.NORTH_WIRE_CONNECTION;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_EAST = Properties.EAST_WIRE_CONNECTION;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_SOUTH = Properties.SOUTH_WIRE_CONNECTION;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_WEST = Properties.WEST_WIRE_CONNECTION;
    public static final IntProperty POWER = Properties.POWER;
    public static final Map<Direction, EnumProperty<WireConnection>> DIRECTION_TO_WIRE_CONNECTION_PROPERTY = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, WIRE_CONNECTION_NORTH, Direction.EAST, WIRE_CONNECTION_EAST, Direction.SOUTH, WIRE_CONNECTION_SOUTH, Direction.WEST, WIRE_CONNECTION_WEST));
    protected static final VoxelShape[] WIRE_CONNECTIONS_TO_SHAPE = new VoxelShape[]{Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 13.0), Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 16.0), Block.createCuboidShape(0.0, 0.0, 3.0, 13.0, 1.0, 13.0), Block.createCuboidShape(0.0, 0.0, 3.0, 13.0, 1.0, 16.0), Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 1.0, 13.0), Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 1.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 13.0, 1.0, 13.0), Block.createCuboidShape(0.0, 0.0, 0.0, 13.0, 1.0, 16.0), Block.createCuboidShape(3.0, 0.0, 3.0, 16.0, 1.0, 13.0), Block.createCuboidShape(3.0, 0.0, 3.0, 16.0, 1.0, 16.0), Block.createCuboidShape(0.0, 0.0, 3.0, 16.0, 1.0, 13.0), Block.createCuboidShape(0.0, 0.0, 3.0, 16.0, 1.0, 16.0), Block.createCuboidShape(3.0, 0.0, 0.0, 16.0, 1.0, 13.0), Block.createCuboidShape(3.0, 0.0, 0.0, 16.0, 1.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 13.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0)};
    private boolean wiresGivePower = true;
    private final Set<BlockPos> affectedNeighbors = Sets.newHashSet();

    public RedstoneWireBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(WIRE_CONNECTION_NORTH, WireConnection.NONE)).with(WIRE_CONNECTION_EAST, WireConnection.NONE)).with(WIRE_CONNECTION_SOUTH, WireConnection.NONE)).with(WIRE_CONNECTION_WEST, WireConnection.NONE)).with(POWER, 0));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return WIRE_CONNECTIONS_TO_SHAPE[RedstoneWireBlock.getWireConnectionMask(state)];
    }

    private static int getWireConnectionMask(BlockState state) {
        boolean bl4;
        int i = 0;
        boolean bl = state.get(WIRE_CONNECTION_NORTH) != WireConnection.NONE;
        boolean bl2 = state.get(WIRE_CONNECTION_EAST) != WireConnection.NONE;
        boolean bl3 = state.get(WIRE_CONNECTION_SOUTH) != WireConnection.NONE;
        boolean bl5 = bl4 = state.get(WIRE_CONNECTION_WEST) != WireConnection.NONE;
        if (bl || bl3 && !bl && !bl2 && !bl4) {
            i |= 1 << Direction.NORTH.getHorizontal();
        }
        if (bl2 || bl4 && !bl && !bl2 && !bl3) {
            i |= 1 << Direction.EAST.getHorizontal();
        }
        if (bl3 || bl && !bl2 && !bl3 && !bl4) {
            i |= 1 << Direction.SOUTH.getHorizontal();
        }
        if (bl4 || bl2 && !bl && !bl3 && !bl4) {
            i |= 1 << Direction.WEST.getHorizontal();
        }
        return i;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World blockView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        return (BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(WIRE_CONNECTION_WEST, this.getRenderConnectionType(blockView, blockPos, Direction.WEST))).with(WIRE_CONNECTION_EAST, this.getRenderConnectionType(blockView, blockPos, Direction.EAST))).with(WIRE_CONNECTION_NORTH, this.getRenderConnectionType(blockView, blockPos, Direction.NORTH))).with(WIRE_CONNECTION_SOUTH, this.getRenderConnectionType(blockView, blockPos, Direction.SOUTH));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, IWorld world, BlockPos pos, BlockPos posFrom) {
        if (direction == Direction.DOWN) {
            return state;
        }
        if (direction == Direction.UP) {
            return (BlockState)((BlockState)((BlockState)((BlockState)state.with(WIRE_CONNECTION_WEST, this.getRenderConnectionType(world, pos, Direction.WEST))).with(WIRE_CONNECTION_EAST, this.getRenderConnectionType(world, pos, Direction.EAST))).with(WIRE_CONNECTION_NORTH, this.getRenderConnectionType(world, pos, Direction.NORTH))).with(WIRE_CONNECTION_SOUTH, this.getRenderConnectionType(world, pos, Direction.SOUTH));
        }
        return (BlockState)state.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), this.getRenderConnectionType(world, pos, direction));
    }

    @Override
    public void prepare(BlockState state, IWorld world, BlockPos pos, int flags) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (Direction direction : Direction.Type.HORIZONTAL) {
            WireConnection wireConnection = (WireConnection)state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
            if (wireConnection == WireConnection.NONE || world.getBlockState(mutable.set(pos, direction)).getBlock() == this) continue;
            mutable.move(Direction.DOWN);
            BlockState blockState = world.getBlockState(mutable);
            if (blockState.getBlock() != Blocks.OBSERVER) {
                BlockPos blockPos = mutable.offset(direction.getOpposite());
                BlockState blockState2 = blockState.getStateForNeighborUpdate(direction.getOpposite(), world.getBlockState(blockPos), world, mutable, blockPos);
                RedstoneWireBlock.replaceBlock(blockState, blockState2, world, mutable, flags);
            }
            mutable.set(pos, direction).move(Direction.UP);
            BlockState blockState3 = world.getBlockState(mutable);
            if (blockState3.getBlock() == Blocks.OBSERVER) continue;
            BlockPos blockPos2 = mutable.offset(direction.getOpposite());
            BlockState blockState4 = blockState3.getStateForNeighborUpdate(direction.getOpposite(), world.getBlockState(blockPos2), world, mutable, blockPos2);
            RedstoneWireBlock.replaceBlock(blockState3, blockState4, world, mutable, flags);
        }
    }

    private WireConnection getRenderConnectionType(BlockView world, BlockPos pos, Direction dir) {
        BlockPos blockPos = pos.offset(dir);
        BlockState blockState = world.getBlockState(blockPos);
        BlockPos blockPos2 = pos.up();
        BlockState blockState2 = world.getBlockState(blockPos2);
        if (!blockState2.isSolidBlock(world, blockPos2)) {
            boolean bl;
            boolean bl2 = bl = blockState.isSideSolidFullSquare(world, blockPos, Direction.UP) || blockState.getBlock() == Blocks.HOPPER;
            if (bl && RedstoneWireBlock.connectsTo(world.getBlockState(blockPos.up()))) {
                if (blockState.isFullCube(world, blockPos)) {
                    return WireConnection.UP;
                }
                return WireConnection.SIDE;
            }
        }
        if (RedstoneWireBlock.connectsTo(blockState, dir) || !blockState.isSolidBlock(world, blockPos) && RedstoneWireBlock.connectsTo(world.getBlockState(blockPos.down()))) {
            return WireConnection.SIDE;
        }
        return WireConnection.NONE;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.isSideSolidFullSquare(world, blockPos, Direction.UP) || blockState.getBlock() == Blocks.HOPPER;
    }

    private BlockState update(World world, BlockPos pos, BlockState state) {
        state = this.updateLogic(world, pos, state);
        ArrayList<BlockPos> list = Lists.newArrayList(this.affectedNeighbors);
        this.affectedNeighbors.clear();
        for (BlockPos blockPos : list) {
            world.updateNeighborsAlways(blockPos, this);
        }
        return state;
    }

    private BlockState updateLogic(World world, BlockPos pos, BlockState state) {
        int l;
        BlockState blockState = state;
        int i = blockState.get(POWER);
        this.wiresGivePower = false;
        int j = world.getReceivedRedstonePower(pos);
        this.wiresGivePower = true;
        int k = 0;
        if (j < 15) {
            for (Direction direction : Direction.Type.HORIZONTAL) {
                BlockPos blockPos = pos.offset(direction);
                BlockState blockState2 = world.getBlockState(blockPos);
                k = this.increasePower(k, blockState2);
                BlockPos blockPos2 = pos.up();
                if (blockState2.isSolidBlock(world, blockPos) && !world.getBlockState(blockPos2).isSolidBlock(world, blockPos2)) {
                    k = this.increasePower(k, world.getBlockState(blockPos.up()));
                    continue;
                }
                if (blockState2.isSolidBlock(world, blockPos)) continue;
                k = this.increasePower(k, world.getBlockState(blockPos.down()));
            }
        }
        if (j > (l = k - 1)) {
            l = j;
        }
        if (i != l) {
            state = (BlockState)state.with(POWER, l);
            if (world.getBlockState(pos) == blockState) {
                world.setBlockState(pos, state, 2);
            }
            this.affectedNeighbors.add(pos);
            for (Direction direction2 : Direction.values()) {
                this.affectedNeighbors.add(pos.offset(direction2));
            }
        }
        return state;
    }

    private void updateNeighbors(World world, BlockPos pos) {
        if (world.getBlockState(pos).getBlock() != this) {
            return;
        }
        world.updateNeighborsAlways(pos, this);
        for (Direction direction : Direction.values()) {
            world.updateNeighborsAlways(pos.offset(direction), this);
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (oldState.getBlock() == state.getBlock() || world.isClient) {
            return;
        }
        this.update(world, pos, state);
        for (Direction direction : Direction.Type.VERTICAL) {
            world.updateNeighborsAlways(pos.offset(direction), this);
        }
        for (Direction direction : Direction.Type.HORIZONTAL) {
            this.updateNeighbors(world, pos.offset(direction));
        }
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos blockPos = pos.offset(direction);
            if (world.getBlockState(blockPos).isSolidBlock(world, blockPos)) {
                this.updateNeighbors(world, blockPos.up());
                continue;
            }
            this.updateNeighbors(world, blockPos.down());
        }
    }

    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean notify) {
        if (notify || state.getBlock() == newState.getBlock()) {
            return;
        }
        super.onBlockRemoved(state, world, pos, newState, notify);
        if (world.isClient) {
            return;
        }
        for (Direction direction : Direction.values()) {
            world.updateNeighborsAlways(pos.offset(direction), this);
        }
        this.update(world, pos, state);
        for (Direction direction2 : Direction.Type.HORIZONTAL) {
            this.updateNeighbors(world, pos.offset(direction2));
        }
        for (Direction direction2 : Direction.Type.HORIZONTAL) {
            BlockPos blockPos = pos.offset(direction2);
            if (world.getBlockState(blockPos).isSolidBlock(world, blockPos)) {
                this.updateNeighbors(world, blockPos.up());
                continue;
            }
            this.updateNeighbors(world, blockPos.down());
        }
    }

    private int increasePower(int power, BlockState state) {
        if (state.getBlock() != this) {
            return power;
        }
        int i = state.get(POWER);
        if (i > power) {
            return i;
        }
        return power;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (world.isClient) {
            return;
        }
        if (state.canPlaceAt(world, pos)) {
            this.update(world, pos, state);
        } else {
            RedstoneWireBlock.dropStacks(state, world, pos);
            world.removeBlock(pos, false);
        }
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (!this.wiresGivePower) {
            return 0;
        }
        return state.getWeakRedstonePower(world, pos, direction);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (!this.wiresGivePower) {
            return 0;
        }
        int i = state.get(POWER);
        if (i == 0) {
            return 0;
        }
        if (direction == Direction.UP) {
            return i;
        }
        EnumSet<Direction> enumSet = EnumSet.noneOf(Direction.class);
        for (Direction direction2 : Direction.Type.HORIZONTAL) {
            if (!this.couldConnectTo(world, pos, direction2)) continue;
            enumSet.add(direction2);
        }
        if (direction.getAxis().isHorizontal() && enumSet.isEmpty()) {
            return i;
        }
        if (enumSet.contains(direction) && !enumSet.contains(direction.rotateYCounterclockwise()) && !enumSet.contains(direction.rotateYClockwise())) {
            return i;
        }
        return 0;
    }

    private boolean couldConnectTo(BlockView world, BlockPos pos, Direction dir) {
        BlockPos blockPos = pos.offset(dir);
        BlockState blockState = world.getBlockState(blockPos);
        boolean bl = blockState.isSolidBlock(world, blockPos);
        BlockPos blockPos2 = pos.up();
        boolean bl2 = world.getBlockState(blockPos2).isSolidBlock(world, blockPos2);
        if (!bl2 && bl && RedstoneWireBlock.connectsTo(world, blockPos.up())) {
            return true;
        }
        if (RedstoneWireBlock.connectsTo(blockState, dir)) {
            return true;
        }
        if (blockState.getBlock() == Blocks.REPEATER && blockState.get(AbstractRedstoneGateBlock.POWERED).booleanValue() && blockState.get(AbstractRedstoneGateBlock.FACING) == dir) {
            return true;
        }
        return !bl && RedstoneWireBlock.connectsTo(world, blockPos.down());
    }

    protected static boolean connectsTo(BlockView world, BlockPos pos) {
        return RedstoneWireBlock.connectsTo(world.getBlockState(pos));
    }

    protected static boolean connectsTo(BlockState state) {
        return RedstoneWireBlock.connectsTo(state, null);
    }

    protected static boolean connectsTo(BlockState state, @Nullable Direction dir) {
        Block block = state.getBlock();
        if (block == Blocks.REDSTONE_WIRE) {
            return true;
        }
        if (state.getBlock() == Blocks.REPEATER) {
            Direction direction = state.get(RepeaterBlock.FACING);
            return direction == dir || direction.getOpposite() == dir;
        }
        if (Blocks.OBSERVER == state.getBlock()) {
            return dir == state.get(ObserverBlock.FACING);
        }
        return state.emitsRedstonePower() && dir != null;
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return this.wiresGivePower;
    }

    @Environment(value=EnvType.CLIENT)
    public static int getWireColor(int powerLevel) {
        float f = (float)powerLevel / 15.0f;
        float g = f * 0.6f + 0.4f;
        if (powerLevel == 0) {
            g = 0.3f;
        }
        float h = f * f * 0.7f - 0.5f;
        float i = f * f * 0.6f - 0.7f;
        if (h < 0.0f) {
            h = 0.0f;
        }
        if (i < 0.0f) {
            i = 0.0f;
        }
        int j = MathHelper.clamp((int)(g * 255.0f), 0, 255);
        int k = MathHelper.clamp((int)(h * 255.0f), 0, 255);
        int l = MathHelper.clamp((int)(i * 255.0f), 0, 255);
        return 0xFF000000 | j << 16 | k << 8 | l;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        int i = state.get(POWER);
        if (i == 0) {
            return;
        }
        double d = (double)pos.getX() + 0.5 + ((double)random.nextFloat() - 0.5) * 0.2;
        double e = (float)pos.getY() + 0.0625f;
        double f = (double)pos.getZ() + 0.5 + ((double)random.nextFloat() - 0.5) * 0.2;
        float g = (float)i / 15.0f;
        float h = g * 0.6f + 0.4f;
        float j = Math.max(0.0f, g * g * 0.7f - 0.5f);
        float k = Math.max(0.0f, g * g * 0.6f - 0.7f);
        world.addParticle(new DustParticleEffect(h, j, k, 1.0f), d, e, f, 0.0, 0.0, 0.0);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_SOUTH))).with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_WEST))).with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_NORTH))).with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_EAST));
            }
            case COUNTERCLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_EAST))).with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_SOUTH))).with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_WEST))).with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_NORTH));
            }
            case CLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_WEST))).with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_NORTH))).with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_EAST))).with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_SOUTH));
            }
        }
        return state;
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        switch (mirror) {
            case LEFT_RIGHT: {
                return (BlockState)((BlockState)state.with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_SOUTH))).with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_NORTH));
            }
            case FRONT_BACK: {
                return (BlockState)((BlockState)state.with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_WEST))).with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_EAST));
            }
        }
        return super.mirror(state, mirror);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WIRE_CONNECTION_NORTH, WIRE_CONNECTION_EAST, WIRE_CONNECTION_SOUTH, WIRE_CONNECTION_WEST, POWER);
    }
}

