package net.minecraft.world;

import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;

public interface TestableWorld {
	boolean test(BlockPos blockPos, Predicate<BlockState> predicate);

	BlockPos getTopPosition(Heightmap.Type type, BlockPos blockPos);
}
