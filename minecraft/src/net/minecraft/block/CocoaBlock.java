package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class CocoaBlock extends HorizontalFacingBlock implements Fertilizable {
	public static final IntProperty AGE = Properties.AGE_2;
	protected static final VoxelShape[] AGE_TO_EAST_SHAPE = new VoxelShape[]{
		Block.createCuboidShape(11.0, 7.0, 6.0, 15.0, 12.0, 10.0),
		Block.createCuboidShape(9.0, 5.0, 5.0, 15.0, 12.0, 11.0),
		Block.createCuboidShape(7.0, 3.0, 4.0, 15.0, 12.0, 12.0)
	};
	protected static final VoxelShape[] AGE_TO_WEST_SHAPE = new VoxelShape[]{
		Block.createCuboidShape(1.0, 7.0, 6.0, 5.0, 12.0, 10.0),
		Block.createCuboidShape(1.0, 5.0, 5.0, 7.0, 12.0, 11.0),
		Block.createCuboidShape(1.0, 3.0, 4.0, 9.0, 12.0, 12.0)
	};
	protected static final VoxelShape[] AGE_TO_NORTH_SHAPE = new VoxelShape[]{
		Block.createCuboidShape(6.0, 7.0, 1.0, 10.0, 12.0, 5.0),
		Block.createCuboidShape(5.0, 5.0, 1.0, 11.0, 12.0, 7.0),
		Block.createCuboidShape(4.0, 3.0, 1.0, 12.0, 12.0, 9.0)
	};
	protected static final VoxelShape[] AGE_TO_SOUTH_SHAPE = new VoxelShape[]{
		Block.createCuboidShape(6.0, 7.0, 11.0, 10.0, 12.0, 15.0),
		Block.createCuboidShape(5.0, 5.0, 9.0, 11.0, 12.0, 15.0),
		Block.createCuboidShape(4.0, 3.0, 7.0, 12.0, 12.0, 15.0)
	};

	public CocoaBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(FACING, Direction.field_11043).with(AGE, Integer.valueOf(0)));
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (world.random.nextInt(5) == 0) {
			int i = (Integer)blockState.get(AGE);
			if (i < 2) {
				world.setBlockState(blockPos, blockState.with(AGE, Integer.valueOf(i + 1)), 2);
			}
		}
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		Block block = viewableWorld.getBlockState(blockPos.offset(blockState.get(FACING))).getBlock();
		return block.matches(BlockTags.field_15474);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		int i = (Integer)blockState.get(AGE);
		switch ((Direction)blockState.get(FACING)) {
			case field_11035:
				return AGE_TO_SOUTH_SHAPE[i];
			case field_11043:
			default:
				return AGE_TO_NORTH_SHAPE[i];
			case field_11039:
				return AGE_TO_WEST_SHAPE[i];
			case field_11034:
				return AGE_TO_EAST_SHAPE[i];
		}
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = this.getDefaultState();
		ViewableWorld viewableWorld = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getBlockPos();

		for (Direction direction : itemPlacementContext.getPlacementDirections()) {
			if (direction.getAxis().isHorizontal()) {
				blockState = blockState.with(FACING, direction);
				if (blockState.canPlaceAt(viewableWorld, blockPos)) {
					return blockState;
				}
			}
		}

		return null;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		return direction == blockState.get(FACING) && !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.getDefaultState()
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean isFertilizable(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
		return (Integer)blockState.get(AGE) < 2;
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos blockPos, BlockState blockState) {
		return true;
	}

	@Override
	public void grow(World world, Random random, BlockPos blockPos, BlockState blockState) {
		world.setBlockState(blockPos, blockState.with(AGE, Integer.valueOf((Integer)blockState.get(AGE) + 1)), 2);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9174;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(FACING, AGE);
	}
}
