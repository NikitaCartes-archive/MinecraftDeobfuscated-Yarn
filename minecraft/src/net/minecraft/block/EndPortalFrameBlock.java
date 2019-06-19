package net.minecraft.block;

import com.google.common.base.Predicates;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EndPortalFrameBlock extends Block {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final BooleanProperty EYE = Properties.EYE;
	protected static final VoxelShape FRAME_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 13.0, 16.0);
	protected static final VoxelShape EYE_SHAPE = Block.createCuboidShape(4.0, 13.0, 4.0, 12.0, 16.0, 12.0);
	protected static final VoxelShape FRAME_WITH_EYE_SHAPE = VoxelShapes.union(FRAME_SHAPE, EYE_SHAPE);
	private static BlockPattern COMPLETED_FRAME;

	public EndPortalFrameBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(FACING, Direction.field_11043).with(EYE, Boolean.valueOf(false)));
	}

	@Override
	public boolean hasSidedTransparency(BlockState blockState) {
		return true;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return blockState.get(EYE) ? FRAME_WITH_EYE_SHAPE : FRAME_SHAPE;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(FACING, itemPlacementContext.getPlayerFacing().getOpposite()).with(EYE, Boolean.valueOf(false));
	}

	@Override
	public boolean hasComparatorOutput(BlockState blockState) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
		return blockState.get(EYE) ? 15 : 0;
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
		builder.add(FACING, EYE);
	}

	public static BlockPattern getCompletedFramePattern() {
		if (COMPLETED_FRAME == null) {
			COMPLETED_FRAME = BlockPatternBuilder.start()
				.aisle("?vvv?", ">???<", ">???<", ">???<", "?^^^?")
				.where('?', CachedBlockPosition.matchesBlockState(BlockStatePredicate.ANY))
				.where(
					'^',
					CachedBlockPosition.matchesBlockState(
						BlockStatePredicate.forBlock(Blocks.field_10398).with(EYE, Predicates.equalTo(true)).with(FACING, Predicates.equalTo(Direction.field_11035))
					)
				)
				.where(
					'>',
					CachedBlockPosition.matchesBlockState(
						BlockStatePredicate.forBlock(Blocks.field_10398).with(EYE, Predicates.equalTo(true)).with(FACING, Predicates.equalTo(Direction.field_11039))
					)
				)
				.where(
					'v',
					CachedBlockPosition.matchesBlockState(
						BlockStatePredicate.forBlock(Blocks.field_10398).with(EYE, Predicates.equalTo(true)).with(FACING, Predicates.equalTo(Direction.field_11043))
					)
				)
				.where(
					'<',
					CachedBlockPosition.matchesBlockState(
						BlockStatePredicate.forBlock(Blocks.field_10398).with(EYE, Predicates.equalTo(true)).with(FACING, Predicates.equalTo(Direction.field_11034))
					)
				)
				.build();
		}

		return COMPLETED_FRAME;
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
