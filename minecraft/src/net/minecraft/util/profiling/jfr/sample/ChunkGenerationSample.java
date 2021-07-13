package net.minecraft.util.profiling.jfr.sample;

import java.time.Duration;
import jdk.jfr.consumer.RecordedEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;

public class ChunkGenerationSample implements LongRunningSample {
	public final Duration duration;
	public final ChunkPos chunkPos;
	public final BlockPos centerPos;
	public final ChunkStatus chunkStatus;
	public final boolean successful;
	public final String worldKey;

	public ChunkGenerationSample(RecordedEvent event) {
		this.duration = event.getDuration();
		this.chunkPos = new ChunkPos(event.getInt("chunkPosX"), event.getInt("chunkPosZ"));
		this.centerPos = new BlockPos(event.getInt("centerBlockPosX"), 100, event.getInt("centerBlockPosZ"));
		this.chunkStatus = ChunkStatus.byId(event.getString("targetStatus"));
		this.successful = event.getBoolean("success");
		this.worldKey = event.getString("level");
	}

	@Override
	public Duration getDuration() {
		return this.duration;
	}
}
