package net.minecraft.server;

import javax.annotation.Nullable;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenerationProgressLogger implements WorldGenerationProgressListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private final int totalCount;
	private int generatedCount;
	private long startTime;
	private long nextMessageTime = Long.MAX_VALUE;

	public WorldGenerationProgressLogger(int radius) {
		int i = radius * 2 + 1;
		this.totalCount = i * i;
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
			LOGGER.info(new TranslatableText("menu.preparingSpawn", MathHelper.clamp(i, 0, 100)).getString());
		}
	}

	@Override
	public void stop() {
		LOGGER.info("Time elapsed: {} ms", Util.getMeasuringTimeMs() - this.startTime);
		this.nextMessageTime = Long.MAX_VALUE;
	}

	public int getProgressPercentage() {
		return MathHelper.floor((float)this.generatedCount * 100.0F / (float)this.totalCount);
	}
}
