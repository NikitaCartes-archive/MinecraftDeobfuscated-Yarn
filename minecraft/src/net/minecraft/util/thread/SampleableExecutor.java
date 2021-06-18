package net.minecraft.util.thread;

import java.util.List;
import net.minecraft.util.profiler.Sampler;

public interface SampleableExecutor {
	List<Sampler> createSamplers();
}
