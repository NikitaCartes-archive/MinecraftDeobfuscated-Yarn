package net.minecraft.entity.ai.goal;

public abstract class DiveJumpingGoal extends Goal {
	public DiveJumpingGoal() {
		this.setControlBits(5);
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
