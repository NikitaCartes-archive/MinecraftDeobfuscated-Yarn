package net.minecraft.block;

import java.util.Random;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public interface Fertilizable {
	boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient);

	boolean canGrow(World world, Random random, BlockPos pos, BlockState state);

	void grow(ServerWorld world, Random random, BlockPos pos, BlockState state);
}
