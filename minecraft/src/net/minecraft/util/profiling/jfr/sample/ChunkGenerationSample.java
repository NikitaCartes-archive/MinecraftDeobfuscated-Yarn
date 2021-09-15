package net.minecraft.util.profiling.jfr.sample;

import java.lang.runtime.ObjectMethods;
import java.time.Duration;
import jdk.jfr.consumer.RecordedEvent;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.world.chunk.ChunkStatus;

public final class ChunkGenerationSample extends Record implements LongRunningSample {
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

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",ChunkGenerationSample,"duration;chunkPos;worldPos;status;success;level",ChunkGenerationSample::duration,ChunkGenerationSample::chunkPos,ChunkGenerationSample::centerPos,ChunkGenerationSample::chunkStatus,ChunkGenerationSample::successful,ChunkGenerationSample::worldKey>(
			this
		);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",ChunkGenerationSample,"duration;chunkPos;worldPos;status;success;level",ChunkGenerationSample::duration,ChunkGenerationSample::chunkPos,ChunkGenerationSample::centerPos,ChunkGenerationSample::chunkStatus,ChunkGenerationSample::successful,ChunkGenerationSample::worldKey>(
			this
		);
	}

	public final boolean equals(Object o) {
		return ObjectMethods.bootstrap<"equals",ChunkGenerationSample,"duration;chunkPos;worldPos;status;success;level",ChunkGenerationSample::duration,ChunkGenerationSample::chunkPos,ChunkGenerationSample::centerPos,ChunkGenerationSample::chunkStatus,ChunkGenerationSample::successful,ChunkGenerationSample::worldKey>(
			this, o
		);
	}

	@Override
	public Duration duration() {
		return this.duration;
	}

	public ChunkPos chunkPos() {
		return this.chunkPos;
	}

	public ColumnPos centerPos() {
		return this.centerPos;
	}

	public ChunkStatus chunkStatus() {
		return this.chunkStatus;
	}

	public boolean successful() {
		return this.successful;
	}

	public String worldKey() {
		return this.worldKey;
	}
}
