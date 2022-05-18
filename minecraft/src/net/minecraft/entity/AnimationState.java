package net.minecraft.entity;

import java.util.function.Consumer;
import net.minecraft.util.math.MathHelper;

public class AnimationState {
	private static final long STOPPED = Long.MAX_VALUE;
	private long updatedAt = Long.MAX_VALUE;
	private long timeRunning;

	public void start(int i) {
		this.updatedAt = (long)i * 1000L / 20L;
		this.timeRunning = 0L;
	}

	public void startIfNotRunning(int i) {
		if (!this.isRunning()) {
			this.start(i);
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

	public void update(float animationProgress, float speedMultiplier) {
		if (this.isRunning()) {
			long l = MathHelper.lfloor((double)(animationProgress * 1000.0F / 20.0F));
			this.timeRunning = this.timeRunning + (long)((float)(l - this.updatedAt) * speedMultiplier);
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
