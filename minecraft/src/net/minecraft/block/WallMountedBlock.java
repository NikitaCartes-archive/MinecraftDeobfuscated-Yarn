package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;

public class WallMountedBlock extends HorizontalFacingBlock {
	public static final EnumProperty<WallMountLocation> field_11007 = Properties.field_12555;

	protected WallMountedBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return canPlaceAt(viewableWorld, blockPos, method_10119(blockState).getOpposite());
	}

	public static boolean canPlaceAt(ViewableWorld viewableWorld, BlockPos blockPos, Direction direction) {
		BlockPos blockPos2 = blockPos.offset(direction);
		return Block.method_20045(viewableWorld.method_8320(blockPos2), viewableWorld, blockPos2, direction.getOpposite());
	}

	@Nullable
	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		for (Direction direction : itemPlacementContext.getPlacementDirections()) {
			BlockState blockState;
			if (direction.getAxis() == Direction.Axis.Y) {
				blockState = this.method_9564()
					.method_11657(field_11007, direction == Direction.field_11036 ? WallMountLocation.field_12473 : WallMountLocation.field_12475)
					.method_11657(field_11177, itemPlacementContext.getPlayerFacing());
			} else {
				blockState = this.method_9564().method_11657(field_11007, WallMountLocation.field_12471).method_11657(field_11177, direction.getOpposite());
			}

			if (blockState.canPlaceAt(itemPlacementContext.method_8045(), itemPlacementContext.getBlockPos())) {
				return blockState;
			}
		}

		return null;
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return method_10119(blockState).getOpposite() == direction && !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.method_9564()
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	protected static Direction method_10119(BlockState blockState) {
		switch ((WallMountLocation)blockState.method_11654(field_11007)) {
			case field_12473:
				return Direction.field_11033;
			case field_12475:
				return Direction.field_11036;
			default:
				return blockState.method_11654(field_11177);
		}
	}
}
