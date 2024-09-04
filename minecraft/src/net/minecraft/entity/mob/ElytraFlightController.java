package net.minecraft.entity.mob;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ElytraFlightController {
	private static final float field_54084 = (float) (Math.PI / 12);
	private static final float field_54085 = (float) (-Math.PI / 12);
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
		float g;
		float h;
		float i;
		if (this.entity.isGliding()) {
			float f = 1.0F;
			Vec3d vec3d = this.entity.getVelocity();
			if (vec3d.y < 0.0) {
				Vec3d vec3d2 = vec3d.normalize();
				f = 1.0F - (float)Math.pow(-vec3d2.y, 1.5);
			}

			g = MathHelper.lerp(f, (float) (Math.PI / 12), (float) (Math.PI / 9));
			h = MathHelper.lerp(f, (float) (-Math.PI / 12), (float) (-Math.PI / 2));
			i = 0.0F;
		} else if (this.entity.isInSneakingPose()) {
			g = (float) (Math.PI * 2.0 / 9.0);
			h = (float) (-Math.PI / 4);
			i = 0.08726646F;
		} else {
			g = (float) (Math.PI / 12);
			h = (float) (-Math.PI / 12);
			i = 0.0F;
		}

		this.leftWingPitch = this.leftWingPitch + (g - this.leftWingPitch) * 0.3F;
		this.leftWingYaw = this.leftWingYaw + (i - this.leftWingYaw) * 0.3F;
		this.leftWingRoll = this.leftWingRoll + (h - this.leftWingRoll) * 0.3F;
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
