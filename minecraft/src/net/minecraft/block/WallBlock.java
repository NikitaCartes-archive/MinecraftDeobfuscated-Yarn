package net.minecraft.block;

import net.minecraft.entity.EntityContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldView;

public class WallBlock extends HorizontalConnectedBlock {
	public static final BooleanProperty UP = Properties.UP;
	private final VoxelShape[] UP_OUTLINE_SHAPES;
	private final VoxelShape[] UP_COLLISION_SHAPES;

	public WallBlock(Block.Settings settings) {
		super(0.0F, 3.0F, 0.0F, 14.0F, 24.0F, settings);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(UP, Boolean.valueOf(true))
				.with(NORTH, Boolean.valueOf(false))
				.with(EAST, Boolean.valueOf(false))
				.with(SOUTH, Boolean.valueOf(false))
				.with(WEST, Boolean.valueOf(false))
				.with(WATERLOGGED, Boolean.valueOf(false))
		);
		this.UP_OUTLINE_SHAPES = this.createShapes(4.0F, 3.0F, 16.0F, 0.0F, 14.0F);
		this.UP_COLLISION_SHAPES = this.createShapes(4.0F, 3.0F, 24.0F, 0.0F, 24.0F);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext ePos) {
		return state.get(UP) ? this.UP_OUTLINE_SHAPES[this.getShapeIndex(state)] : super.getOutlineShape(state, view, pos, ePos);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, EntityContext ePos) {
		return state.get(UP) ? this.UP_COLLISION_SHAPES[this.getShapeIndex(state)] : super.getCollisionShape(state, view, pos, ePos);
	}

	@Override
	public boolean canPlaceAtSide(BlockState world, BlockView view, BlockPos pos, BlockPlacementEnvironment env) {
		return false;
	}

	private boolean shouldConnectTo(BlockState state, boolean faceFullSquare, Direction side) {
		Block block = state.getBlock();
		boolean bl = block.matches(BlockTags.WALLS) || block instanceof FenceGateBlock && FenceGateBlock.canWallConnect(state, side);
		return !canConnect(block) && faceFullSquare || bl;
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
		BlockState blockState = worldView.getBlockState(blockPos2);
		BlockState blockState2 = worldView.getBlockState(blockPos3);
		BlockState blockState3 = worldView.getBlockState(blockPos4);
		BlockState blockState4 = worldView.getBlockState(blockPos5);
		boolean bl = this.shouldConnectTo(blockState, blockState.isSideSolidFullSquare(worldView, blockPos2, Direction.SOUTH), Direction.SOUTH);
		boolean bl2 = this.shouldConnectTo(blockState2, blockState2.isSideSolidFullSquare(worldView, blockPos3, Direction.WEST), Direction.WEST);
		boolean bl3 = this.shouldConnectTo(blockState3, blockState3.isSideSolidFullSquare(worldView, blockPos4, Direction.NORTH), Direction.NORTH);
		boolean bl4 = this.shouldConnectTo(blockState4, blockState4.isSideSolidFullSquare(worldView, blockPos5, Direction.EAST), Direction.EAST);
		boolean bl5 = (!bl || bl2 || !bl3 || bl4) && (bl || !bl2 || bl3 || !bl4);
		return this.getDefaultState()
			.with(UP, Boolean.valueOf(bl5 || !worldView.isAir(blockPos.up())))
			.with(NORTH, Boolean.valueOf(bl))
			.with(EAST, Boolean.valueOf(bl2))
			.with(SOUTH, Boolean.valueOf(bl3))
			.with(WEST, Boolean.valueOf(bl4))
			.with(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if ((Boolean)state.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		if (facing == Direction.DOWN) {
			return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
		} else {
			Direction direction = facing.getOpposite();
			boolean bl = facing == Direction.NORTH
				? this.shouldConnectTo(neighborState, neighborState.isSideSolidFullSquare(world, neighborPos, direction), direction)
				: (Boolean)state.get(NORTH);
			boolean bl2 = facing == Direction.EAST
				? this.shouldConnectTo(neighborState, neighborState.isSideSolidFullSquare(world, neighborPos, direction), direction)
				: (Boolean)state.get(EAST);
			boolean bl3 = facing == Direction.SOUTH
				? this.shouldConnectTo(neighborState, neighborState.isSideSolidFullSquare(world, neighborPos, direction), direction)
				: (Boolean)state.get(SOUTH);
			boolean bl4 = facing == Direction.WEST
				? this.shouldConnectTo(neighborState, neighborState.isSideSolidFullSquare(world, neighborPos, direction), direction)
				: (Boolean)state.get(WEST);
			boolean bl5 = (!bl || bl2 || !bl3 || bl4) && (bl || !bl2 || bl3 || !bl4);
			return state.with(UP, Boolean.valueOf(bl5 || !world.isAir(pos.up())))
				.with(NORTH, Boolean.valueOf(bl))
				.with(EAST, Boolean.valueOf(bl2))
				.with(SOUTH, Boolean.valueOf(bl3))
				.with(WEST, Boolean.valueOf(bl4));
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(UP, NORTH, EAST, WEST, SOUTH, WATERLOGGED);
	}
}
