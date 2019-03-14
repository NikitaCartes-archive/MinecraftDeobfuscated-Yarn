package net.minecraft.entity.ai.goal;

import java.util.EnumSet;

public abstract class Goal {
	private final EnumSet<Goal.ControlBit> controlBits = EnumSet.noneOf(Goal.ControlBit.class);

	public abstract boolean canStart();

	public boolean shouldContinue() {
		return this.canStart();
	}

	public boolean canStop() {
		return true;
	}

	public void start() {
	}

	public void onRemove() {
	}

	public void tick() {
	}

	public void setControlBits(EnumSet<Goal.ControlBit> enumSet) {
		this.controlBits.clear();
		this.controlBits.addAll(enumSet);
	}

	public EnumSet<Goal.ControlBit> getControlBits() {
		return this.controlBits;
	}

	public static enum ControlBit {
		field_18405,
		field_18406,
		field_18407,
		field_18408;
	}
}
