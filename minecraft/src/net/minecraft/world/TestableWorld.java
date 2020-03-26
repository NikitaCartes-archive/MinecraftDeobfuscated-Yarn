package net.minecraft.world;

import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface TestableWorld {
	boolean testBlockState(BlockPos pos, Predicate<BlockState> state);

	BlockPos getTopPosition(Heightmap.Type type, BlockPos pos);
}
