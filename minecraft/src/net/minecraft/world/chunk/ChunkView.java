package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;

public interface ChunkView {
	@Nullable
	BlockView get(int i, int j);

	default void method_12247(LightType lightType, int i, int j, int k) {
	}

	BlockView method_16399();
}
