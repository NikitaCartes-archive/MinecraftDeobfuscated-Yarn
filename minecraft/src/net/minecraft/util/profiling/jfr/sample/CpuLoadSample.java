package net.minecraft.util.profiling.jfr.sample;

import jdk.jfr.consumer.RecordedEvent;

public record CpuLoadSample(double jvm, double userJvm, double system) {
	public static CpuLoadSample fromEvent(RecordedEvent event) {
		return new CpuLoadSample((double)event.getFloat("jvmSystem"), (double)event.getFloat("jvmUser"), (double)event.getFloat("machineTotal"));
	}
}
