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
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ObserverBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.entity.EntityContext;
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

    public RedstoneWireBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateFactory.getDefaultState()).with(WIRE_CONNECTION_NORTH, WireConnection.NONE)).with(WIRE_CONNECTION_EAST, WireConnection.NONE)).with(WIRE_CONNECTION_SOUTH, WireConnection.NONE)).with(WIRE_CONNECTION_WEST, WireConnection.NONE)).with(POWER, 0));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        return WIRE_CONNECTIONS_TO_SHAPE[RedstoneWireBlock.getWireConnectionMask(blockState)];
    }

    private static int getWireConnectionMask(BlockState blockState) {
        boolean bl4;
        int i = 0;
        boolean bl = blockState.get(WIRE_CONNECTION_NORTH) != WireConnection.NONE;
        boolean bl2 = blockState.get(WIRE_CONNECTION_EAST) != WireConnection.NONE;
        boolean bl3 = blockState.get(WIRE_CONNECTION_SOUTH) != WireConnection.NONE;
        boolean bl5 = bl4 = blockState.get(WIRE_CONNECTION_WEST) != WireConnection.NONE;
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
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        World blockView = itemPlacementContext.getWorld();
        BlockPos blockPos = itemPlacementContext.getBlockPos();
        return (BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(WIRE_CONNECTION_WEST, this.getRenderConnectionType(blockView, blockPos, Direction.WEST))).with(WIRE_CONNECTION_EAST, this.getRenderConnectionType(blockView, blockPos, Direction.EAST))).with(WIRE_CONNECTION_NORTH, this.getRenderConnectionType(blockView, blockPos, Direction.NORTH))).with(WIRE_CONNECTION_SOUTH, this.getRenderConnectionType(blockView, blockPos, Direction.SOUTH));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
        if (direction == Direction.DOWN) {
            return blockState;
        }
        if (direction == Direction.UP) {
            return (BlockState)((BlockState)((BlockState)((BlockState)blockState.with(WIRE_CONNECTION_WEST, this.getRenderConnectionType(iWorld, blockPos, Direction.WEST))).with(WIRE_CONNECTION_EAST, this.getRenderConnectionType(iWorld, blockPos, Direction.EAST))).with(WIRE_CONNECTION_NORTH, this.getRenderConnectionType(iWorld, blockPos, Direction.NORTH))).with(WIRE_CONNECTION_SOUTH, this.getRenderConnectionType(iWorld, blockPos, Direction.SOUTH));
        }
        return (BlockState)blockState.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), this.getRenderConnectionType(iWorld, blockPos, direction));
    }

    @Override
    public void method_9517(BlockState blockState, IWorld iWorld, BlockPos blockPos, int i) {
        try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get();){
            for (Direction direction : Direction.Type.HORIZONTAL) {
                WireConnection wireConnection = (WireConnection)blockState.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
                if (wireConnection == WireConnection.NONE || iWorld.getBlockState(pooledMutable.method_10114(blockPos).method_10118(direction)).getBlock() == this) continue;
                pooledMutable.method_10118(Direction.DOWN);
                BlockState blockState2 = iWorld.getBlockState(pooledMutable);
                if (blockState2.getBlock() != Blocks.OBSERVER) {
                    BlockPos blockPos2 = pooledMutable.offset(direction.getOpposite());
                    BlockState blockState3 = blockState2.getStateForNeighborUpdate(direction.getOpposite(), iWorld.getBlockState(blockPos2), iWorld, pooledMutable, blockPos2);
                    RedstoneWireBlock.replaceBlock(blockState2, blockState3, iWorld, pooledMutable, i);
                }
                pooledMutable.method_10114(blockPos).method_10118(direction).method_10118(Direction.UP);
                BlockState blockState4 = iWorld.getBlockState(pooledMutable);
                if (blockState4.getBlock() == Blocks.OBSERVER) continue;
                BlockPos blockPos3 = pooledMutable.offset(direction.getOpposite());
                BlockState blockState5 = blockState4.getStateForNeighborUpdate(direction.getOpposite(), iWorld.getBlockState(blockPos3), iWorld, pooledMutable, blockPos3);
                RedstoneWireBlock.replaceBlock(blockState4, blockState5, iWorld, pooledMutable, i);
            }
        }
    }

    private WireConnection getRenderConnectionType(BlockView blockView, BlockPos blockPos, Direction direction) {
        BlockPos blockPos2 = blockPos.offset(direction);
        BlockState blockState = blockView.getBlockState(blockPos2);
        BlockPos blockPos3 = blockPos.up();
        BlockState blockState2 = blockView.getBlockState(blockPos3);
        if (!blockState2.isSimpleFullBlock(blockView, blockPos3)) {
            boolean bl;
            boolean bl2 = bl = blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.UP) || blockState.getBlock() == Blocks.HOPPER;
            if (bl && RedstoneWireBlock.connectsTo(blockView.getBlockState(blockPos2.up()))) {
                if (blockState.isFullCube(blockView, blockPos2)) {
                    return WireConnection.UP;
                }
                return WireConnection.SIDE;
            }
        }
        if (RedstoneWireBlock.connectsTo(blockState, direction) || !blockState.isSimpleFullBlock(blockView, blockPos2) && RedstoneWireBlock.connectsTo(blockView.getBlockState(blockPos2.method_10074()))) {
            return WireConnection.SIDE;
        }
        return WireConnection.NONE;
    }

    @Override
    public boolean canPlaceAt(BlockState blockState, WorldView worldView, BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.method_10074();
        BlockState blockState2 = worldView.getBlockState(blockPos2);
        return blockState2.isSideSolidFullSquare(worldView, blockPos2, Direction.UP) || blockState2.getBlock() == Blocks.HOPPER;
    }

    private BlockState update(World world, BlockPos blockPos, BlockState blockState) {
        blockState = this.updateLogic(world, blockPos, blockState);
        ArrayList<BlockPos> list = Lists.newArrayList(this.affectedNeighbors);
        this.affectedNeighbors.clear();
        for (BlockPos blockPos2 : list) {
            world.updateNeighborsAlways(blockPos2, this);
        }
        return blockState;
    }

    private BlockState updateLogic(World world, BlockPos blockPos, BlockState blockState) {
        int l;
        BlockState blockState2 = blockState;
        int i = blockState2.get(POWER);
        this.wiresGivePower = false;
        int j = world.getReceivedRedstonePower(blockPos);
        this.wiresGivePower = true;
        int k = 0;
        if (j < 15) {
            for (Direction direction : Direction.Type.HORIZONTAL) {
                BlockPos blockPos2 = blockPos.offset(direction);
                BlockState blockState3 = world.getBlockState(blockPos2);
                k = this.increasePower(k, blockState3);
                BlockPos blockPos3 = blockPos.up();
                if (blockState3.isSimpleFullBlock(world, blockPos2) && !world.getBlockState(blockPos3).isSimpleFullBlock(world, blockPos3)) {
                    k = this.increasePower(k, world.getBlockState(blockPos2.up()));
                    continue;
                }
                if (blockState3.isSimpleFullBlock(world, blockPos2)) continue;
                k = this.increasePower(k, world.getBlockState(blockPos2.method_10074()));
            }
        }
        if (j > (l = k - 1)) {
            l = j;
        }
        if (i != l) {
            blockState = (BlockState)blockState.with(POWER, l);
            if (world.getBlockState(blockPos) == blockState2) {
                world.setBlockState(blockPos, blockState, 2);
            }
            this.affectedNeighbors.add(blockPos);
            for (Direction direction2 : Direction.values()) {
                this.affectedNeighbors.add(blockPos.offset(direction2));
            }
        }
        return blockState;
    }

    private void updateNeighbors(World world, BlockPos blockPos) {
        if (world.getBlockState(blockPos).getBlock() != this) {
            return;
        }
        world.updateNeighborsAlways(blockPos, this);
        for (Direction direction : Direction.values()) {
            world.updateNeighborsAlways(blockPos.offset(direction), this);
        }
    }

    @Override
    public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState2.getBlock() == blockState.getBlock() || world.isClient) {
            return;
        }
        this.update(world, blockPos, blockState);
        for (Direction direction : Direction.Type.VERTICAL) {
            world.updateNeighborsAlways(blockPos.offset(direction), this);
        }
        for (Direction direction : Direction.Type.HORIZONTAL) {
            this.updateNeighbors(world, blockPos.offset(direction));
        }
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos blockPos2 = blockPos.offset(direction);
            if (world.getBlockState(blockPos2).isSimpleFullBlock(world, blockPos2)) {
                this.updateNeighbors(world, blockPos2.up());
                continue;
            }
            this.updateNeighbors(world, blockPos2.method_10074());
        }
    }

    @Override
    public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (bl || blockState.getBlock() == blockState2.getBlock()) {
            return;
        }
        super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
        if (world.isClient) {
            return;
        }
        for (Direction direction : Direction.values()) {
            world.updateNeighborsAlways(blockPos.offset(direction), this);
        }
        this.update(world, blockPos, blockState);
        for (Direction direction2 : Direction.Type.HORIZONTAL) {
            this.updateNeighbors(world, blockPos.offset(direction2));
        }
        for (Direction direction2 : Direction.Type.HORIZONTAL) {
            BlockPos blockPos2 = blockPos.offset(direction2);
            if (world.getBlockState(blockPos2).isSimpleFullBlock(world, blockPos2)) {
                this.updateNeighbors(world, blockPos2.up());
                continue;
            }
            this.updateNeighbors(world, blockPos2.method_10074());
        }
    }

    private int increasePower(int i, BlockState blockState) {
        if (blockState.getBlock() != this) {
            return i;
        }
        int j = blockState.get(POWER);
        if (j > i) {
            return j;
        }
        return i;
    }

    @Override
    public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        if (world.isClient) {
            return;
        }
        if (blockState.canPlaceAt(world, blockPos)) {
            this.update(world, blockPos, blockState);
        } else {
            RedstoneWireBlock.dropStacks(blockState, world, blockPos);
            world.removeBlock(blockPos, false);
        }
    }

    @Override
    public int getStrongRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
        if (!this.wiresGivePower) {
            return 0;
        }
        return blockState.getWeakRedstonePower(blockView, blockPos, direction);
    }

    @Override
    public int getWeakRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
        if (!this.wiresGivePower) {
            return 0;
        }
        int i = blockState.get(POWER);
        if (i == 0) {
            return 0;
        }
        if (direction == Direction.UP) {
            return i;
        }
        EnumSet<Direction> enumSet = EnumSet.noneOf(Direction.class);
        for (Direction direction2 : Direction.Type.HORIZONTAL) {
            if (!this.couldConnectTo(blockView, blockPos, direction2)) continue;
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

    private boolean couldConnectTo(BlockView blockView, BlockPos blockPos, Direction direction) {
        BlockPos blockPos2 = blockPos.offset(direction);
        BlockState blockState = blockView.getBlockState(blockPos2);
        boolean bl = blockState.isSimpleFullBlock(blockView, blockPos2);
        BlockPos blockPos3 = blockPos.up();
        boolean bl2 = blockView.getBlockState(blockPos3).isSimpleFullBlock(blockView, blockPos3);
        if (!bl2 && bl && RedstoneWireBlock.connectsTo(blockView, blockPos2.up())) {
            return true;
        }
        if (RedstoneWireBlock.connectsTo(blockState, direction)) {
            return true;
        }
        if (blockState.getBlock() == Blocks.REPEATER && blockState.get(AbstractRedstoneGateBlock.POWERED).booleanValue() && blockState.get(AbstractRedstoneGateBlock.FACING) == direction) {
            return true;
        }
        return !bl && RedstoneWireBlock.connectsTo(blockView, blockPos2.method_10074());
    }

    protected static boolean connectsTo(BlockView blockView, BlockPos blockPos) {
        return RedstoneWireBlock.connectsTo(blockView.getBlockState(blockPos));
    }

    protected static boolean connectsTo(BlockState blockState) {
        return RedstoneWireBlock.connectsTo(blockState, null);
    }

    protected static boolean connectsTo(BlockState blockState, @Nullable Direction direction) {
        Block block = blockState.getBlock();
        if (block == Blocks.REDSTONE_WIRE) {
            return true;
        }
        if (blockState.getBlock() == Blocks.REPEATER) {
            Direction direction2 = blockState.get(RepeaterBlock.FACING);
            return direction2 == direction || direction2.getOpposite() == direction;
        }
        if (Blocks.OBSERVER == blockState.getBlock()) {
            return direction == blockState.get(ObserverBlock.FACING);
        }
        return blockState.emitsRedstonePower() && direction != null;
    }

    @Override
    public boolean emitsRedstonePower(BlockState blockState) {
        return this.wiresGivePower;
    }

    @Environment(value=EnvType.CLIENT)
    public static int getWireColor(int i) {
        float f = (float)i / 15.0f;
        float g = f * 0.6f + 0.4f;
        if (i == 0) {
            g = 0.3f;
        }
        float h = f * f * 0.7f - 0.5f;
        float j = f * f * 0.6f - 0.7f;
        if (h < 0.0f) {
            h = 0.0f;
        }
        if (j < 0.0f) {
            j = 0.0f;
        }
        int k = MathHelper.clamp((int)(g * 255.0f), 0, 255);
        int l = MathHelper.clamp((int)(h * 255.0f), 0, 255);
        int m = MathHelper.clamp((int)(j * 255.0f), 0, 255);
        return 0xFF000000 | k << 16 | l << 8 | m;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        int i = blockState.get(POWER);
        if (i == 0) {
            return;
        }
        double d = (double)blockPos.getX() + 0.5 + ((double)random.nextFloat() - 0.5) * 0.2;
        double e = (float)blockPos.getY() + 0.0625f;
        double f = (double)blockPos.getZ() + 0.5 + ((double)random.nextFloat() - 0.5) * 0.2;
        float g = (float)i / 15.0f;
        float h = g * 0.6f + 0.4f;
        float j = Math.max(0.0f, g * g * 0.7f - 0.5f);
        float k = Math.max(0.0f, g * g * 0.6f - 0.7f);
        world.addParticle(new DustParticleEffect(h, j, k, 1.0f), d, e, f, 0.0, 0.0, 0.0);
    }

    @Override
    public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
        switch (blockRotation) {
            case CLOCKWISE_180: {
                return (BlockState)((BlockState)((BlockState)((BlockState)blockState.with(WIRE_CONNECTION_NORTH, blockState.get(WIRE_CONNECTION_SOUTH))).with(WIRE_CONNECTION_EAST, blockState.get(WIRE_CONNECTION_WEST))).with(WIRE_CONNECTION_SOUTH, blockState.get(WIRE_CONNECTION_NORTH))).with(WIRE_CONNECTION_WEST, blockState.get(WIRE_CONNECTION_EAST));
            }
            case COUNTERCLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)blockState.with(WIRE_CONNECTION_NORTH, blockState.get(WIRE_CONNECTION_EAST))).with(WIRE_CONNECTION_EAST, blockState.get(WIRE_CONNECTION_SOUTH))).with(WIRE_CONNECTION_SOUTH, blockState.get(WIRE_CONNECTION_WEST))).with(WIRE_CONNECTION_WEST, blockState.get(WIRE_CONNECTION_NORTH));
            }
            case CLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)blockState.with(WIRE_CONNECTION_NORTH, blockState.get(WIRE_CONNECTION_WEST))).with(WIRE_CONNECTION_EAST, blockState.get(WIRE_CONNECTION_NORTH))).with(WIRE_CONNECTION_SOUTH, blockState.get(WIRE_CONNECTION_EAST))).with(WIRE_CONNECTION_WEST, blockState.get(WIRE_CONNECTION_SOUTH));
            }
        }
        return blockState;
    }

    @Override
    public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
        switch (blockMirror) {
            case LEFT_RIGHT: {
                return (BlockState)((BlockState)blockState.with(WIRE_CONNECTION_NORTH, blockState.get(WIRE_CONNECTION_SOUTH))).with(WIRE_CONNECTION_SOUTH, blockState.get(WIRE_CONNECTION_NORTH));
            }
            case FRONT_BACK: {
                return (BlockState)((BlockState)blockState.with(WIRE_CONNECTION_EAST, blockState.get(WIRE_CONNECTION_WEST))).with(WIRE_CONNECTION_WEST, blockState.get(WIRE_CONNECTION_EAST));
            }
        }
        return super.mirror(blockState, blockMirror);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WIRE_CONNECTION_NORTH, WIRE_CONNECTION_EAST, WIRE_CONNECTION_SOUTH, WIRE_CONNECTION_WEST, POWER);
    }
}

