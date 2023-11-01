package net.minecraft.world.tick;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.TimeHelper;

public class TickManager {
	public static final float MIN_TICK_RATE = 1.0F;
	protected float tickRate = 20.0F;
	protected long nanosPerTick = TimeHelper.SECOND_IN_NANOS / 20L;
	protected int stepTicks = 0;
	protected boolean shouldTick = true;
	protected boolean frozen = false;

	public void setTickRate(float tickRate) {
		this.tickRate = Math.max(tickRate, 1.0F);
		this.nanosPerTick = (long)((double)TimeHelper.SECOND_IN_NANOS / (double)this.tickRate);
	}

	public float getTickRate() {
		return this.tickRate;
	}

	public float getMillisPerTick() {
		return (float)this.nanosPerTick / (float)TimeHelper.MILLI_IN_NANOS;
	}

	public long getNanosPerTick() {
		return this.nanosPerTick;
	}

	public boolean shouldTick() {
		return this.shouldTick;
	}

	public boolean isStepping() {
		return this.stepTicks > 0;
	}

	public void setStepTicks(int stepTicks) {
		this.stepTicks = stepTicks;
	}

	public int getStepTicks() {
		return this.stepTicks;
	}

	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}

	public boolean isFrozen() {
		return this.frozen;
	}

	public void step() {
		this.shouldTick = !this.frozen || this.stepTicks > 0;
		if (this.stepTicks > 0) {
			this.stepTicks--;
		}
	}

	public boolean shouldSkipTick(Entity entity) {
		return !this.shouldTick() && !(entity instanceof PlayerEntity) && entity.getPlayerPassengers() <= 0;
	}
}
