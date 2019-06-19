package net.minecraft.block;

import java.util.Map;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public class MushroomBlock extends Block {
	public static final BooleanProperty NORTH = ConnectedPlantBlock.NORTH;
	public static final BooleanProperty EAST = ConnectedPlantBlock.EAST;
	public static final BooleanProperty SOUTH = ConnectedPlantBlock.SOUTH;
	public static final BooleanProperty WEST = ConnectedPlantBlock.WEST;
	public static final BooleanProperty UP = ConnectedPlantBlock.UP;
	public static final BooleanProperty DOWN = ConnectedPlantBlock.DOWN;
	private static final Map<Direction, BooleanProperty> FACING_PROPERTIES = ConnectedPlantBlock.FACING_PROPERTIES;

	public MushroomBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory
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
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockView blockView = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		return this.getDefaultState()
			.with(DOWN, Boolean.valueOf(this != blockView.getBlockState(blockPos.down()).getBlock()))
			.with(UP, Boolean.valueOf(this != blockView.getBlockState(blockPos.up()).getBlock()))
			.with(NORTH, Boolean.valueOf(this != blockView.getBlockState(blockPos.north()).getBlock()))
			.with(EAST, Boolean.valueOf(this != blockView.getBlockState(blockPos.east()).getBlock()))
			.with(SOUTH, Boolean.valueOf(this != blockView.getBlockState(blockPos.south()).getBlock()))
			.with(WEST, Boolean.valueOf(this != blockView.getBlockState(blockPos.west()).getBlock()));
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		return blockState2.getBlock() == this
			? blockState.with((Property)FACING_PROPERTIES.get(direction), Boolean.valueOf(false))
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
		return blockState.with((Property)FACING_PROPERTIES.get(blockRotation.rotate(Direction.field_11043)), blockState.get(NORTH))
			.with((Property)FACING_PROPERTIES.get(blockRotation.rotate(Direction.field_11035)), blockState.get(SOUTH))
			.with((Property)FACING_PROPERTIES.get(blockRotation.rotate(Direction.field_11034)), blockState.get(EAST))
			.with((Property)FACING_PROPERTIES.get(blockRotation.rotate(Direction.field_11039)), blockState.get(WEST))
			.with((Property)FACING_PROPERTIES.get(blockRotation.rotate(Direction.field_11036)), blockState.get(UP))
			.with((Property)FACING_PROPERTIES.get(blockRotation.rotate(Direction.field_11033)), blockState.get(DOWN));
	}

	@Override
	public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
		return blockState.with((Property)FACING_PROPERTIES.get(blockMirror.apply(Direction.field_11043)), blockState.get(NORTH))
			.with((Property)FACING_PROPERTIES.get(blockMirror.apply(Direction.field_11035)), blockState.get(SOUTH))
			.with((Property)FACING_PROPERTIES.get(blockMirror.apply(Direction.field_11034)), blockState.get(EAST))
			.with((Property)FACING_PROPERTIES.get(blockMirror.apply(Direction.field_11039)), blockState.get(WEST))
			.with((Property)FACING_PROPERTIES.get(blockMirror.apply(Direction.field_11036)), blockState.get(UP))
			.with((Property)FACING_PROPERTIES.get(blockMirror.apply(Direction.field_11033)), blockState.get(DOWN));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
	}
}
