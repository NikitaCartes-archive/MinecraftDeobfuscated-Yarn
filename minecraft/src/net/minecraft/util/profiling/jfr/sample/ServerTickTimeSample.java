package net.minecraft.util.profiling.jfr.sample;

import java.lang.runtime.ObjectMethods;
import java.time.Duration;
import java.time.Instant;
import jdk.jfr.consumer.RecordedEvent;

public final class ServerTickTimeSample extends Record {
	private final Instant time;
	private final Duration averageTickMs;

	public ServerTickTimeSample(Instant instant, Duration duration) {
		this.time = instant;
		this.averageTickMs = duration;
	}

	public static ServerTickTimeSample fromEvent(RecordedEvent event) {
		return new ServerTickTimeSample(event.getStartTime(), event.getDuration("averageTickDuration"));
	}

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",ServerTickTimeSample,"timestamp;currentAverage",ServerTickTimeSample::time,ServerTickTimeSample::averageTickMs>(
			this
		);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",ServerTickTimeSample,"timestamp;currentAverage",ServerTickTimeSample::time,ServerTickTimeSample::averageTickMs>(
			this
		);
	}

	public final boolean equals(Object o) {
		return ObjectMethods.bootstrap<"equals",ServerTickTimeSample,"timestamp;currentAverage",ServerTickTimeSample::time,ServerTickTimeSample::averageTickMs>(
			this, o
		);
	}

	public Instant time() {
		return this.time;
	}

	public Duration averageTickMs() {
		return this.averageTickMs;
	}
}
