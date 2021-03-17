/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.yarn.constants.SetBlockStateFlags;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ObserverBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
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
    private static final VoxelShape DOT_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 13.0);
    private static final Map<Direction, VoxelShape> field_24414 = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 1.0, 13.0), Direction.SOUTH, Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 16.0), Direction.EAST, Block.createCuboidShape(3.0, 0.0, 3.0, 16.0, 1.0, 13.0), Direction.WEST, Block.createCuboidShape(0.0, 0.0, 3.0, 13.0, 1.0, 13.0)));
    private static final Map<Direction, VoxelShape> field_24415 = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, VoxelShapes.union(field_24414.get(Direction.NORTH), Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 16.0, 1.0)), Direction.SOUTH, VoxelShapes.union(field_24414.get(Direction.SOUTH), Block.createCuboidShape(3.0, 0.0, 15.0, 13.0, 16.0, 16.0)), Direction.EAST, VoxelShapes.union(field_24414.get(Direction.EAST), Block.createCuboidShape(15.0, 0.0, 3.0, 16.0, 16.0, 13.0)), Direction.WEST, VoxelShapes.union(field_24414.get(Direction.WEST), Block.createCuboidShape(0.0, 0.0, 3.0, 1.0, 16.0, 13.0))));
    private static final Map<BlockState, VoxelShape> SHAPES = Maps.newHashMap();
    private static final Vec3d[] COLORS = Util.make(new Vec3d[16], vec3ds -> {
        for (int i = 0; i <= 15; ++i) {
            float f;
            float g = f * 0.6f + ((f = (float)i / 15.0f) > 0.0f ? 0.4f : 0.3f);
            float h = MathHelper.clamp(f * f * 0.7f - 0.5f, 0.0f, 1.0f);
            float j = MathHelper.clamp(f * f * 0.6f - 0.7f, 0.0f, 1.0f);
            vec3ds[i] = new Vec3d(g, h, j);
        }
    });
    private final BlockState dotState;
    private boolean wiresGivePower = true;

    public RedstoneWireBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(WIRE_CONNECTION_NORTH, WireConnection.NONE)).with(WIRE_CONNECTION_EAST, WireConnection.NONE)).with(WIRE_CONNECTION_SOUTH, WireConnection.NONE)).with(WIRE_CONNECTION_WEST, WireConnection.NONE)).with(POWER, 0));
        this.dotState = (BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(WIRE_CONNECTION_NORTH, WireConnection.SIDE)).with(WIRE_CONNECTION_EAST, WireConnection.SIDE)).with(WIRE_CONNECTION_SOUTH, WireConnection.SIDE)).with(WIRE_CONNECTION_WEST, WireConnection.SIDE);
        for (BlockState blockState : this.getStateManager().getStates()) {
            if (blockState.get(POWER) != 0) continue;
            SHAPES.put(blockState, this.getShapeForState(blockState));
        }
    }

    private VoxelShape getShapeForState(BlockState state) {
        VoxelShape voxelShape = DOT_SHAPE;
        for (Direction direction : Direction.Type.HORIZONTAL) {
            WireConnection wireConnection = (WireConnection)state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
            if (wireConnection == WireConnection.SIDE) {
                voxelShape = VoxelShapes.union(voxelShape, field_24414.get(direction));
                continue;
            }
            if (wireConnection != WireConnection.UP) continue;
            voxelShape = VoxelShapes.union(voxelShape, field_24415.get(direction));
        }
        return voxelShape;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES.get(state.with(POWER, 0));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getPlacementState(ctx.getWorld(), this.dotState, ctx.getBlockPos());
    }

    private BlockState getPlacementState(BlockView world, BlockState state, BlockPos pos) {
        boolean bl7;
        boolean bl = RedstoneWireBlock.isNotConnected(state);
        state = this.method_27843(world, (BlockState)this.getDefaultState().with(POWER, state.get(POWER)), pos);
        if (bl && RedstoneWireBlock.isNotConnected(state)) {
            return state;
        }
        boolean bl2 = state.get(WIRE_CONNECTION_NORTH).isConnected();
        boolean bl3 = state.get(WIRE_CONNECTION_SOUTH).isConnected();
        boolean bl4 = state.get(WIRE_CONNECTION_EAST).isConnected();
        boolean bl5 = state.get(WIRE_CONNECTION_WEST).isConnected();
        boolean bl6 = !bl2 && !bl3;
        boolean bl8 = bl7 = !bl4 && !bl5;
        if (!bl5 && bl6) {
            state = (BlockState)state.with(WIRE_CONNECTION_WEST, WireConnection.SIDE);
        }
        if (!bl4 && bl6) {
            state = (BlockState)state.with(WIRE_CONNECTION_EAST, WireConnection.SIDE);
        }
        if (!bl2 && bl7) {
            state = (BlockState)state.with(WIRE_CONNECTION_NORTH, WireConnection.SIDE);
        }
        if (!bl3 && bl7) {
            state = (BlockState)state.with(WIRE_CONNECTION_SOUTH, WireConnection.SIDE);
        }
        return state;
    }

    private BlockState method_27843(BlockView world, BlockState state, BlockPos pos) {
        boolean bl = !world.getBlockState(pos.up()).isSolidBlock(world, pos);
        for (Direction direction : Direction.Type.HORIZONTAL) {
            if (((WireConnection)state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction))).isConnected()) continue;
            WireConnection wireConnection = this.getRenderConnectionType(world, pos, direction, bl);
            state = (BlockState)state.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection);
        }
        return state;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN) {
            return state;
        }
        if (direction == Direction.UP) {
            return this.getPlacementState(world, state, pos);
        }
        WireConnection wireConnection = this.getRenderConnectionType(world, pos, direction);
        if (wireConnection.isConnected() == ((WireConnection)state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction))).isConnected() && !RedstoneWireBlock.isFullyConnected(state)) {
            return (BlockState)state.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection);
        }
        return this.getPlacementState(world, (BlockState)((BlockState)this.dotState.with(POWER, state.get(POWER))).with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection), pos);
    }

    private static boolean isFullyConnected(BlockState state) {
        return state.get(WIRE_CONNECTION_NORTH).isConnected() && state.get(WIRE_CONNECTION_SOUTH).isConnected() && state.get(WIRE_CONNECTION_EAST).isConnected() && state.get(WIRE_CONNECTION_WEST).isConnected();
    }

    private static boolean isNotConnected(BlockState state) {
        return !state.get(WIRE_CONNECTION_NORTH).isConnected() && !state.get(WIRE_CONNECTION_SOUTH).isConnected() && !state.get(WIRE_CONNECTION_EAST).isConnected() && !state.get(WIRE_CONNECTION_WEST).isConnected();
    }

    @Override
    public void prepare(BlockState state, WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (Direction direction : Direction.Type.HORIZONTAL) {
            WireConnection wireConnection = (WireConnection)state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
            if (wireConnection == WireConnection.NONE || world.getBlockState(mutable.set(pos, direction)).isOf(this)) continue;
            mutable.move(Direction.DOWN);
            BlockState blockState = world.getBlockState(mutable);
            if (!blockState.isOf(Blocks.OBSERVER)) {
                BlockPos blockPos = mutable.offset(direction.getOpposite());
                BlockState blockState2 = blockState.getStateForNeighborUpdate(direction.getOpposite(), world.getBlockState(blockPos), world, mutable, blockPos);
                RedstoneWireBlock.replace(blockState, blockState2, world, mutable, flags, maxUpdateDepth);
            }
            mutable.set(pos, direction).move(Direction.UP);
            BlockState blockState3 = world.getBlockState(mutable);
            if (blockState3.isOf(Blocks.OBSERVER)) continue;
            BlockPos blockPos2 = mutable.offset(direction.getOpposite());
            BlockState blockState4 = blockState3.getStateForNeighborUpdate(direction.getOpposite(), world.getBlockState(blockPos2), world, mutable, blockPos2);
            RedstoneWireBlock.replace(blockState3, blockState4, world, mutable, flags, maxUpdateDepth);
        }
    }

    private WireConnection getRenderConnectionType(BlockView world, BlockPos pos, Direction direction) {
        return this.getRenderConnectionType(world, pos, direction, !world.getBlockState(pos.up()).isSolidBlock(world, pos));
    }

    private WireConnection getRenderConnectionType(BlockView world, BlockPos pos, Direction direction, boolean bl) {
        boolean bl2;
        BlockPos blockPos = pos.offset(direction);
        BlockState blockState = world.getBlockState(blockPos);
        if (bl && (bl2 = this.canRunOnTop(world, blockPos, blockState)) && RedstoneWireBlock.connectsTo(world.getBlockState(blockPos.up()))) {
            if (blockState.isSideSolidFullSquare(world, blockPos, direction.getOpposite())) {
                return WireConnection.UP;
            }
            return WireConnection.SIDE;
        }
        if (RedstoneWireBlock.connectsTo(blockState, direction) || !blockState.isSolidBlock(world, blockPos) && RedstoneWireBlock.connectsTo(world.getBlockState(blockPos.down()))) {
            return WireConnection.SIDE;
        }
        return WireConnection.NONE;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        return this.canRunOnTop(world, blockPos, blockState);
    }

    private boolean canRunOnTop(BlockView world, BlockPos pos, BlockState floor) {
        return floor.isSideSolidFullSquare(world, pos, Direction.UP) || floor.isOf(Blocks.HOPPER);
    }

    private void update(World world, BlockPos pos, BlockState state) {
        int i = this.getReceivedRedstonePower(world, pos);
        if (state.get(POWER) != i) {
            if (world.getBlockState(pos) == state) {
                world.setBlockState(pos, (BlockState)state.with(POWER, i), SetBlockStateFlags.NOTIFY_LISTENERS);
            }
            HashSet<BlockPos> set = Sets.newHashSet();
            set.add(pos);
            for (Direction direction : Direction.values()) {
                set.add(pos.offset(direction));
            }
            for (BlockPos blockPos : set) {
                world.updateNeighborsAlways(blockPos, this);
            }
        }
    }

    private int getReceivedRedstonePower(World world, BlockPos pos) {
        this.wiresGivePower = false;
        int i = world.getReceivedRedstonePower(pos);
        this.wiresGivePower = true;
        int j = 0;
        if (i < 15) {
            for (Direction direction : Direction.Type.HORIZONTAL) {
                BlockPos blockPos = pos.offset(direction);
                BlockState blockState = world.getBlockState(blockPos);
                j = Math.max(j, this.increasePower(blockState));
                BlockPos blockPos2 = pos.up();
                if (blockState.isSolidBlock(world, blockPos) && !world.getBlockState(blockPos2).isSolidBlock(world, blockPos2)) {
                    j = Math.max(j, this.increasePower(world.getBlockState(blockPos.up())));
                    continue;
                }
                if (blockState.isSolidBlock(world, blockPos)) continue;
                j = Math.max(j, this.increasePower(world.getBlockState(blockPos.down())));
            }
        }
        return Math.max(i, j - 1);
    }

    private int increasePower(BlockState state) {
        return state.isOf(this) ? state.get(POWER) : 0;
    }

    private void updateNeighbors(World world, BlockPos pos) {
        if (!world.getBlockState(pos).isOf(this)) {
            return;
        }
        world.updateNeighborsAlways(pos, this);
        for (Direction direction : Direction.values()) {
            world.updateNeighborsAlways(pos.offset(direction), this);
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (oldState.isOf(state.getBlock()) || world.isClient) {
            return;
        }
        this.update(world, pos, state);
        for (Direction direction : Direction.Type.VERTICAL) {
            world.updateNeighborsAlways(pos.offset(direction), this);
        }
        this.updateOffsetNeighbors(world, pos);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved || state.isOf(newState.getBlock())) {
            return;
        }
        super.onStateReplaced(state, world, pos, newState, moved);
        if (world.isClient) {
            return;
        }
        for (Direction direction : Direction.values()) {
            world.updateNeighborsAlways(pos.offset(direction), this);
        }
        this.update(world, pos, state);
        this.updateOffsetNeighbors(world, pos);
    }

    private void updateOffsetNeighbors(World world, BlockPos pos) {
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
        if (!this.wiresGivePower || direction == Direction.DOWN) {
            return 0;
        }
        int i = state.get(POWER);
        if (i == 0) {
            return 0;
        }
        if (direction == Direction.UP || ((WireConnection)this.getPlacementState(world, state, pos).get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction.getOpposite()))).isConnected()) {
            return i;
        }
        return 0;
    }

    protected static boolean connectsTo(BlockState state) {
        return RedstoneWireBlock.connectsTo(state, null);
    }

    protected static boolean connectsTo(BlockState state, @Nullable Direction dir) {
        if (state.isOf(Blocks.REDSTONE_WIRE)) {
            return true;
        }
        if (state.isOf(Blocks.REPEATER)) {
            Direction direction = state.get(RepeaterBlock.FACING);
            return direction == dir || direction.getOpposite() == dir;
        }
        if (state.isOf(Blocks.OBSERVER)) {
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
        Vec3d vec3d = COLORS[powerLevel];
        return MathHelper.packRgb((float)vec3d.getX(), (float)vec3d.getY(), (float)vec3d.getZ());
    }

    @Environment(value=EnvType.CLIENT)
    private void addPoweredParticles(World world, Random random, BlockPos pos, Vec3d color, Direction direction, Direction direction2, float f, float g) {
        float h = g - f;
        if (random.nextFloat() >= 0.2f * h) {
            return;
        }
        float i = 0.4375f;
        float j = f + h * random.nextFloat();
        double d = 0.5 + (double)(0.4375f * (float)direction.getOffsetX()) + (double)(j * (float)direction2.getOffsetX());
        double e = 0.5 + (double)(0.4375f * (float)direction.getOffsetY()) + (double)(j * (float)direction2.getOffsetY());
        double k = 0.5 + (double)(0.4375f * (float)direction.getOffsetZ()) + (double)(j * (float)direction2.getOffsetZ());
        world.addParticle(new DustParticleEffect(new Vec3f(color), 1.0f), (double)pos.getX() + d, (double)pos.getY() + e, (double)pos.getZ() + k, 0.0, 0.0, 0.0);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        int i = state.get(POWER);
        if (i == 0) {
            return;
        }
        block4: for (Direction direction : Direction.Type.HORIZONTAL) {
            WireConnection wireConnection = (WireConnection)state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
            switch (wireConnection) {
                case UP: {
                    this.addPoweredParticles(world, random, pos, COLORS[i], direction, Direction.UP, -0.5f, 0.5f);
                }
                case SIDE: {
                    this.addPoweredParticles(world, random, pos, COLORS[i], Direction.DOWN, direction, 0.0f, 0.5f);
                    continue block4;
                }
            }
            this.addPoweredParticles(world, random, pos, COLORS[i], Direction.DOWN, direction, 0.0f, 0.3f);
        }
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

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.getAbilities().allowModifyWorld) {
            return ActionResult.PASS;
        }
        if (RedstoneWireBlock.isFullyConnected(state) || RedstoneWireBlock.isNotConnected(state)) {
            BlockState blockState = RedstoneWireBlock.isFullyConnected(state) ? this.getDefaultState() : this.dotState;
            blockState = (BlockState)blockState.with(POWER, state.get(POWER));
            if ((blockState = this.getPlacementState(world, blockState, pos)) != state) {
                world.setBlockState(pos, blockState, SetBlockStateFlags.DEFAULT);
                this.updateForNewState(world, pos, state, blockState);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    private void updateForNewState(World world, BlockPos pos, BlockState oldState, BlockState newState) {
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos blockPos = pos.offset(direction);
            if (((WireConnection)oldState.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction))).isConnected() == ((WireConnection)newState.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction))).isConnected() || !world.getBlockState(blockPos).isSolidBlock(world, blockPos)) continue;
            world.updateNeighborsExcept(blockPos, newState.getBlock(), direction.getOpposite());
        }
    }
}

