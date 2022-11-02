package net.minecraft.block;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public interface Fertilizable {
	boolean isFertilizable(WorldView world, BlockPos pos, BlockState state, boolean isClient);

	boolean canGrow(World world, Random random, BlockPos pos, BlockState state);

	void grow(ServerWorld world, Random random, BlockPos pos, BlockState state);
}
