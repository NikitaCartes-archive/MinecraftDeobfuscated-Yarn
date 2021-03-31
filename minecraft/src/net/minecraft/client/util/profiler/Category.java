package net.minecraft.client.util.profiler;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Category {
	private final String name;
	private final SamplingRecorder[] samplers;

	public Category(String name, SamplingRecorder... samplers) {
		this.name = name;
		this.samplers = samplers;
	}

	public Category(String name, List<SamplingRecorder> samplers) {
		this.name = name;
		this.samplers = (SamplingRecorder[])samplers.toArray(new SamplingRecorder[0]);
	}

	public void sample() {
		for (SamplingRecorder samplingRecorder : this.samplers) {
			samplingRecorder.sample();
		}
	}

	public void start() {
		for (SamplingRecorder samplingRecorder : this.samplers) {
			samplingRecorder.start();
		}
	}

	public void stop() {
		for (SamplingRecorder samplingRecorder : this.samplers) {
			samplingRecorder.stop();
		}
	}

	public String getName() {
		return this.name;
	}

	public List<SamplingRecorder> getSamplers() {
		return ImmutableList.copyOf(this.samplers);
	}
}
