package net.minecraft.block;

import java.util.Random;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class CoralWallFanBlock extends CoralDeadWallFanBlock {
	private final Block field_10819;

	protected CoralWallFanBlock(Block block, Block.Settings settings) {
		super(settings);
		this.field_10819 = block;
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		this.method_9430(blockState, world, blockPos);
	}

	@Override
	public void scheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!method_9431(blockState, world, blockPos)) {
			world.setBlockState(blockPos, this.field_10819.getDefaultState().with(WATERLOGGED, Boolean.valueOf(false)).with(field_9933, blockState.get(field_9933)), 2);
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if (direction.getOpposite() == blockState.get(field_9933) && !blockState.canPlaceAt(iWorld, blockPos)) {
			return Blocks.field_10124.getDefaultState();
		} else {
			if ((Boolean)blockState.get(WATERLOGGED)) {
				iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.method_15789(iWorld));
			}

			this.method_9430(blockState, iWorld, blockPos);
			return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		}
	}
}
