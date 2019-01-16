package net.minecraft.server;

import javax.annotation.Nullable;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenerationProgressLogger implements WorldGenerationProgressListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private final int field_17468;
	private int field_17469;
	private long startTime;
	private long nextMessageTime = Long.MAX_VALUE;

	public WorldGenerationProgressLogger(int i) {
		int j = i * 2 + 1;
		this.field_17468 = j * j;
	}

	@Override
	public void start(ChunkPos chunkPos) {
		this.nextMessageTime = SystemUtil.getMeasuringTimeMs();
		this.startTime = this.nextMessageTime;
	}

	@Override
	public void setChunkStatus(ChunkPos chunkPos, @Nullable ChunkStatus chunkStatus) {
		if (chunkStatus == ChunkStatus.FULL) {
			this.field_17469++;
		}

		int i = this.getProgressPercentage();
		if (SystemUtil.getMeasuringTimeMs() > this.nextMessageTime) {
			this.nextMessageTime += 500L;
			LOGGER.info(new TranslatableTextComponent("menu.preparingSpawn", MathHelper.clamp(i, 0, 100)).getString());
		}
	}

	@Override
	public void stop() {
		LOGGER.info("Time elapsed: {} ms", SystemUtil.getMeasuringTimeMs() - this.startTime);
		this.nextMessageTime = Long.MAX_VALUE;
	}

	public int getProgressPercentage() {
		return MathHelper.floor((float)this.field_17469 * 100.0F / (float)this.field_17468);
	}
}
