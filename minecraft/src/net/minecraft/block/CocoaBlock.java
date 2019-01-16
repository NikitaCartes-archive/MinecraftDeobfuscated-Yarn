package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntegerProperty;
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
	public static final IntegerProperty field_10779 = Properties.AGE_2;
	protected static final VoxelShape[] field_10778 = new VoxelShape[]{
		Block.createCubeShape(11.0, 7.0, 6.0, 15.0, 12.0, 10.0),
		Block.createCubeShape(9.0, 5.0, 5.0, 15.0, 12.0, 11.0),
		Block.createCubeShape(7.0, 3.0, 4.0, 15.0, 12.0, 12.0)
	};
	protected static final VoxelShape[] field_10776 = new VoxelShape[]{
		Block.createCubeShape(1.0, 7.0, 6.0, 5.0, 12.0, 10.0),
		Block.createCubeShape(1.0, 5.0, 5.0, 7.0, 12.0, 11.0),
		Block.createCubeShape(1.0, 3.0, 4.0, 9.0, 12.0, 12.0)
	};
	protected static final VoxelShape[] field_10777 = new VoxelShape[]{
		Block.createCubeShape(6.0, 7.0, 1.0, 10.0, 12.0, 5.0),
		Block.createCubeShape(5.0, 5.0, 1.0, 11.0, 12.0, 7.0),
		Block.createCubeShape(4.0, 3.0, 1.0, 12.0, 12.0, 9.0)
	};
	protected static final VoxelShape[] field_10780 = new VoxelShape[]{
		Block.createCubeShape(6.0, 7.0, 11.0, 10.0, 12.0, 15.0),
		Block.createCubeShape(5.0, 5.0, 9.0, 11.0, 12.0, 15.0),
		Block.createCubeShape(4.0, 3.0, 7.0, 12.0, 12.0, 15.0)
	};

	public CocoaBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_11177, Direction.NORTH).with(field_10779, Integer.valueOf(0)));
	}

	@Override
	public void scheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (world.random.nextInt(5) == 0) {
			int i = (Integer)blockState.get(field_10779);
			if (i < 2) {
				world.setBlockState(blockPos, blockState.with(field_10779, Integer.valueOf(i + 1)), 2);
			}
		}
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		Block block = viewableWorld.getBlockState(blockPos.offset(blockState.get(field_11177))).getBlock();
		return block.matches(BlockTags.field_15474);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		int i = (Integer)blockState.get(field_10779);
		switch ((Direction)blockState.get(field_11177)) {
			case SOUTH:
				return field_10780[i];
			case NORTH:
			default:
				return field_10777[i];
			case WEST:
				return field_10776[i];
			case EAST:
				return field_10778[i];
		}
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = this.getDefaultState();
		ViewableWorld viewableWorld = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getPos();

		for (Direction direction : itemPlacementContext.getPlacementFacings()) {
			if (direction.getAxis().isHorizontal()) {
				blockState = blockState.with(field_11177, direction);
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
		return direction == blockState.get(field_11177) && !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.getDefaultState()
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean isFertilizable(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
		return (Integer)blockState.get(field_10779) < 2;
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos blockPos, BlockState blockState) {
		return true;
	}

	@Override
	public void grow(World world, Random random, BlockPos blockPos, BlockState blockState) {
		world.setBlockState(blockPos, blockState.with(field_10779, Integer.valueOf((Integer)blockState.get(field_10779) + 1)), 2);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11177, field_10779);
	}
}
