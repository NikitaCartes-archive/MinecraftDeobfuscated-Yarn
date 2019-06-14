package net.minecraft.block;

import java.util.Random;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class CoralFanBlock extends DeadCoralFanBlock {
	private final Block deadCoralBlock;

	protected CoralFanBlock(Block block, Block.Settings settings) {
		super(settings);
		this.deadCoralBlock = block;
	}

	@Override
	public void method_9615(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		this.method_9430(blockState, world, blockPos);
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!method_9431(blockState, world, blockPos)) {
			world.method_8652(blockPos, this.deadCoralBlock.method_9564().method_11657(field_9940, Boolean.valueOf(false)), 2);
		}
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if (direction == Direction.field_11033 && !blockState.canPlaceAt(iWorld, blockPos)) {
			return Blocks.field_10124.method_9564();
		} else {
			this.method_9430(blockState, iWorld, blockPos);
			if ((Boolean)blockState.method_11654(field_9940)) {
				iWorld.method_8405().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
			}

			return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		}
	}
}
