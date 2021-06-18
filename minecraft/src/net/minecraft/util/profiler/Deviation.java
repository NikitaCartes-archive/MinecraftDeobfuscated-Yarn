package net.minecraft.util.profiler;

import java.time.Instant;

public final class Deviation {
	public final Instant instant;
	public final int ticks;
	public final ProfileResult result;

	public Deviation(Instant instant, int ticks, ProfileResult result) {
		this.instant = instant;
		this.ticks = ticks;
		this.result = result;
	}
}
