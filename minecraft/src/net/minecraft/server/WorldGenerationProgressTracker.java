package net.minecraft.server;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.server.world.ChunkLevels;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;

public class WorldGenerationProgressTracker implements WorldGenerationProgressListener {
	private final WorldGenerationProgressLogger progressLogger;
	private final Long2ObjectOpenHashMap<ChunkStatus> chunkStatuses = new Long2ObjectOpenHashMap<>();
	private ChunkPos spawnPos = new ChunkPos(0, 0);
	private final int centerSize;
	private final int radius;
	private final int size;
	private boolean running;

	private WorldGenerationProgressTracker(WorldGenerationProgressLogger progressLogger, int centerSize, int radius, int size) {
		this.progressLogger = progressLogger;
		this.centerSize = centerSize;
		this.radius = radius;
		this.size = size;
	}

	public static WorldGenerationProgressTracker create(int spawnChunkRadius) {
		return spawnChunkRadius > 0 ? forSpawnChunks(spawnChunkRadius + 1) : noSpawnChunks();
	}

	public static WorldGenerationProgressTracker forSpawnChunks(int spawnChunkRadius) {
		WorldGenerationProgressLogger worldGenerationProgressLogger = WorldGenerationProgressLogger.forSpawnChunks(spawnChunkRadius);
		int i = WorldGenerationProgressListener.getStartRegionSize(spawnChunkRadius);
		int j = spawnChunkRadius + ChunkLevels.field_51859;
		int k = WorldGenerationProgressListener.getStartRegionSize(j);
		return new WorldGenerationProgressTracker(worldGenerationProgressLogger, i, j, k);
	}

	public static WorldGenerationProgressTracker noSpawnChunks() {
		return new WorldGenerationProgressTracker(WorldGenerationProgressLogger.noSpawnChunks(), 0, 0, 0);
	}

	@Override
	public void start(ChunkPos spawnPos) {
		if (this.running) {
			this.progressLogger.start(spawnPos);
			this.spawnPos = spawnPos;
		}
	}

	@Override
	public void setChunkStatus(ChunkPos pos, @Nullable ChunkStatus status) {
		if (this.running) {
			this.progressLogger.setChunkStatus(pos, status);
			if (status == null) {
				this.chunkStatuses.remove(pos.toLong());
			} else {
				this.chunkStatuses.put(pos.toLong(), status);
			}
		}
	}

	@Override
	public void start() {
		this.running = true;
		this.chunkStatuses.clear();
		this.progressLogger.start();
	}

	@Override
	public void stop() {
		this.running = false;
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
	public ChunkStatus getChunkStatus(int x, int z) {
		return this.chunkStatuses.get(ChunkPos.toLong(x + this.spawnPos.x - this.radius, z + this.spawnPos.z - this.radius));
	}
}
