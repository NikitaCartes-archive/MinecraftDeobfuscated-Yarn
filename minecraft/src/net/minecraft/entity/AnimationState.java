package net.minecraft.entity;

import java.util.function.Consumer;
import net.minecraft.util.Util;

public class AnimationState {
	private static final long field_37417 = Long.MAX_VALUE;
	private long startedAt = Long.MAX_VALUE;

	public void start() {
		this.startedAt = Util.getMeasuringTimeMs();
	}

	public void startIfNotRunning() {
		if (!this.isRunning()) {
			this.start();
		}
	}

	public void stop() {
		this.startedAt = Long.MAX_VALUE;
	}

	public long getStartTime() {
		return this.startedAt;
	}

	public void run(Consumer<AnimationState> consumer) {
		if (this.isRunning()) {
			consumer.accept(this);
		}
	}

	public boolean isRunning() {
		return this.startedAt != Long.MAX_VALUE;
	}
}
