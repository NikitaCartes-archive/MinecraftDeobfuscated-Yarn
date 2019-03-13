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
	public static final BooleanProperty field_11171 = ConnectedPlantBlock.field_11332;
	public static final BooleanProperty field_11172 = ConnectedPlantBlock.field_11335;
	public static final BooleanProperty field_11170 = ConnectedPlantBlock.field_11331;
	public static final BooleanProperty field_11167 = ConnectedPlantBlock.field_11328;
	public static final BooleanProperty field_11166 = ConnectedPlantBlock.field_11327;
	public static final BooleanProperty field_11169 = ConnectedPlantBlock.field_11330;
	private static final Map<Direction, BooleanProperty> FACING_PROPERTIES = ConnectedPlantBlock.FACING_PROPERTIES;

	public MushroomBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11171, Boolean.valueOf(true))
				.method_11657(field_11172, Boolean.valueOf(true))
				.method_11657(field_11170, Boolean.valueOf(true))
				.method_11657(field_11167, Boolean.valueOf(true))
				.method_11657(field_11166, Boolean.valueOf(true))
				.method_11657(field_11169, Boolean.valueOf(true))
		);
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		BlockView blockView = itemPlacementContext.method_8045();
		BlockPos blockPos = itemPlacementContext.method_8037();
		return this.method_9564()
			.method_11657(field_11169, Boolean.valueOf(this != blockView.method_8320(blockPos.down()).getBlock()))
			.method_11657(field_11166, Boolean.valueOf(this != blockView.method_8320(blockPos.up()).getBlock()))
			.method_11657(field_11171, Boolean.valueOf(this != blockView.method_8320(blockPos.north()).getBlock()))
			.method_11657(field_11172, Boolean.valueOf(this != blockView.method_8320(blockPos.east()).getBlock()))
			.method_11657(field_11170, Boolean.valueOf(this != blockView.method_8320(blockPos.south()).getBlock()))
			.method_11657(field_11167, Boolean.valueOf(this != blockView.method_8320(blockPos.west()).getBlock()));
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return blockState2.getBlock() == this
			? blockState.method_11657((Property)FACING_PROPERTIES.get(direction), Boolean.valueOf(false))
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public BlockState method_9598(BlockState blockState, Rotation rotation) {
		return blockState.method_11657((Property)FACING_PROPERTIES.get(rotation.method_10503(Direction.NORTH)), blockState.method_11654(field_11171))
			.method_11657((Property)FACING_PROPERTIES.get(rotation.method_10503(Direction.SOUTH)), blockState.method_11654(field_11170))
			.method_11657((Property)FACING_PROPERTIES.get(rotation.method_10503(Direction.EAST)), blockState.method_11654(field_11172))
			.method_11657((Property)FACING_PROPERTIES.get(rotation.method_10503(Direction.WEST)), blockState.method_11654(field_11167))
			.method_11657((Property)FACING_PROPERTIES.get(rotation.method_10503(Direction.UP)), blockState.method_11654(field_11166))
			.method_11657((Property)FACING_PROPERTIES.get(rotation.method_10503(Direction.DOWN)), blockState.method_11654(field_11169));
	}

	@Override
	public BlockState method_9569(BlockState blockState, Mirror mirror) {
		return blockState.method_11657((Property)FACING_PROPERTIES.get(mirror.method_10343(Direction.NORTH)), blockState.method_11654(field_11171))
			.method_11657((Property)FACING_PROPERTIES.get(mirror.method_10343(Direction.SOUTH)), blockState.method_11654(field_11170))
			.method_11657((Property)FACING_PROPERTIES.get(mirror.method_10343(Direction.EAST)), blockState.method_11654(field_11172))
			.method_11657((Property)FACING_PROPERTIES.get(mirror.method_10343(Direction.WEST)), blockState.method_11654(field_11167))
			.method_11657((Property)FACING_PROPERTIES.get(mirror.method_10343(Direction.UP)), blockState.method_11654(field_11166))
			.method_11657((Property)FACING_PROPERTIES.get(mirror.method_10343(Direction.DOWN)), blockState.method_11654(field_11169));
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11166, field_11169, field_11171, field_11172, field_11170, field_11167);
	}
}
