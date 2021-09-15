package net.minecraft.util.profiling.jfr.sample;

import java.time.Instant;

public interface TimedSample {
	Instant getTime();
}
