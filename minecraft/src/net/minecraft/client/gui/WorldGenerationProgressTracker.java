package net.minecraft.client.gui;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.WorldGenerationProgressLogger;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;

@Environment(EnvType.CLIENT)
public class WorldGenerationProgressTracker implements WorldGenerationProgressListener {
	private final WorldGenerationProgressLogger progressLogger;
	private final Long2ObjectOpenHashMap<ChunkStatus> chunkStatuses;
	private ChunkPos spawnPos = new ChunkPos(0, 0);
	private final int centerSize;
	private final int radius;
	private final int size;
	private boolean isRunning;

	public WorldGenerationProgressTracker(int i) {
		this.progressLogger = new WorldGenerationProgressLogger(i);
		this.centerSize = i * 2 + 1;
		this.radius = i + ChunkStatus.getMaxTargetGenerationRadius();
		this.size = this.radius * 2 + 1;
		this.chunkStatuses = new Long2ObjectOpenHashMap<>();
	}

	@Override
	public void start(ChunkPos chunkPos) {
		if (this.isRunning) {
			this.progressLogger.start(chunkPos);
			this.spawnPos = chunkPos;
		}
	}

	@Override
	public void setChunkStatus(ChunkPos chunkPos, @Nullable ChunkStatus chunkStatus) {
		if (this.isRunning) {
			this.progressLogger.setChunkStatus(chunkPos, chunkStatus);
			if (chunkStatus == null) {
				this.chunkStatuses.remove(chunkPos.toLong());
			} else {
				this.chunkStatuses.put(chunkPos.toLong(), chunkStatus);
			}
		}
	}

	public void start() {
		this.isRunning = true;
		this.chunkStatuses.clear();
	}

	@Override
	public void stop() {
		this.isRunning = false;
		this.progressLogger.stop();
	}

	public int getCenterSize() {
		return this.centerSize;
	}

	public int getSize() {
		return this.size;
	}

	public int getProgressPercentage() {
		return this.progressLogger.getProgressPercentage();
	}

	@Nullable
	public ChunkStatus getChunkStatus(int i, int j) {
		return this.chunkStatuses.get(ChunkPos.toLong(i + this.spawnPos.x - this.radius, j + this.spawnPos.z - this.radius));
	}
}
