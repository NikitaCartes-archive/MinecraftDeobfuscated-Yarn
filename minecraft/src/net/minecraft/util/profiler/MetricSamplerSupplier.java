package net.minecraft.util.profiler;

import java.util.List;
import net.minecraft.client.util.profiler.MetricSampler;

public interface MetricSamplerSupplier {
	List<MetricSampler> getSamplers();
}
