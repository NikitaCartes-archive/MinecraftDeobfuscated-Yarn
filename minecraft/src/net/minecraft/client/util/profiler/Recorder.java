package net.minecraft.client.util.profiler;

import net.minecraft.util.profiler.Profiler;

public interface Recorder {
	void sample();

	void start();

	boolean isActive();

	Profiler getProfiler();

	void read();
}
