package net.minecraft.block;

import java.util.Random;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public interface Fertilizable {
	boolean isFertilizable(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl);

	boolean canGrow(World world, Random random, BlockPos blockPos, BlockState blockState);

	void grow(ServerWorld serverWorld, Random random, BlockPos blockPos, BlockState blockState);
}
