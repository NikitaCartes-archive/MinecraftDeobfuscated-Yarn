package net.minecraft.block;

import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class GravelBlock extends FallingBlock {
	public GravelBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public int getColor(BlockState state, BlockView world, BlockPos pos) {
		return -8356741;
	}

	@Override
	public void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos, FallingBlockEntity fallingBlockEntity) {
		super.onLanding(world, pos, fallingBlockState, currentStateInPos, fallingBlockEntity);

		for (Direction direction : Direction.Type.HORIZONTAL) {
			this.method_42883(world, pos.offset(direction));
		}
	}

	private void method_42883(World world, BlockPos blockPos) {
		FireBlock fireBlock = (FireBlock)Blocks.FIRE;
		int i = fireBlock.getBurnChance(world.getBlockState(blockPos));
		if (i > 0 && world.random.nextInt(i) > 5) {
			world.setBlockState(blockPos, fireBlock.getStateForPosition(world, blockPos), Block.NOTIFY_ALL);
		}
	}
}
