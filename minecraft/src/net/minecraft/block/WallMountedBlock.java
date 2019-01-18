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
		Direction direction = getDirection(blockState).getOpposite();
		BlockPos blockPos2 = blockPos.offset(direction);
		BlockState blockState2 = viewableWorld.getBlockState(blockPos2);
		Block block = blockState2.getBlock();
		if (method_9553(block)) {
			return false;
		} else {
			boolean bl = Block.isFaceFullSquare(blockState2.getCollisionShape(viewableWorld, blockPos2), direction.getOpposite());
			return direction == Direction.UP ? block == Blocks.field_10312 || bl : !method_9581(block) && bl;
		}
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		for (Direction direction : itemPlacementContext.getPlacementFacings()) {
			BlockState blockState;
			if (direction.getAxis() == Direction.Axis.Y) {
				blockState = this.getDefaultState()
					.with(FACE, direction == Direction.UP ? WallMountLocation.field_12473 : WallMountLocation.field_12475)
					.with(FACING, itemPlacementContext.getPlayerHorizontalFacing());
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
				return Direction.DOWN;
			case field_12475:
				return Direction.UP;
			default:
				return blockState.get(FACING);
		}
	}
}
