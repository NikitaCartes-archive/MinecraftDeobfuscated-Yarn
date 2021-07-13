package net.minecraft.util.profiling.jfr.sample;

import java.time.Duration;

public interface LongRunningSample {
	Duration getDuration();
}
