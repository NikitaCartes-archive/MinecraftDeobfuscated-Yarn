package net.minecraft.entity.ai.goal;

import java.util.EnumSet;

public abstract class DiveJumpingGoal extends Goal {
	public DiveJumpingGoal() {
		this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.JUMP));
	}

	protected float updatePitch(float f, float g, float h) {
		float i = g - f;

		while (i < -180.0F) {
			i += 360.0F;
		}

		while (i >= 180.0F) {
			i -= 360.0F;
		}

		return f + h * i;
	}
}
