package net.minecraft.util.profiling.jfr.sample;

import jdk.jfr.consumer.RecordedEvent;

public record ChunkRegionSample(String level, String dimension, int x, int z) {
	public static ChunkRegionSample fromEvent(RecordedEvent event) {
		return new ChunkRegionSample(event.getString("level"), event.getString("dimension"), event.getInt("chunkPosX"), event.getInt("chunkPosZ"));
	}
}
