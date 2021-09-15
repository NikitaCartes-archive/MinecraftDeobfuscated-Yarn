package net.minecraft.util.profiling.jfr.sample;

import jdk.jfr.consumer.RecordedEvent;

public record CpuLoadSample() {
	private final double jvm;
	private final double userJvm;
	private final double system;

	public CpuLoadSample(double d, double e, double f) {
		this.jvm = d;
		this.userJvm = e;
		this.system = f;
	}

	public static CpuLoadSample fromEvent(RecordedEvent event) {
		return new CpuLoadSample((double)event.getFloat("jvmSystem"), (double)event.getFloat("jvmUser"), (double)event.getFloat("machineTotal"));
	}
}
