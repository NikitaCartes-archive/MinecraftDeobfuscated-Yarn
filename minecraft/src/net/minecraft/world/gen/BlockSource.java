package net.minecraft.world.gen;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

@FunctionalInterface
public interface BlockSource {
	default BlockState get(BlockPos pos) {
		return this.sample(pos.getX(), pos.getY(), pos.getZ());
	}

	BlockState sample(int x, int y, int z);
}
