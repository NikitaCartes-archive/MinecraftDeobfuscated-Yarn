package net.minecraft.entity.mob;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ElytraFlightController {
	private float leftWingPitch;
	private float leftWingYaw;
	private float leftWingRoll;
	private float lastLeftWingPitch;
	private float lastLeftWingYaw;
	private float lastLeftWingRoll;
	private final LivingEntity entity;

	public ElytraFlightController(LivingEntity entity) {
		this.entity = entity;
	}

	public void update() {
		this.lastLeftWingPitch = this.leftWingPitch;
		this.lastLeftWingYaw = this.leftWingYaw;
		this.lastLeftWingRoll = this.leftWingRoll;
		if (this.entity.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)) {
			float f = (float) (Math.PI / 12);
			float g = (float) (-Math.PI / 12);
			float h = 0.0F;
			if (this.entity.isFallFlying()) {
				float i = 1.0F;
				Vec3d vec3d = this.entity.getVelocity();
				if (vec3d.y < 0.0) {
					Vec3d vec3d2 = vec3d.normalize();
					i = 1.0F - (float)Math.pow(-vec3d2.y, 1.5);
				}

				f = i * (float) (Math.PI / 9) + (1.0F - i) * f;
				g = i * (float) (-Math.PI / 2) + (1.0F - i) * g;
			} else if (this.entity.isInSneakingPose()) {
				f = (float) (Math.PI * 2.0 / 9.0);
				g = (float) (-Math.PI / 4);
				h = 0.08726646F;
			}

			this.leftWingPitch = this.leftWingPitch + (f - this.leftWingPitch) * 0.3F;
			this.leftWingYaw = this.leftWingYaw + (h - this.leftWingYaw) * 0.3F;
			this.leftWingRoll = this.leftWingRoll + (g - this.leftWingRoll) * 0.3F;
		} else {
			this.leftWingPitch = 0.0F;
			this.leftWingYaw = 0.0F;
			this.leftWingRoll = 0.0F;
		}
	}

	public float leftWingPitch(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.lastLeftWingPitch, this.leftWingPitch);
	}

	public float leftWingYaw(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.lastLeftWingYaw, this.leftWingYaw);
	}

	public float leftWingRoll(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.lastLeftWingRoll, this.leftWingRoll);
	}
}
