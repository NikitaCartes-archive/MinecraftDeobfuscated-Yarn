package net.minecraft.entity.ai.goal;

import java.util.EnumSet;

public abstract class DiveJumpingGoal extends Goal {
	public DiveJumpingGoal() {
		this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.JUMP));
	}

	protected float updatePitch(float previousPitch, float f, float g) {
		float h = f - previousPitch;

		while (h < -180.0F) {
			h += 360.0F;
		}

		while (h >= 180.0F) {
			h -= 360.0F;
		}

		return previousPitch + g * h;
	}
}
