package net.minecraft.server;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkStatus;
import org.slf4j.Logger;

public class WorldGenerationProgressLogger implements WorldGenerationProgressListener {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final int totalCount;
	private int generatedCount;
	private long startTime;
	private long nextMessageTime = Long.MAX_VALUE;

	private WorldGenerationProgressLogger(int radius) {
		this.totalCount = radius;
	}

	public static WorldGenerationProgressLogger create(int spawnChunkRadius) {
		return spawnChunkRadius > 0 ? forSpawnChunks(spawnChunkRadius + 1) : noSpawnChunks();
	}

	public static WorldGenerationProgressLogger forSpawnChunks(int spawnChunkRadius) {
		int i = WorldGenerationProgressListener.getStartRegionSize(spawnChunkRadius);
		return new WorldGenerationProgressLogger(i * i);
	}

	public static WorldGenerationProgressLogger noSpawnChunks() {
		return new WorldGenerationProgressLogger(0);
	}

	@Override
	public void start(ChunkPos spawnPos) {
		this.nextMessageTime = Util.getMeasuringTimeMs();
		this.startTime = this.nextMessageTime;
	}

	@Override
	public void setChunkStatus(ChunkPos pos, @Nullable ChunkStatus status) {
		if (status == ChunkStatus.FULL) {
			this.generatedCount++;
		}

		int i = this.getProgressPercentage();
		if (Util.getMeasuringTimeMs() > this.nextMessageTime) {
			this.nextMessageTime += 500L;
			LOGGER.info(Text.translatable("menu.preparingSpawn", MathHelper.clamp(i, 0, 100)).getString());
		}
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
		LOGGER.info("Time elapsed: {} ms", Util.getMeasuringTimeMs() - this.startTime);
		this.nextMessageTime = Long.MAX_VALUE;
	}

	public int getProgressPercentage() {
		return this.totalCount == 0 ? 100 : MathHelper.floor((float)this.generatedCount * 100.0F / (float)this.totalCount);
	}
}
