package net.minecraft.util.profiler;

import javax.annotation.Nullable;

public interface ReadableProfiler extends Profiler {
	ProfileResult getResult();

	@Nullable
	ProfilerSystem.LocatedInfo getInfo(String name);
}
