package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.class_4538;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class WallMountedBlock extends HorizontalFacingBlock {
	public static final EnumProperty<WallMountLocation> FACE = Properties.WALL_MOUNT_LOCATION;

	protected WallMountedBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, class_4538 arg, BlockPos blockPos) {
		return canPlaceAt(arg, blockPos, getDirection(blockState).getOpposite());
	}

	public static boolean canPlaceAt(class_4538 arg, BlockPos blockPos, Direction direction) {
		BlockPos blockPos2 = blockPos.offset(direction);
		return arg.getBlockState(blockPos2).isSideSolidFullSquare(arg, blockPos2, direction.getOpposite());
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		for (Direction direction : itemPlacementContext.getPlacementDirections()) {
			BlockState blockState;
			if (direction.getAxis() == Direction.Axis.Y) {
				blockState = this.getDefaultState()
					.with(FACE, direction == Direction.UP ? WallMountLocation.CEILING : WallMountLocation.FLOOR)
					.with(FACING, itemPlacementContext.getPlayerFacing());
			} else {
				blockState = this.getDefaultState().with(FACE, WallMountLocation.WALL).with(FACING, direction.getOpposite());
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
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	protected static Direction getDirection(BlockState blockState) {
		switch ((WallMountLocation)blockState.get(FACE)) {
			case CEILING:
				return Direction.DOWN;
			case FLOOR:
				return Direction.UP;
			default:
				return blockState.get(FACING);
		}
	}
}
