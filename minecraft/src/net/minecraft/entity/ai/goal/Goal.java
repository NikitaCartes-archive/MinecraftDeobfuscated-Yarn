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

	public void setControls(EnumSet<Goal.Control> enumSet) {
		this.controls.clear();
		this.controls.addAll(enumSet);
	}

	public EnumSet<Goal.Control> getControls() {
		return this.controls;
	}

	public static enum Control {
		field_18405,
		field_18406,
		field_18407,
		field_18408;
	}
}
