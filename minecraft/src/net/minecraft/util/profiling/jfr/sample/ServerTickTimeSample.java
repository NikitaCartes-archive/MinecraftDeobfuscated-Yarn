package net.minecraft.util.profiling.jfr.sample;

import java.time.Instant;
import jdk.jfr.consumer.RecordedEvent;

public class ServerTickTimeSample {
	public final Instant time;
	public final float averageTickMs;

	public ServerTickTimeSample(RecordedEvent event) {
		this.time = event.getStartTime();
		this.averageTickMs = event.getFloat("averageTickMs");
	}
}
