package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.minecraft.class_4076;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;

public interface ChunkProvider {
	@Nullable
	BlockView getChunk(int i, int j);

	default void onLightUpdate(LightType lightType, class_4076 arg) {
	}

	BlockView getWorld();
}
