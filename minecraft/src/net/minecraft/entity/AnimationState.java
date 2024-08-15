package net.minecraft.entity;

import java.util.function.Consumer;

public class AnimationState {
	private static final int STOPPED = Integer.MIN_VALUE;
	private int startTick = Integer.MIN_VALUE;

	public void start(int tick) {
		this.startTick = tick;
	}

	public void startIfNotRunning(int tick) {
		if (!this.isRunning()) {
			this.start(tick);
		}
	}

	public void setRunning(boolean running, int tick) {
		if (running) {
			this.startIfNotRunning(tick);
		} else {
			this.stop();
		}
	}

	public void stop() {
		this.startTick = Integer.MIN_VALUE;
	}

	public void run(Consumer<AnimationState> consumer) {
		if (this.isRunning()) {
			consumer.accept(this);
		}
	}

	public void skip(int ticks, float speedMultiplier) {
		if (this.isRunning()) {
			this.startTick -= (int)((float)ticks * speedMultiplier);
		}
	}

	public long getTimeInMilliseconds(float age) {
		float f = age - (float)this.startTick;
		return (long)(f * 50.0F);
	}

	public boolean isRunning() {
		return this.startTick != Integer.MIN_VALUE;
	}

	public void copyFrom(AnimationState state) {
		this.startTick = state.startTick;
	}
}
