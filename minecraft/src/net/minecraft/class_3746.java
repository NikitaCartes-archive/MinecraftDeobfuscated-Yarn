package net.minecraft;

import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;

public interface class_3746 {
	boolean method_16358(BlockPos blockPos, Predicate<BlockState> predicate);

	BlockPos getTopPosition(Heightmap.Type type, BlockPos blockPos);
}
