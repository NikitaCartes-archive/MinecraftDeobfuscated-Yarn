package net.minecraft.world;

import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;

public interface TestableWorld {
	boolean method_16358(BlockPos blockPos, Predicate<BlockState> predicate);

	BlockPos method_8598(Heightmap.Type type, BlockPos blockPos);
}
