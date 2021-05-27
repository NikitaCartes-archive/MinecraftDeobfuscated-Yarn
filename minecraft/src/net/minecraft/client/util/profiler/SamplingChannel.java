package net.minecraft.client.util.profiler;

public enum SamplingChannel {
	PATH_FINDING("pathfinding"),
	EVENT_LOOPS("event-loops"),
	MAIL_BOXES("mailboxes"),
	TICK_LOOP("ticking"),
	JVM("jvm"),
	CHUNK_RENDERING("chunk rendering"),
	CHUNK_RENDERING_DISPATCHING("chunk rendering dispatching"),
	CPU("cpu");

	private final String name;

	private SamplingChannel(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
