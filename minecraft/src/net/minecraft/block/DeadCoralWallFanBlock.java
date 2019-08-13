package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityContext;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;

public class DeadCoralWallFanBlock extends DeadCoralFanBlock {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	private static final Map<Direction, VoxelShape> FACING_TO_SHAPE = Maps.newEnumMap(
		ImmutableMap.of(
			Direction.field_11043,
			Block.createCuboidShape(0.0, 4.0, 5.0, 16.0, 12.0, 16.0),
			Direction.field_11035,
			Block.createCuboidShape(0.0, 4.0, 0.0, 16.0, 12.0, 11.0),
			Direction.field_11039,
			Block.createCuboidShape(5.0, 4.0, 0.0, 16.0, 12.0, 16.0),
			Direction.field_11034,
			Block.createCuboidShape(0.0, 4.0, 0.0, 11.0, 12.0, 16.0)
		)
	);

	protected DeadCoralWallFanBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(FACING, Direction.field_11043).with(WATERLOGGED, Boolean.valueOf(true)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return (VoxelShape)FACING_TO_SHAPE.get(blockState.get(FACING));
	}

	@Override
	public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
		return blockState.with(FACING, blockRotation.rotate(blockState.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
		return blockState.rotate(blockMirror.getRotation(blockState.get(FACING)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if ((Boolean)blockState.get(WATERLOGGED)) {
			iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
		}

		return direction.getOpposite() == blockState.get(FACING) && !blockState.canPlaceAt(iWorld, blockPos) ? Blocks.field_10124.getDefaultState() : blockState;
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		Direction direction = blockState.get(FACING);
		BlockPos blockPos2 = blockPos.offset(direction.getOpposite());
		BlockState blockState2 = viewableWorld.getBlockState(blockPos2);
		return blockState2.isSideSolidFullSquare(viewableWorld, blockPos2, direction);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = super.getPlacementState(itemPlacementContext);
		ViewableWorld viewableWorld = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		Direction[] directions = itemPlacementContext.getPlacementDirections();

		for (Direction direction : directions) {
			if (direction.getAxis().isHorizontal()) {
				blockState = blockState.with(FACING, direction.getOpposite());
				if (blockState.canPlaceAt(viewableWorld, blockPos)) {
					return blockState;
				}
			}
		}

		return null;
	}
}
