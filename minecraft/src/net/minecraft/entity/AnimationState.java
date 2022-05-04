package net.minecraft.entity;

import java.util.function.Consumer;
import net.minecraft.util.Util;

public class AnimationState {
	private static final long field_37417 = Long.MAX_VALUE;
	private long updatedAt = Long.MAX_VALUE;
	private long timeRunning;

	public void start() {
		this.updatedAt = Util.getMeasuringTimeMs();
		this.timeRunning = 0L;
	}

	public void startIfNotRunning() {
		if (!this.isRunning()) {
			this.start();
		}
	}

	public void stop() {
		this.updatedAt = Long.MAX_VALUE;
	}

	public void run(Consumer<AnimationState> consumer) {
		if (this.isRunning()) {
			consumer.accept(this);
		}
	}

	public void update(boolean gamePaused, float f) {
		if (this.isRunning()) {
			long l = Util.getMeasuringTimeMs();
			if (!gamePaused) {
				this.timeRunning = this.timeRunning + (long)((float)(l - this.updatedAt) * f);
			}

			this.updatedAt = l;
		}
	}

	public long getTimeRunning() {
		return this.timeRunning;
	}

	public boolean isRunning() {
		return this.updatedAt != Long.MAX_VALUE;
	}
}
