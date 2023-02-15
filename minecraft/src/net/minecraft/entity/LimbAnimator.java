package net.minecraft.entity;

import net.minecraft.util.math.MathHelper;

public class LimbAnimator {
	private float prevSpeed;
	private float speed;
	private float pos;

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public void updateLimbs(float speed, float multiplier) {
		this.prevSpeed = this.speed;
		this.speed = this.speed + (speed - this.speed) * multiplier;
		this.pos = this.pos + this.speed;
	}

	public float getSpeed() {
		return this.speed;
	}

	public float getSpeed(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.prevSpeed, this.speed);
	}

	public float getPos() {
		return this.pos;
	}

	public float getPos(float tickDelta) {
		return this.pos - this.speed * (1.0F - tickDelta);
	}

	public boolean isLimbMoving() {
		return this.speed > 1.0E-5F;
	}
}
