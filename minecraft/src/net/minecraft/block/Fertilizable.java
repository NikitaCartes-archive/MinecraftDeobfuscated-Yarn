package net.minecraft.block;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public interface Fertilizable {
	boolean isFertilizable(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl);

	boolean canGrow(World world, Random random, BlockPos blockPos, BlockState blockState);

	void grow(World world, Random random, BlockPos blockPos, BlockState blockState);
}
