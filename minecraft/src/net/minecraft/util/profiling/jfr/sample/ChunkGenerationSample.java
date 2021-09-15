package net.minecraft.util.profiling.jfr.sample;

import java.time.Duration;
import jdk.jfr.consumer.RecordedEvent;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.world.chunk.ChunkStatus;

public record ChunkGenerationSample() implements LongRunningSample {
	private final Duration duration;
	private final ChunkPos chunkPos;
	private final ColumnPos centerPos;
	private final ChunkStatus chunkStatus;
	private final boolean successful;
	private final String worldKey;

	public ChunkGenerationSample(Duration duration, ChunkPos chunkPos, ColumnPos columnPos, ChunkStatus chunkStatus, boolean bl, String string) {
		this.duration = duration;
		this.chunkPos = chunkPos;
		this.centerPos = columnPos;
		this.chunkStatus = chunkStatus;
		this.successful = bl;
		this.worldKey = string;
	}

	public static ChunkGenerationSample fromEvent(RecordedEvent event) {
		return new ChunkGenerationSample(
			event.getDuration(),
			new ChunkPos(event.getInt("chunkPosX"), event.getInt("chunkPosX")),
			new ColumnPos(event.getInt("worldPosX"), event.getInt("worldPosZ")),
			ChunkStatus.byId(event.getString("status")),
			event.getBoolean("success"),
			event.getString("level")
		);
	}
}
