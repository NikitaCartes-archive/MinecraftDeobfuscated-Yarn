package net.minecraft.entity.ai.control;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class BodyControl {
	private final MobEntity entity;
	private int activeTicks;
	private float lastHeadYaw;

	public BodyControl(MobEntity mobEntity) {
		this.entity = mobEntity;
	}

	public void tick() {
		if (this.isMoving()) {
			this.entity.field_6283 = this.entity.yaw;
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
		this.entity.field_6283 = MathHelper.method_20306(this.entity.field_6283, this.entity.headYaw, (float)this.entity.method_5986());
	}

	private void rotateHead() {
		this.entity.headYaw = MathHelper.method_20306(this.entity.headYaw, this.entity.field_6283, (float)this.entity.method_5986());
	}

	private void rotateBody() {
		int i = this.activeTicks - 10;
		float f = MathHelper.clamp((float)i / 10.0F, 0.0F, 1.0F);
		float g = (float)this.entity.method_5986() * (1.0F - f);
		this.entity.field_6283 = MathHelper.method_20306(this.entity.field_6283, this.entity.headYaw, g);
	}

	private boolean isIndependent() {
		return this.entity.getPassengerList().isEmpty() || !(this.entity.getPassengerList().get(0) instanceof MobEntity);
	}

	private boolean isMoving() {
		double d = this.entity.x - this.entity.prevX;
		double e = this.entity.z - this.entity.prevZ;
		return d * d + e * e > 2.5000003E-7F;
	}
}
