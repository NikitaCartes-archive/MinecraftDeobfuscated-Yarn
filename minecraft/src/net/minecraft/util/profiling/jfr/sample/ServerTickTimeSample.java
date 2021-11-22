package net.minecraft.util.profiling.jfr.sample;

import java.time.Duration;
import java.time.Instant;
import jdk.jfr.consumer.RecordedEvent;

public record ServerTickTimeSample(Instant time, Duration averageTickMs) {
	public static ServerTickTimeSample fromEvent(RecordedEvent event) {
		return new ServerTickTimeSample(event.getStartTime(), event.getDuration("averageTickDuration"));
	}
}
