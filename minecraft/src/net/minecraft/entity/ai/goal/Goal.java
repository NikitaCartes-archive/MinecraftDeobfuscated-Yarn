package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.util.math.MathHelper;

public abstract class Goal {
	private final EnumSet<Goal.Control> controls = EnumSet.noneOf(Goal.Control.class);

	public abstract boolean canStart();

	public boolean shouldContinue() {
		return this.canStart();
	}

	public boolean canStop() {
		return true;
	}

	public void start() {
	}

	public void stop() {
	}

	/**
	 * {@return if the goal should run every tick or not}
	 * 
	 * <p>This returns {@code false} by default. If this returns false,
	 * the goal will tick once after the entity is spawned, and will tick
	 * every other tick.
	 * 
	 * @see #getTickCount(int)
	 */
	public boolean shouldRunEveryTick() {
		return false;
	}

	public void tick() {
	}

	public void setControls(EnumSet<Goal.Control> controls) {
		this.controls.clear();
		this.controls.addAll(controls);
	}

	public String toString() {
		return this.getClass().getSimpleName();
	}

	public EnumSet<Goal.Control> getControls() {
		return this.controls;
	}

	/**
	 * {@return how many times a goal can tick in the given {@param ticks} at most}
	 */
	protected int getTickCount(int ticks) {
		return this.shouldRunEveryTick() ? ticks : toGoalTicks(ticks);
	}

	protected static int toGoalTicks(int serverTicks) {
		return MathHelper.ceilDiv(serverTicks, 2);
	}

	public static enum Control {
		MOVE,
		LOOK,
		JUMP,
		TARGET;
	}
}
