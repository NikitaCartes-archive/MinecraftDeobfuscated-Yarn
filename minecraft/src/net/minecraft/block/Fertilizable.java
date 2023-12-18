package net.minecraft.block;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public interface Fertilizable {
	boolean isFertilizable(WorldView world, BlockPos pos, BlockState state);

	boolean canGrow(World world, Random random, BlockPos pos, BlockState state);

	void grow(ServerWorld world, Random random, BlockPos pos, BlockState state);

	default BlockPos method_55769(BlockPos blockPos) {
		return switch (this.method_55770()) {
			case NEIGHBOR_SPREADER -> blockPos.up();
			case GROWER -> blockPos;
		};
	}

	default Fertilizable.class_9077 method_55770() {
		return Fertilizable.class_9077.GROWER;
	}

	public static enum class_9077 {
		NEIGHBOR_SPREADER,
		GROWER;
	}
}
