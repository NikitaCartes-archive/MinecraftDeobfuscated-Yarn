package net.minecraft.util.profiling.jfr.sample;

import jdk.jfr.consumer.RecordedEvent;

public class CpuLoadSample {
	public final double jvm;
	public final double userJvm;
	public final double system;

	public CpuLoadSample(RecordedEvent event) {
		this.jvm = (double)event.getFloat("jvmSystem");
		this.userJvm = (double)event.getFloat("jvmUser");
		this.system = (double)event.getFloat("machineTotal");
	}
}
