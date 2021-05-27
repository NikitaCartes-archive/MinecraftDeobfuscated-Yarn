package net.minecraft.util.profiler;

import java.util.List;
import net.minecraft.client.util.profiler.SamplingRecorder;

public interface MetricSamplerSupplier {
	List<SamplingRecorder> getSamplers();
}
