package net.minecraft.block;

import java.util.Random;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class CoralWallFanBlock extends DeadCoralWallFanBlock {
	private final Block deadCoralBlock;

	protected CoralWallFanBlock(Block block, Block.Settings settings) {
		super(settings);
		this.deadCoralBlock = block;
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		this.checkLivingConditions(blockState, world, blockPos);
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!isInWater(blockState, world, blockPos)) {
			world.setBlockState(blockPos, this.deadCoralBlock.getDefaultState().with(WATERLOGGED, Boolean.valueOf(false)).with(FACING, blockState.get(FACING)), 2);
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if (direction.getOpposite() == blockState.get(FACING) && !blockState.canPlaceAt(iWorld, blockPos)) {
			return Blocks.field_10124.getDefaultState();
		} else {
			if ((Boolean)blockState.get(WATERLOGGED)) {
				iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
			}

			this.checkLivingConditions(blockState, iWorld, blockPos);
			return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		}
	}
}
