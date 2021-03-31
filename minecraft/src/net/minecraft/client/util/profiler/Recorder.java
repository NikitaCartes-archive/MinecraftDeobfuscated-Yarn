package net.minecraft.client.util.profiler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.profiler.Profiler;

@Environment(EnvType.CLIENT)
public interface Recorder {
	void sample();

	void start();

	boolean isActive();

	Profiler getProfiler();

	void read();
}
