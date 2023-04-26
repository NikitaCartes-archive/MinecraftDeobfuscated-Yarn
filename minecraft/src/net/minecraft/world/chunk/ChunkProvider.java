package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.light.LightSourceView;

public interface ChunkProvider {
	@Nullable
	LightSourceView getChunk(int chunkX, int chunkZ);

	default void onLightUpdate(LightType type, ChunkSectionPos pos) {
	}

	BlockView getWorld();
}
