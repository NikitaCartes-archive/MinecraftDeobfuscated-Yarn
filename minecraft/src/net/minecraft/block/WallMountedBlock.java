package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class WallMountedBlock extends HorizontalFacingBlock {
	public static final EnumProperty<WallMountLocation> FACE = Properties.WALL_MOUNT_LOCATION;

	protected WallMountedBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return canPlaceAt(world, pos, getDirection(state).getOpposite());
	}

	public static boolean canPlaceAt(WorldView worldView, BlockPos pos, Direction direction) {
		BlockPos blockPos = pos.offset(direction);
		return worldView.getBlockState(blockPos).isSideSolidFullSquare(worldView, blockPos, direction.getOpposite());
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		for (Direction direction : ctx.getPlacementDirections()) {
			BlockState blockState;
			if (direction.getAxis() == Direction.Axis.field_11052) {
				blockState = this.getDefaultState()
					.with(FACE, direction == Direction.field_11036 ? WallMountLocation.field_12473 : WallMountLocation.field_12475)
					.with(FACING, ctx.getPlayerFacing());
			} else {
				blockState = this.getDefaultState().with(FACE, WallMountLocation.field_12471).with(FACING, direction.getOpposite());
			}

			if (blockState.canPlaceAt(ctx.getWorld(), ctx.getBlockPos())) {
				return blockState;
			}
		}

		return null;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		return getDirection(state).getOpposite() == direction && !state.canPlaceAt(world, pos)
			? Blocks.field_10124.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
	}

	protected static Direction getDirection(BlockState state) {
		switch ((WallMountLocation)state.get(FACE)) {
			case field_12473:
				return Direction.field_11033;
			case field_12475:
				return Direction.field_11036;
			default:
				return state.get(FACING);
		}
	}
}
