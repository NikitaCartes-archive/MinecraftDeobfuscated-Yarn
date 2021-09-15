package net.minecraft.util.profiling.jfr.sample;

import java.lang.runtime.ObjectMethods;
import jdk.jfr.consumer.RecordedEvent;

public final class CpuLoadSample extends Record {
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

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",CpuLoadSample,"jvm;userJvm;system",CpuLoadSample::jvm,CpuLoadSample::userJvm,CpuLoadSample::system>(this);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",CpuLoadSample,"jvm;userJvm;system",CpuLoadSample::jvm,CpuLoadSample::userJvm,CpuLoadSample::system>(this);
	}

	public final boolean equals(Object o) {
		return ObjectMethods.bootstrap<"equals",CpuLoadSample,"jvm;userJvm;system",CpuLoadSample::jvm,CpuLoadSample::userJvm,CpuLoadSample::system>(this, o);
	}

	public double jvm() {
		return this.jvm;
	}

	public double userJvm() {
		return this.userJvm;
	}

	public double system() {
		return this.system;
	}
}
