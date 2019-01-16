package net.minecraft.block;

import com.google.common.base.Predicates;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EndPortalFrameBlock extends Block {
	public static final DirectionProperty PROPERTY_FACING = HorizontalFacingBlock.field_11177;
	public static final BooleanProperty field_10958 = Properties.EYE;
	protected static final VoxelShape field_10956 = Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 13.0, 16.0);
	protected static final VoxelShape field_10953 = Block.createCubeShape(4.0, 13.0, 4.0, 12.0, 16.0, 12.0);
	protected static final VoxelShape field_10955 = VoxelShapes.union(field_10956, field_10953);
	private static BlockPattern COMPLETED_FRAME;

	public EndPortalFrameBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(PROPERTY_FACING, Direction.NORTH).with(field_10958, Boolean.valueOf(false)));
	}

	@Override
	public boolean method_9526(BlockState blockState) {
		return true;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return blockState.get(field_10958) ? field_10955 : field_10956;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(PROPERTY_FACING, itemPlacementContext.getPlayerHorizontalFacing().getOpposite()).with(field_10958, Boolean.valueOf(false));
	}

	@Override
	public boolean hasComparatorOutput(BlockState blockState) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
		return blockState.get(field_10958) ? 15 : 0;
	}

	@Override
	public BlockState applyRotation(BlockState blockState, Rotation rotation) {
		return blockState.with(PROPERTY_FACING, rotation.method_10503(blockState.get(PROPERTY_FACING)));
	}

	@Override
	public BlockState applyMirror(BlockState blockState, Mirror mirror) {
		return blockState.applyRotation(mirror.getRotation(blockState.get(PROPERTY_FACING)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(PROPERTY_FACING, field_10958);
	}

	public static BlockPattern getCompletedFramePattern() {
		if (COMPLETED_FRAME == null) {
			COMPLETED_FRAME = BlockPatternBuilder.start()
				.aisle("?vvv?", ">???<", ">???<", ">???<", "?^^^?")
				.where('?', CachedBlockPosition.matchesBlockState(BlockStatePredicate.ANY))
				.where(
					'^',
					CachedBlockPosition.matchesBlockState(
						BlockStatePredicate.forBlock(Blocks.field_10398).with(field_10958, Predicates.equalTo(true)).with(PROPERTY_FACING, Predicates.equalTo(Direction.SOUTH))
					)
				)
				.where(
					'>',
					CachedBlockPosition.matchesBlockState(
						BlockStatePredicate.forBlock(Blocks.field_10398).with(field_10958, Predicates.equalTo(true)).with(PROPERTY_FACING, Predicates.equalTo(Direction.WEST))
					)
				)
				.where(
					'v',
					CachedBlockPosition.matchesBlockState(
						BlockStatePredicate.forBlock(Blocks.field_10398).with(field_10958, Predicates.equalTo(true)).with(PROPERTY_FACING, Predicates.equalTo(Direction.NORTH))
					)
				)
				.where(
					'<',
					CachedBlockPosition.matchesBlockState(
						BlockStatePredicate.forBlock(Blocks.field_10398).with(field_10958, Predicates.equalTo(true)).with(PROPERTY_FACING, Predicates.equalTo(Direction.EAST))
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
