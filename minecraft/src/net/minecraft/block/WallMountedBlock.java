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
		Direction direction = method_10119(blockState).getOpposite();
		BlockPos blockPos2 = blockPos.method_10093(direction);
		BlockState blockState2 = viewableWorld.method_8320(blockPos2);
		Block block = blockState2.getBlock();
		if (method_9553(block)) {
			return false;
		} else {
			boolean bl = Block.method_9501(blockState2.method_11628(viewableWorld, blockPos2), direction.getOpposite());
			return direction == Direction.UP ? block == Blocks.field_10312 || bl : !method_9581(block) && bl;
		}
	}

	@Nullable
	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		for (Direction direction : itemPlacementContext.method_7718()) {
			BlockState blockState;
			if (direction.getAxis() == Direction.Axis.Y) {
				blockState = this.method_9564()
					.method_11657(field_11007, direction == Direction.UP ? WallMountLocation.field_12473 : WallMountLocation.field_12475)
					.method_11657(field_11177, itemPlacementContext.method_8042());
			} else {
				blockState = this.method_9564().method_11657(field_11007, WallMountLocation.field_12471).method_11657(field_11177, direction.getOpposite());
			}

			if (blockState.method_11591(itemPlacementContext.method_8045(), itemPlacementContext.method_8037())) {
				return blockState;
			}
		}

		return null;
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return method_10119(blockState).getOpposite() == direction && !blockState.method_11591(iWorld, blockPos)
			? Blocks.field_10124.method_9564()
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	protected static Direction method_10119(BlockState blockState) {
		switch ((WallMountLocation)blockState.method_11654(field_11007)) {
			case field_12473:
				return Direction.DOWN;
			case field_12475:
				return Direction.UP;
			default:
				return blockState.method_11654(field_11177);
		}
	}
}
