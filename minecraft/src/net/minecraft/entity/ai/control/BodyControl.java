package net.minecraft.entity.ai.control;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class BodyControl {
	private final MobEntity entity;
	private int activeTicks;
	private float lastHeadYaw;

	public BodyControl(MobEntity entity) {
		this.entity = entity;
	}

	public void tick() {
		if (this.isMoving()) {
			this.entity.bodyYaw = this.entity.yaw;
			this.rotateHead();
			this.lastHeadYaw = this.entity.headYaw;
			this.activeTicks = 0;
		} else {
			if (this.isIndependent()) {
				if (Math.abs(this.entity.headYaw - this.lastHeadYaw) > 15.0F) {
					this.activeTicks = 0;
					this.lastHeadYaw = this.entity.headYaw;
					this.rotateLook();
				} else {
					this.activeTicks++;
					if (this.activeTicks > 10) {
						this.rotateBody();
					}
				}
			}
		}
	}

	private void rotateLook() {
		this.entity.bodyYaw = MathHelper.stepAngleTowards(this.entity.bodyYaw, this.entity.headYaw, (float)this.entity.getBodyYawSpeed());
	}

	private void rotateHead() {
		this.entity.headYaw = MathHelper.stepAngleTowards(this.entity.headYaw, this.entity.bodyYaw, (float)this.entity.getBodyYawSpeed());
	}

	private void rotateBody() {
		int i = this.activeTicks - 10;
		float f = MathHelper.clamp((float)i / 10.0F, 0.0F, 1.0F);
		float g = (float)this.entity.getBodyYawSpeed() * (1.0F - f);
		this.entity.bodyYaw = MathHelper.stepAngleTowards(this.entity.bodyYaw, this.entity.headYaw, g);
	}

	private boolean isIndependent() {
		return this.entity.getPassengerList().isEmpty() || !(this.entity.getPassengerList().get(0) instanceof MobEntity);
	}

	private boolean isMoving() {
		double d = this.entity.getX() - this.entity.prevX;
		double e = this.entity.getZ() - this.entity.prevZ;
		return d * d + e * e > 2.5000003E-7F;
	}
}
