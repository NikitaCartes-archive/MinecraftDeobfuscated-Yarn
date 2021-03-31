package net.minecraft.client.util.profiler;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.profiler.ProfilerSystem;
import net.minecraft.util.profiler.ReadableProfiler;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class SamplerFactory {
	private final Metric metric;
	private final Supplier<ReadableProfiler> profilerGetter;

	public SamplerFactory(Metric metric, Supplier<ReadableProfiler> profilerGetter) {
		this.metric = metric;
		this.profilerGetter = profilerGetter;
	}

	public SamplerFactory(String name, Supplier<ReadableProfiler> profilerGetter) {
		this(new Metric(name), profilerGetter);
	}

	public SamplingRecorder createSampler(String... pathNodes) {
		if (pathNodes.length == 0) {
			throw new IllegalArgumentException("Expected at least one path node, got no values");
		} else {
			String string = StringUtils.join((Object[])pathNodes, '\u001e');
			return SamplingRecorder.create(this.metric, () -> {
				ProfilerSystem.LocatedInfo locatedInfo = ((ReadableProfiler)this.profilerGetter.get()).getInfo(string);
				return locatedInfo == null ? -1.0 : (double)locatedInfo.getTotalTime() / 1000000.0;
			});
		}
	}
}
