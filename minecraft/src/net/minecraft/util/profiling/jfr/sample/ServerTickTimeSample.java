package net.minecraft.util.profiling.jfr.sample;

import java.time.Instant;
import jdk.jfr.consumer.RecordedEvent;

public record ServerTickTimeSample() {
	private final Instant time;
	private final float averageTickMs;

	public ServerTickTimeSample(Instant instant, float f) {
		this.time = instant;
		this.averageTickMs = f;
	}

	public static ServerTickTimeSample fromEvent(RecordedEvent event) {
		return new ServerTickTimeSample(event.getStartTime(), event.getFloat("averageTickMs"));
	}
}
