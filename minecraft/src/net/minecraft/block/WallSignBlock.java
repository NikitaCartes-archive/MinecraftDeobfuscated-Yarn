package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.fluid.FluidState;
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

public class WallSignBlock extends AbstractSignBlock {
	public static final DirectionProperty FACING = HorizontalFacingBlock.field_11177;
	private static final Map<Direction, VoxelShape> FACING_TO_SHAPE = Maps.newEnumMap(
		ImmutableMap.of(
			Direction.NORTH,
			Block.createCuboidShape(0.0, 4.5, 14.0, 16.0, 12.5, 16.0),
			Direction.SOUTH,
			Block.createCuboidShape(0.0, 4.5, 0.0, 16.0, 12.5, 2.0),
			Direction.EAST,
			Block.createCuboidShape(0.0, 4.5, 0.0, 2.0, 12.5, 16.0),
			Direction.WEST,
			Block.createCuboidShape(14.0, 4.5, 0.0, 16.0, 12.5, 16.0)
		)
	);

	public WallSignBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(FACING, Direction.NORTH).with(WATERLOGGED, Boolean.valueOf(false)));
	}

	@Override
	public String getTranslationKey() {
		return this.getItem().getTranslationKey();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return (VoxelShape)FACING_TO_SHAPE.get(blockState.get(FACING));
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return viewableWorld.getBlockState(blockPos.offset(((Direction)blockState.get(FACING)).getOpposite())).getMaterial().method_15799();
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = this.getDefaultState();
		FluidState fluidState = itemPlacementContext.getWorld().getFluidState(itemPlacementContext.getBlockPos());
		ViewableWorld viewableWorld = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		Direction[] directions = itemPlacementContext.getPlacementFacings();

		for (Direction direction : directions) {
			if (direction.getAxis().isHorizontal()) {
				Direction direction2 = direction.getOpposite();
				blockState = blockState.with(FACING, direction2);
				if (blockState.canPlaceAt(viewableWorld, blockPos)) {
					return blockState.with(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
				}
			}
		}

		return null;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		return direction.getOpposite() == blockState.get(FACING) && !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.getDefaultState()
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public BlockState rotate(BlockState blockState, Rotation rotation) {
		return blockState.with(FACING, rotation.rotate(blockState.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState blockState, Mirror mirror) {
		return blockState.rotate(mirror.getRotation(blockState.get(FACING)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(FACING, WATERLOGGED);
	}
}
