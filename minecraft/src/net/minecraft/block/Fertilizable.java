package net.minecraft.block;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public interface Fertilizable {
	boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient);

	boolean canGrow(World world, AbstractRandom random, BlockPos pos, BlockState state);

	void grow(ServerWorld world, AbstractRandom random, BlockPos pos, BlockState state);
}
