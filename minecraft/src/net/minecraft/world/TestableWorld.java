package net.minecraft.world;

import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface TestableWorld {
	boolean testBlockState(BlockPos blockPos, Predicate<BlockState> predicate);

	BlockPos getTopPosition(Heightmap.Type type, BlockPos blockPos);
}
