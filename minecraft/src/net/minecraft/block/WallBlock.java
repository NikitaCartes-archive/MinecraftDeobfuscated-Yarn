package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import net.minecraft.block.enums.WallShape;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class WallBlock extends Block implements Waterloggable {
	public static final MapCodec<WallBlock> CODEC = createCodec(WallBlock::new);
	public static final BooleanProperty UP = Properties.UP;
	public static final EnumProperty<WallShape> EAST_SHAPE = Properties.EAST_WALL_SHAPE;
	public static final EnumProperty<WallShape> NORTH_SHAPE = Properties.NORTH_WALL_SHAPE;
	public static final EnumProperty<WallShape> SOUTH_SHAPE = Properties.SOUTH_WALL_SHAPE;
	public static final EnumProperty<WallShape> WEST_SHAPE = Properties.WEST_WALL_SHAPE;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	private final Map<BlockState, VoxelShape> shapeMap;
	private final Map<BlockState, VoxelShape> collisionShapeMap;
	private static final int field_31276 = 3;
	private static final int field_31277 = 14;
	private static final int field_31278 = 4;
	private static final int field_31279 = 1;
	private static final int field_31280 = 7;
	private static final int field_31281 = 9;
	private static final VoxelShape TALL_POST_SHAPE = Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 16.0, 9.0);
	private static final VoxelShape TALL_NORTH_SHAPE = Block.createCuboidShape(7.0, 0.0, 0.0, 9.0, 16.0, 9.0);
	private static final VoxelShape TALL_SOUTH_SHAPE = Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 16.0, 16.0);
	private static final VoxelShape TALL_WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 7.0, 9.0, 16.0, 9.0);
	private static final VoxelShape TALL_EAST_SHAPE = Block.createCuboidShape(7.0, 0.0, 7.0, 16.0, 16.0, 9.0);

	@Override
	public MapCodec<WallBlock> getCodec() {
		return CODEC;
	}

	public WallBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(UP, Boolean.valueOf(true))
				.with(NORTH_SHAPE, WallShape.NONE)
				.with(EAST_SHAPE, WallShape.NONE)
				.with(SOUTH_SHAPE, WallShape.NONE)
				.with(WEST_SHAPE, WallShape.NONE)
				.with(WATERLOGGED, Boolean.valueOf(false))
		);
		this.shapeMap = this.getShapeMap(4.0F, 3.0F, 16.0F, 0.0F, 14.0F, 16.0F);
		this.collisionShapeMap = this.getShapeMap(4.0F, 3.0F, 24.0F, 0.0F, 24.0F, 24.0F);
	}

	private static VoxelShape getVoxelShape(VoxelShape base, WallShape wallShape, VoxelShape tall, VoxelShape low) {
		if (wallShape == WallShape.TALL) {
			return VoxelShapes.union(base, low);
		} else {
			return wallShape == WallShape.LOW ? VoxelShapes.union(base, tall) : base;
		}
	}

	private Map<BlockState, VoxelShape> getShapeMap(float f, float g, float h, float i, float j, float k) {
		float l = 8.0F - f;
		float m = 8.0F + f;
		float n = 8.0F - g;
		float o = 8.0F + g;
		VoxelShape voxelShape = Block.createCuboidShape((double)l, 0.0, (double)l, (double)m, (double)h, (double)m);
		VoxelShape voxelShape2 = Block.createCuboidShape((double)n, (double)i, 0.0, (double)o, (double)j, (double)o);
		VoxelShape voxelShape3 = Block.createCuboidShape((double)n, (double)i, (double)n, (double)o, (double)j, 16.0);
		VoxelShape voxelShape4 = Block.createCuboidShape(0.0, (double)i, (double)n, (double)o, (double)j, (double)o);
		VoxelShape voxelShape5 = Block.createCuboidShape((double)n, (double)i, (double)n, 16.0, (double)j, (double)o);
		VoxelShape voxelShape6 = Block.createCuboidShape((double)n, (double)i, 0.0, (double)o, (double)k, (double)o);
		VoxelShape voxelShape7 = Block.createCuboidShape((double)n, (double)i, (double)n, (double)o, (double)k, 16.0);
		VoxelShape voxelShape8 = Block.createCuboidShape(0.0, (double)i, (double)n, (double)o, (double)k, (double)o);
		VoxelShape voxelShape9 = Block.createCuboidShape((double)n, (double)i, (double)n, 16.0, (double)k, (double)o);
		Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

		for (Boolean boolean_ : UP.getValues()) {
			for (WallShape wallShape : EAST_SHAPE.getValues()) {
				for (WallShape wallShape2 : NORTH_SHAPE.getValues()) {
					for (WallShape wallShape3 : WEST_SHAPE.getValues()) {
						for (WallShape wallShape4 : SOUTH_SHAPE.getValues()) {
							VoxelShape voxelShape10 = VoxelShapes.empty();
							voxelShape10 = getVoxelShape(voxelShape10, wallShape, voxelShape5, voxelShape9);
							voxelShape10 = getVoxelShape(voxelShape10, wallShape3, voxelShape4, voxelShape8);
							voxelShape10 = getVoxelShape(voxelShape10, wallShape2, voxelShape2, voxelShape6);
							voxelShape10 = getVoxelShape(voxelShape10, wallShape4, voxelShape3, voxelShape7);
							if (boolean_) {
								voxelShape10 = VoxelShapes.union(voxelShape10, voxelShape);
							}

							BlockState blockState = this.getDefaultState()
								.with(UP, boolean_)
								.with(EAST_SHAPE, wallShape)
								.with(WEST_SHAPE, wallShape3)
								.with(NORTH_SHAPE, wallShape2)
								.with(SOUTH_SHAPE, wallShape4);
							builder.put(blockState.with(WATERLOGGED, Boolean.valueOf(false)), voxelShape10);
							builder.put(blockState.with(WATERLOGGED, Boolean.valueOf(true)), voxelShape10);
						}
					}
				}
			}
		}

		return builder.build();
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return (VoxelShape)this.shapeMap.get(state);
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return (VoxelShape)this.collisionShapeMap.get(state);
	}

	@Override
	protected boolean canPathfindThrough(BlockState state, NavigationType type) {
		return false;
	}

	private boolean shouldConnectTo(BlockState state, boolean faceFullSquare, Direction side) {
		Block block = state.getBlock();
		boolean bl = block instanceof FenceGateBlock && FenceGateBlock.canWallConnect(state, side);
		return state.isIn(BlockTags.WALLS) || !cannotConnect(state) && faceFullSquare || block instanceof PaneBlock || bl;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		WorldView worldView = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		BlockPos blockPos2 = blockPos.north();
		BlockPos blockPos3 = blockPos.east();
		BlockPos blockPos4 = blockPos.south();
		BlockPos blockPos5 = blockPos.west();
		BlockPos blockPos6 = blockPos.up();
		BlockState blockState = worldView.getBlockState(blockPos2);
		BlockState blockState2 = worldView.getBlockState(blockPos3);
		BlockState blockState3 = worldView.getBlockState(blockPos4);
		BlockState blockState4 = worldView.getBlockState(blockPos5);
		BlockState blockState5 = worldView.getBlockState(blockPos6);
		boolean bl = this.shouldConnectTo(blockState, blockState.isSideSolidFullSquare(worldView, blockPos2, Direction.SOUTH), Direction.SOUTH);
		boolean bl2 = this.shouldConnectTo(blockState2, blockState2.isSideSolidFullSquare(worldView, blockPos3, Direction.WEST), Direction.WEST);
		boolean bl3 = this.shouldConnectTo(blockState3, blockState3.isSideSolidFullSquare(worldView, blockPos4, Direction.NORTH), Direction.NORTH);
		boolean bl4 = this.shouldConnectTo(blockState4, blockState4.isSideSolidFullSquare(worldView, blockPos5, Direction.EAST), Direction.EAST);
		BlockState blockState6 = this.getDefaultState().with(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
		return this.getStateWith(worldView, blockState6, blockPos6, blockState5, bl, bl2, bl3, bl4);
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if ((Boolean)state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		if (direction == Direction.DOWN) {
			return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
		} else {
			return direction == Direction.UP
				? this.getStateAt(world, state, neighborPos, neighborState)
				: this.getStateWithNeighbor(world, pos, state, neighborPos, neighborState, direction);
		}
	}

	private static boolean isConnected(BlockState state, Property<WallShape> property) {
		return state.get(property) != WallShape.NONE;
	}

	private static boolean shouldUseTallShape(VoxelShape aboveShape, VoxelShape tallShape) {
		return !VoxelShapes.matchesAnywhere(tallShape, aboveShape, BooleanBiFunction.ONLY_FIRST);
	}

	private BlockState getStateAt(WorldView world, BlockState state, BlockPos pos, BlockState aboveState) {
		boolean bl = isConnected(state, NORTH_SHAPE);
		boolean bl2 = isConnected(state, EAST_SHAPE);
		boolean bl3 = isConnected(state, SOUTH_SHAPE);
		boolean bl4 = isConnected(state, WEST_SHAPE);
		return this.getStateWith(world, state, pos, aboveState, bl, bl2, bl3, bl4);
	}

	private BlockState getStateWithNeighbor(WorldView world, BlockPos pos, BlockState state, BlockPos neighborPos, BlockState neighborState, Direction direction) {
		Direction direction2 = direction.getOpposite();
		boolean bl = direction == Direction.NORTH
			? this.shouldConnectTo(neighborState, neighborState.isSideSolidFullSquare(world, neighborPos, direction2), direction2)
			: isConnected(state, NORTH_SHAPE);
		boolean bl2 = direction == Direction.EAST
			? this.shouldConnectTo(neighborState, neighborState.isSideSolidFullSquare(world, neighborPos, direction2), direction2)
			: isConnected(state, EAST_SHAPE);
		boolean bl3 = direction == Direction.SOUTH
			? this.shouldConnectTo(neighborState, neighborState.isSideSolidFullSquare(world, neighborPos, direction2), direction2)
			: isConnected(state, SOUTH_SHAPE);
		boolean bl4 = direction == Direction.WEST
			? this.shouldConnectTo(neighborState, neighborState.isSideSolidFullSquare(world, neighborPos, direction2), direction2)
			: isConnected(state, WEST_SHAPE);
		BlockPos blockPos = pos.up();
		BlockState blockState = world.getBlockState(blockPos);
		return this.getStateWith(world, state, blockPos, blockState, bl, bl2, bl3, bl4);
	}

	private BlockState getStateWith(
		WorldView world, BlockState state, BlockPos pos, BlockState aboveState, boolean north, boolean east, boolean south, boolean west
	) {
		VoxelShape voxelShape = aboveState.getCollisionShape(world, pos).getFace(Direction.DOWN);
		BlockState blockState = this.getStateWith(state, north, east, south, west, voxelShape);
		return blockState.with(UP, Boolean.valueOf(this.shouldHavePost(blockState, aboveState, voxelShape)));
	}

	private boolean shouldHavePost(BlockState state, BlockState aboveState, VoxelShape aboveShape) {
		boolean bl = aboveState.getBlock() instanceof WallBlock && (Boolean)aboveState.get(UP);
		if (bl) {
			return true;
		} else {
			WallShape wallShape = state.get(NORTH_SHAPE);
			WallShape wallShape2 = state.get(SOUTH_SHAPE);
			WallShape wallShape3 = state.get(EAST_SHAPE);
			WallShape wallShape4 = state.get(WEST_SHAPE);
			boolean bl2 = wallShape2 == WallShape.NONE;
			boolean bl3 = wallShape4 == WallShape.NONE;
			boolean bl4 = wallShape3 == WallShape.NONE;
			boolean bl5 = wallShape == WallShape.NONE;
			boolean bl6 = bl5 && bl2 && bl3 && bl4 || bl5 != bl2 || bl3 != bl4;
			if (bl6) {
				return true;
			} else {
				boolean bl7 = wallShape == WallShape.TALL && wallShape2 == WallShape.TALL || wallShape3 == WallShape.TALL && wallShape4 == WallShape.TALL;
				return bl7 ? false : aboveState.isIn(BlockTags.WALL_POST_OVERRIDE) || shouldUseTallShape(aboveShape, TALL_POST_SHAPE);
			}
		}
	}

	private BlockState getStateWith(BlockState state, boolean north, boolean east, boolean south, boolean west, VoxelShape aboveShape) {
		return state.with(NORTH_SHAPE, this.getWallShape(north, aboveShape, TALL_NORTH_SHAPE))
			.with(EAST_SHAPE, this.getWallShape(east, aboveShape, TALL_EAST_SHAPE))
			.with(SOUTH_SHAPE, this.getWallShape(south, aboveShape, TALL_SOUTH_SHAPE))
			.with(WEST_SHAPE, this.getWallShape(west, aboveShape, TALL_WEST_SHAPE));
	}

	private WallShape getWallShape(boolean connected, VoxelShape aboveShape, VoxelShape tallShape) {
		if (connected) {
			return shouldUseTallShape(aboveShape, tallShape) ? WallShape.TALL : WallShape.LOW;
		} else {
			return WallShape.NONE;
		}
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	protected boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
		return !(Boolean)state.get(WATERLOGGED);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(UP, NORTH_SHAPE, EAST_SHAPE, WEST_SHAPE, SOUTH_SHAPE, WATERLOGGED);
	}

	@Override
	protected BlockState rotate(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case CLOCKWISE_180:
				return state.with(NORTH_SHAPE, (WallShape)state.get(SOUTH_SHAPE))
					.with(EAST_SHAPE, (WallShape)state.get(WEST_SHAPE))
					.with(SOUTH_SHAPE, (WallShape)state.get(NORTH_SHAPE))
					.with(WEST_SHAPE, (WallShape)state.get(EAST_SHAPE));
			case COUNTERCLOCKWISE_90:
				return state.with(NORTH_SHAPE, (WallShape)state.get(EAST_SHAPE))
					.with(EAST_SHAPE, (WallShape)state.get(SOUTH_SHAPE))
					.with(SOUTH_SHAPE, (WallShape)state.get(WEST_SHAPE))
					.with(WEST_SHAPE, (WallShape)state.get(NORTH_SHAPE));
			case CLOCKWISE_90:
				return state.with(NORTH_SHAPE, (WallShape)state.get(WEST_SHAPE))
					.with(EAST_SHAPE, (WallShape)state.get(NORTH_SHAPE))
					.with(SOUTH_SHAPE, (WallShape)state.get(EAST_SHAPE))
					.with(WEST_SHAPE, (WallShape)state.get(SOUTH_SHAPE));
			default:
				return state;
		}
	}

	@Override
	protected BlockState mirror(BlockState state, BlockMirror mirror) {
		switch (mirror) {
			case LEFT_RIGHT:
				return state.with(NORTH_SHAPE, (WallShape)state.get(SOUTH_SHAPE)).with(SOUTH_SHAPE, (WallShape)state.get(NORTH_SHAPE));
			case FRONT_BACK:
				return state.with(EAST_SHAPE, (WallShape)state.get(WEST_SHAPE)).with(WEST_SHAPE, (WallShape)state.get(EAST_SHAPE));
			default:
				return super.mirror(state, mirror);
		}
	}
}
