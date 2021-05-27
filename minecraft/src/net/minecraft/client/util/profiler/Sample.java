package net.minecraft.client.util.profiler;

import java.time.Instant;
import net.minecraft.util.profiler.ProfileResult;

public final class Sample {
	public final Instant samplingTimer;
	public final int ticks;
	public final ProfileResult result;

	public Sample(Instant instant, int ticks, ProfileResult result) {
		this.samplingTimer = instant;
		this.ticks = ticks;
		this.result = result;
	}
}
