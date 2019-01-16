package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class CoralBlock extends CoralParentBlock {
	private final Block field_10833;
	protected static final VoxelShape field_10834 = Block.createCubeShape(2.0, 0.0, 2.0, 14.0, 15.0, 14.0);

	protected CoralBlock(Block block, Block.Settings settings) {
		super(settings);
		this.field_10833 = block;
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		this.method_9430(blockState, world, blockPos);
	}

	@Override
	public void scheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!method_9431(blockState, world, blockPos)) {
			world.setBlockState(blockPos, this.field_10833.getDefaultState().with(WATERLOGGED, Boolean.valueOf(false)), 2);
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if (direction == Direction.DOWN && !blockState.canPlaceAt(iWorld, blockPos)) {
			return Blocks.field_10124.getDefaultState();
		} else {
			this.method_9430(blockState, iWorld, blockPos);
			if ((Boolean)blockState.get(WATERLOGGED)) {
				iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.method_15789(iWorld));
			}

			return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		}
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return field_10834;
	}
}
