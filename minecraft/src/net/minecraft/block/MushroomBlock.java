package net.minecraft.block;

import java.util.Map;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public class MushroomBlock extends Block {
	public static final BooleanProperty field_11171 = ConnectedPlantBlock.NORTH;
	public static final BooleanProperty field_11172 = ConnectedPlantBlock.EAST;
	public static final BooleanProperty field_11170 = ConnectedPlantBlock.SOUTH;
	public static final BooleanProperty field_11167 = ConnectedPlantBlock.WEST;
	public static final BooleanProperty field_11166 = ConnectedPlantBlock.UP;
	public static final BooleanProperty field_11169 = ConnectedPlantBlock.DOWN;
	private static final Map<Direction, BooleanProperty> field_11168 = ConnectedPlantBlock.FACING_PROPERTIES;

	public MushroomBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory
				.getDefaultState()
				.with(field_11171, Boolean.valueOf(true))
				.with(field_11172, Boolean.valueOf(true))
				.with(field_11170, Boolean.valueOf(true))
				.with(field_11167, Boolean.valueOf(true))
				.with(field_11166, Boolean.valueOf(true))
				.with(field_11169, Boolean.valueOf(true))
		);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockView blockView = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getPos();
		return this.getDefaultState()
			.with(field_11169, Boolean.valueOf(this != blockView.getBlockState(blockPos.down()).getBlock()))
			.with(field_11166, Boolean.valueOf(this != blockView.getBlockState(blockPos.up()).getBlock()))
			.with(field_11171, Boolean.valueOf(this != blockView.getBlockState(blockPos.north()).getBlock()))
			.with(field_11172, Boolean.valueOf(this != blockView.getBlockState(blockPos.east()).getBlock()))
			.with(field_11170, Boolean.valueOf(this != blockView.getBlockState(blockPos.south()).getBlock()))
			.with(field_11167, Boolean.valueOf(this != blockView.getBlockState(blockPos.west()).getBlock()));
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return blockState2.getBlock() == this
			? blockState.with((Property)field_11168.get(direction), Boolean.valueOf(false))
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public BlockState applyRotation(BlockState blockState, Rotation rotation) {
		return blockState.with((Property)field_11168.get(rotation.method_10503(Direction.NORTH)), blockState.get(field_11171))
			.with((Property)field_11168.get(rotation.method_10503(Direction.SOUTH)), blockState.get(field_11170))
			.with((Property)field_11168.get(rotation.method_10503(Direction.EAST)), blockState.get(field_11172))
			.with((Property)field_11168.get(rotation.method_10503(Direction.WEST)), blockState.get(field_11167))
			.with((Property)field_11168.get(rotation.method_10503(Direction.UP)), blockState.get(field_11166))
			.with((Property)field_11168.get(rotation.method_10503(Direction.DOWN)), blockState.get(field_11169));
	}

	@Override
	public BlockState applyMirror(BlockState blockState, Mirror mirror) {
		return blockState.with((Property)field_11168.get(mirror.method_10343(Direction.NORTH)), blockState.get(field_11171))
			.with((Property)field_11168.get(mirror.method_10343(Direction.SOUTH)), blockState.get(field_11170))
			.with((Property)field_11168.get(mirror.method_10343(Direction.EAST)), blockState.get(field_11172))
			.with((Property)field_11168.get(mirror.method_10343(Direction.WEST)), blockState.get(field_11167))
			.with((Property)field_11168.get(mirror.method_10343(Direction.UP)), blockState.get(field_11166))
			.with((Property)field_11168.get(mirror.method_10343(Direction.DOWN)), blockState.get(field_11169));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11166, field_11169, field_11171, field_11172, field_11170, field_11167);
	}
}
