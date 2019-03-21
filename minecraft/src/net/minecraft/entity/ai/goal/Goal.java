package net.minecraft.entity.ai.goal;

import java.util.EnumSet;

public abstract class Goal {
	private final EnumSet<Goal.class_4134> controlBits = EnumSet.noneOf(Goal.class_4134.class);

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

	public void setControlBits(EnumSet<Goal.class_4134> enumSet) {
		this.controlBits.clear();
		this.controlBits.addAll(enumSet);
	}

	public EnumSet<Goal.class_4134> getControlBits() {
		return this.controlBits;
	}

	public static enum class_4134 {
		field_18405,
		field_18406,
		field_18407,
		field_18408;
	}
}
