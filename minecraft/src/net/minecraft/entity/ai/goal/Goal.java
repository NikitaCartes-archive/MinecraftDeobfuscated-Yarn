package net.minecraft.entity.ai.goal;

import java.util.EnumSet;

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

	public static enum Control {
		MOVE,
		LOOK,
		JUMP,
		TARGET;
	}
}
