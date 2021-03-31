package net.minecraft.util.profiler;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import net.minecraft.client.util.profiler.MetricSampler;
import net.minecraft.client.util.profiler.SamplingChannel;

public class MetricSuppliers {
	public static final MetricSuppliers INSTANCE = new MetricSuppliers();
	private final WeakHashMap<MetricSamplerSupplier, Void> samplers = new WeakHashMap();

	private MetricSuppliers() {
	}

	public void add(MetricSamplerSupplier supplier) {
		this.samplers.put(supplier, null);
	}

	public Map<SamplingChannel, List<MetricSampler>> getSamplers() {
		return (Map<SamplingChannel, List<MetricSampler>>)this.samplers
			.keySet()
			.stream()
			.flatMap(metricSamplerSupplier -> metricSamplerSupplier.getSamplers().stream())
			.collect(Collectors.collectingAndThen(Collectors.groupingBy(MetricSampler::getChannel), EnumMap::new));
	}
}
