package net.minecraft.util.profiler;

import java.util.Set;
import java.util.function.Supplier;

public interface SamplerSource {
	Set<Sampler> getSamplers(Supplier<ReadableProfiler> profilerSupplier);
}
