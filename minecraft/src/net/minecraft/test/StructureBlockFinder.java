package net.minecraft.test;

import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

@FunctionalInterface
public interface StructureBlockFinder {
	Stream<BlockPos> findStructureBlockPos();
}
