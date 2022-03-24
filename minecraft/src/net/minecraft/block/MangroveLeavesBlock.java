package net.minecraft.block;

import java.util.Random;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class MangroveLeavesBlock extends LeavesBlock implements Fertilizable {
	public static final int field_37587 = 5;

	public MangroveLeavesBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public boolean hasRandomTicks(BlockState state) {
		return true;
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		super.randomTick(state, world, pos, random);
		if (random.nextInt(5) == 0 && !(Boolean)state.get(PERSISTENT) && !this.shouldDecay(state)) {
			BlockPos blockPos = pos.down();
			if (world.getBlockState(blockPos).isAir() && world.getBlockState(blockPos.down()).isAir() && !hasNearbyPropagule(world, blockPos)) {
				world.setBlockState(blockPos, PropaguleBlock.getDefaultHangingState());
			}
		}
	}

	private static boolean hasNearbyPropagule(WorldAccess world, BlockPos pos) {
		for (BlockPos blockPos : BlockPos.iterate(pos.up().north().east(), pos.down().south().west())) {
			if (world.getBlockState(blockPos).isOf(Blocks.MANGROVE_PROPAGULE)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
		return world.getBlockState(pos.down()).isAir();
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		world.setBlockState(pos.down(), PropaguleBlock.getDefaultHangingState(), Block.NOTIFY_LISTENERS);
	}
}
