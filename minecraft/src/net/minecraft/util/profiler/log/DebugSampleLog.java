package net.minecraft.util.profiler.log;

public interface DebugSampleLog {
	void set(long[] values);

	void push(long value);

	void push(long value, int column);
}
