package net.minecraft.util.profiling.jfr.sample;

import java.time.Duration;
import java.time.Instant;
import jdk.jfr.consumer.RecordedEvent;

public record ServerTickTimeSample() {
	private final Instant time;
	private final Duration averageTickMs;

	public ServerTickTimeSample(Instant instant, Duration duration) {
		this.time = instant;
		this.averageTickMs = duration;
	}

	public static ServerTickTimeSample fromEvent(RecordedEvent event) {
		return new ServerTickTimeSample(event.getStartTime(), event.getDuration("averageTickDuration"));
	}
}
