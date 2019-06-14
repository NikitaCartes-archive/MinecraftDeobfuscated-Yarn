package net.minecraft.block;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public interface Fertilizable {
	boolean method_9651(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl);

	boolean method_9650(World world, Random random, BlockPos blockPos, BlockState blockState);

	void method_9652(World world, Random random, BlockPos blockPos, BlockState blockState);
}
