package net.minecraft.block;

import com.google.common.base.Predicates;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.BlockProxy;
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
	public static final DirectionProperty field_10954 = HorizontalFacingBlock.field_11177;
	public static final BooleanProperty field_10958 = Properties.EYE;
	protected static final VoxelShape field_10956 = Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 13.0, 16.0);
	protected static final VoxelShape field_10953 = Block.createCubeShape(4.0, 13.0, 4.0, 12.0, 16.0, 12.0);
	protected static final VoxelShape field_10955 = VoxelShapes.method_1084(field_10956, field_10953);
	private static BlockPattern COMPLETED_FRAME;

	public EndPortalFrameBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_10954, Direction.NORTH).with(field_10958, Boolean.valueOf(false)));
	}

	@Override
	public boolean method_9526(BlockState blockState) {
		return true;
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.get(field_10958) ? field_10955 : field_10956;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(field_10954, itemPlacementContext.method_8042().getOpposite()).with(field_10958, Boolean.valueOf(false));
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
		return blockState.with(field_10954, rotation.method_10503(blockState.get(field_10954)));
	}

	@Override
	public BlockState applyMirror(BlockState blockState, Mirror mirror) {
		return blockState.applyRotation(mirror.method_10345(blockState.get(field_10954)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_10954, field_10958);
	}

	public static BlockPattern getCompletedFramePattern() {
		if (COMPLETED_FRAME == null) {
			COMPLETED_FRAME = BlockPatternBuilder.start()
				.aisle("?vvv?", ">???<", ">???<", ">???<", "?^^^?")
				.where('?', BlockProxy.method_11678(BlockStatePredicate.ANY))
				.where(
					'^',
					BlockProxy.method_11678(
						BlockStatePredicate.forBlock(Blocks.field_10398).with(field_10958, Predicates.equalTo(true)).with(field_10954, Predicates.equalTo(Direction.SOUTH))
					)
				)
				.where(
					'>',
					BlockProxy.method_11678(
						BlockStatePredicate.forBlock(Blocks.field_10398).with(field_10958, Predicates.equalTo(true)).with(field_10954, Predicates.equalTo(Direction.WEST))
					)
				)
				.where(
					'v',
					BlockProxy.method_11678(
						BlockStatePredicate.forBlock(Blocks.field_10398).with(field_10958, Predicates.equalTo(true)).with(field_10954, Predicates.equalTo(Direction.NORTH))
					)
				)
				.where(
					'<',
					BlockProxy.method_11678(
						BlockStatePredicate.forBlock(Blocks.field_10398).with(field_10958, Predicates.equalTo(true)).with(field_10954, Predicates.equalTo(Direction.EAST))
					)
				)
				.build();
		}

		return COMPLETED_FRAME;
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, PlacementEnvironment placementEnvironment) {
		return false;
	}
}
