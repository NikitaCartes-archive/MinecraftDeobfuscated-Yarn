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
	public static final EnumProperty<WallMountLocation> FACE = Properties.WALL_MOUNT_LOCATION;

	protected WallMountedBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return canPlaceAt(viewableWorld, blockPos, getDirection(blockState).getOpposite());
	}

	public static boolean canPlaceAt(ViewableWorld viewableWorld, BlockPos blockPos, Direction direction) {
		BlockPos blockPos2 = blockPos.offset(direction);
		return viewableWorld.getBlockState(blockPos2).isSideSolidFullSquare(viewableWorld, blockPos2, direction.getOpposite());
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		for (Direction direction : itemPlacementContext.getPlacementDirections()) {
			BlockState blockState;
			if (direction.getAxis() == Direction.Axis.field_11052) {
				blockState = this.getDefaultState()
					.with(FACE, direction == Direction.field_11036 ? WallMountLocation.field_12473 : WallMountLocation.field_12475)
					.with(FACING, itemPlacementContext.getPlayerFacing());
			} else {
				blockState = this.getDefaultState().with(FACE, WallMountLocation.field_12471).with(FACING, direction.getOpposite());
			}

			if (blockState.canPlaceAt(itemPlacementContext.getWorld(), itemPlacementContext.getBlockPos())) {
				return blockState;
			}
		}

		return null;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		return getDirection(blockState).getOpposite() == direction && !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.getDefaultState()
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	protected static Direction getDirection(BlockState blockState) {
		switch ((WallMountLocation)blockState.get(FACE)) {
			case field_12473:
				return Direction.field_11033;
			case field_12475:
				return Direction.field_11036;
			default:
				return blockState.get(FACING);
		}
	}
}
