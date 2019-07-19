package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.CollisionView;
import net.minecraft.world.IWorld;

public class WallMountedBlock extends HorizontalFacingBlock {
	public static final EnumProperty<WallMountLocation> FACE = Properties.WALL_MOUNT_LOCATION;

	protected WallMountedBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public boolean canPlaceAt(BlockState state, CollisionView world, BlockPos pos) {
		return canPlaceAt(world, pos, getDirection(state).getOpposite());
	}

	public static boolean canPlaceAt(CollisionView world, BlockPos pos, Direction direction) {
		BlockPos blockPos = pos.offset(direction);
		return world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, direction.getOpposite());
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		for (Direction direction : ctx.getPlacementDirections()) {
			BlockState blockState;
			if (direction.getAxis() == Direction.Axis.Y) {
				blockState = this.getDefaultState()
					.with(FACE, direction == Direction.UP ? WallMountLocation.CEILING : WallMountLocation.FLOOR)
					.with(FACING, ctx.getPlayerFacing());
			} else {
				blockState = this.getDefaultState().with(FACE, WallMountLocation.WALL).with(FACING, direction.getOpposite());
			}

			if (blockState.canPlaceAt(ctx.getWorld(), ctx.getBlockPos())) {
				return blockState;
			}
		}

		return null;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return getDirection(state).getOpposite() == facing && !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
	}

	protected static Direction getDirection(BlockState state) {
		switch ((WallMountLocation)state.get(FACE)) {
			case CEILING:
				return Direction.DOWN;
			case FLOOR:
				return Direction.UP;
			default:
				return state.get(FACING);
		}
	}
}
