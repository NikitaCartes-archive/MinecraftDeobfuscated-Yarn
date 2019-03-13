package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;

public class DeadCoralWallFanBlock extends DeadCoralFanBlock {
	public static final DirectionProperty field_9933 = HorizontalFacingBlock.field_11177;
	private static final Map<Direction, VoxelShape> FACING_TO_SHAPE = Maps.newEnumMap(
		ImmutableMap.of(
			Direction.NORTH,
			Block.method_9541(0.0, 4.0, 5.0, 16.0, 12.0, 16.0),
			Direction.SOUTH,
			Block.method_9541(0.0, 4.0, 0.0, 16.0, 12.0, 11.0),
			Direction.WEST,
			Block.method_9541(5.0, 4.0, 0.0, 16.0, 12.0, 16.0),
			Direction.EAST,
			Block.method_9541(0.0, 4.0, 0.0, 11.0, 12.0, 16.0)
		)
	);

	protected DeadCoralWallFanBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_9933, Direction.NORTH).method_11657(field_9940, Boolean.valueOf(true)));
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return (VoxelShape)FACING_TO_SHAPE.get(blockState.method_11654(field_9933));
	}

	@Override
	public BlockState method_9598(BlockState blockState, Rotation rotation) {
		return blockState.method_11657(field_9933, rotation.method_10503(blockState.method_11654(field_9933)));
	}

	@Override
	public BlockState method_9569(BlockState blockState, Mirror mirror) {
		return blockState.rotate(mirror.method_10345(blockState.method_11654(field_9933)));
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_9933, field_9940);
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if ((Boolean)blockState.method_11654(field_9940)) {
			iWorld.method_8405().method_8676(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
		}

		return direction.getOpposite() == blockState.method_11654(field_9933) && !blockState.method_11591(iWorld, blockPos)
			? Blocks.field_10124.method_9564()
			: blockState;
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		Direction direction = blockState.method_11654(field_9933);
		BlockPos blockPos2 = blockPos.method_10093(direction.getOpposite());
		BlockState blockState2 = viewableWorld.method_8320(blockPos2);
		return Block.method_9501(blockState2.method_11628(viewableWorld, blockPos2), direction) && !method_9581(blockState2.getBlock());
	}

	@Nullable
	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = super.method_9605(itemPlacementContext);
		ViewableWorld viewableWorld = itemPlacementContext.method_8045();
		BlockPos blockPos = itemPlacementContext.method_8037();
		Direction[] directions = itemPlacementContext.method_7718();

		for (Direction direction : directions) {
			if (direction.getAxis().isHorizontal()) {
				blockState = blockState.method_11657(field_9933, direction.getOpposite());
				if (blockState.method_11591(viewableWorld, blockPos)) {
					return blockState;
				}
			}
		}

		return null;
	}
}
