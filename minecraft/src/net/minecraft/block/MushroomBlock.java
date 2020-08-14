package net.minecraft.block;

import java.util.Map;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class MushroomBlock extends Block {
	public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
	public static final BooleanProperty EAST = ConnectingBlock.EAST;
	public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
	public static final BooleanProperty WEST = ConnectingBlock.WEST;
	public static final BooleanProperty UP = ConnectingBlock.UP;
	public static final BooleanProperty DOWN = ConnectingBlock.DOWN;
	private static final Map<Direction, BooleanProperty> FACING_PROPERTIES = ConnectingBlock.FACING_PROPERTIES;

	public MushroomBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(NORTH, Boolean.valueOf(true))
				.with(EAST, Boolean.valueOf(true))
				.with(SOUTH, Boolean.valueOf(true))
				.with(WEST, Boolean.valueOf(true))
				.with(UP, Boolean.valueOf(true))
				.with(DOWN, Boolean.valueOf(true))
		);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockView blockView = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();
		return this.getDefaultState()
			.with(DOWN, Boolean.valueOf(this != blockView.getBlockState(blockPos.down()).getBlock()))
			.with(UP, Boolean.valueOf(this != blockView.getBlockState(blockPos.up()).getBlock()))
			.with(NORTH, Boolean.valueOf(this != blockView.getBlockState(blockPos.north()).getBlock()))
			.with(EAST, Boolean.valueOf(this != blockView.getBlockState(blockPos.east()).getBlock()))
			.with(SOUTH, Boolean.valueOf(this != blockView.getBlockState(blockPos.south()).getBlock()))
			.with(WEST, Boolean.valueOf(this != blockView.getBlockState(blockPos.west()).getBlock()));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		return newState.isOf(this)
			? state.with((Property)FACING_PROPERTIES.get(direction), Boolean.valueOf(false))
			: super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with((Property)FACING_PROPERTIES.get(rotation.rotate(Direction.NORTH)), state.get(NORTH))
			.with((Property)FACING_PROPERTIES.get(rotation.rotate(Direction.SOUTH)), state.get(SOUTH))
			.with((Property)FACING_PROPERTIES.get(rotation.rotate(Direction.EAST)), state.get(EAST))
			.with((Property)FACING_PROPERTIES.get(rotation.rotate(Direction.WEST)), state.get(WEST))
			.with((Property)FACING_PROPERTIES.get(rotation.rotate(Direction.UP)), state.get(UP))
			.with((Property)FACING_PROPERTIES.get(rotation.rotate(Direction.DOWN)), state.get(DOWN));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.with((Property)FACING_PROPERTIES.get(mirror.apply(Direction.NORTH)), state.get(NORTH))
			.with((Property)FACING_PROPERTIES.get(mirror.apply(Direction.SOUTH)), state.get(SOUTH))
			.with((Property)FACING_PROPERTIES.get(mirror.apply(Direction.EAST)), state.get(EAST))
			.with((Property)FACING_PROPERTIES.get(mirror.apply(Direction.WEST)), state.get(WEST))
			.with((Property)FACING_PROPERTIES.get(mirror.apply(Direction.UP)), state.get(UP))
			.with((Property)FACING_PROPERTIES.get(mirror.apply(Direction.DOWN)), state.get(DOWN));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
	}
}
