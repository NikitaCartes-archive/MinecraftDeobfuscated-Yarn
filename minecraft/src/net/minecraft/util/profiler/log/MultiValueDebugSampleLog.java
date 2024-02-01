package net.minecraft.util.profiler.log;

public interface MultiValueDebugSampleLog {
	int getDimension();

	int getLength();

	long get(int index);

	long get(int index, int dimension);

	void clear();
}
