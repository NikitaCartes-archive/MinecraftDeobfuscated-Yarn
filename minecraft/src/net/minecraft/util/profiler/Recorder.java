package net.minecraft.util.profiler;

public interface Recorder {
	void stop();

	void startTick();

	boolean isActive();

	Profiler getProfiler();

	void endTick();
}
