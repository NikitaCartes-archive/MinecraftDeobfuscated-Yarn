package net.minecraft.client.util.profiler;

public class Metric {
	private final String name;

	public Metric(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public String toString() {
		return "Metric{name='" + this.name + "'}";
	}
}
