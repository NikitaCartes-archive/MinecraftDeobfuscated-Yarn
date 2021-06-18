package net.minecraft.util.profiler;

import java.util.Set;
import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;

public interface ReadableProfiler extends Profiler {
	ProfileResult getResult();

	@Nullable
	ProfilerSystem.LocatedInfo getInfo(String name);

	/**
	 * {@return a set of pairs of profiler location and sample kind}
	 */
	Set<Pair<String, SampleType>> getSampleTargets();
}
