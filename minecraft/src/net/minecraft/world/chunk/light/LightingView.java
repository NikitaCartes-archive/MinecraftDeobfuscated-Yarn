package net.minecraft.world.chunk.light;

import net.minecraft.class_4076;
import net.minecraft.util.math.BlockPos;

public interface LightingView {
	default void scheduleChunkLightUpdate(BlockPos blockPos, boolean bl) {
		this.scheduleChunkLightUpdate(class_4076.method_18682(blockPos), bl);
	}

	void scheduleChunkLightUpdate(class_4076 arg, boolean bl);
}
