package net.minecraft.world.chunk.light;

import net.minecraft.util.math.BlockPos;

public interface LightingView {
	default void method_15552(BlockPos blockPos, boolean bl) {
		this.method_15551(blockPos.getX() >> 4, blockPos.getY() >> 4, blockPos.getZ() >> 4, bl);
	}

	void method_15551(int i, int j, int k, boolean bl);
}
