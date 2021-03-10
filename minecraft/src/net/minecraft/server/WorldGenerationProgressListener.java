package net.minecraft.server;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;

public interface WorldGenerationProgressListener {
	void start(ChunkPos spawnPos);

	void setChunkStatus(ChunkPos pos, @Nullable ChunkStatus status);

	@Environment(EnvType.CLIENT)
	void start();

	void stop();
}
