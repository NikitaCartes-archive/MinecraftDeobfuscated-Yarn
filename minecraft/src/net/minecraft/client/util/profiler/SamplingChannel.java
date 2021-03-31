package net.minecraft.client.util.profiler;

public enum SamplingChannel {
	EVENT_LOOP("eventLoops"),
	MAIL_BOX("mailBoxes");

	private final String name;

	private SamplingChannel(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
