package net.minecraft.block;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface Oxidizable {
	default int getOxidationTime(Random random) {
		return 1200000 + random.nextInt(768000);
	}

	BlockState getOxidationResult(BlockState state);

	default void scheduleOxidation(World world, Block block, BlockPos pos) {
		world.getBlockTickScheduler().schedule(pos, block, this.getOxidationTime(world.getRandom()));
	}

	default void oxidize(World world, BlockState state, BlockPos pos) {
		world.setBlockState(pos, this.getOxidationResult(state));
	}
}
