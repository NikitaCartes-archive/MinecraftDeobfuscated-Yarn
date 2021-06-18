package net.minecraft.util.profiler;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.util.TimeHelper;

public class SamplerFactory {
	private final Set<String> sampledFullPaths = new ObjectOpenHashSet<>();

	public Set<Sampler> createSamplers(Supplier<ReadableProfiler> profilerSupplier) {
		Set<Sampler> set = (Set<Sampler>)((ReadableProfiler)profilerSupplier.get())
			.getSampleTargets()
			.stream()
			.filter(target -> !this.sampledFullPaths.contains(target.getLeft()))
			.map(target -> createSampler(profilerSupplier, (String)target.getLeft(), (SampleType)target.getRight()))
			.collect(Collectors.toSet());

		for (Sampler sampler : set) {
			this.sampledFullPaths.add(sampler.getName());
		}

		return set;
	}

	private static Sampler createSampler(Supplier<ReadableProfiler> profilerSupplier, String id, SampleType type) {
		return Sampler.create(id, type, () -> {
			ProfilerSystem.LocatedInfo locatedInfo = ((ReadableProfiler)profilerSupplier.get()).getInfo(id);
			return locatedInfo == null ? 0.0 : (double)locatedInfo.getMaxTime() / (double)TimeHelper.MILLI_IN_NANOS;
		});
	}
}
