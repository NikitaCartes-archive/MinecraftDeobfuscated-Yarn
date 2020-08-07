package net.minecraft.entity.ai.control;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class LookControl {
	protected final MobEntity entity;
	protected float yawSpeed;
	protected float pitchSpeed;
	protected boolean active;
	protected double lookX;
	protected double lookY;
	protected double lookZ;

	public LookControl(MobEntity entity) {
		this.entity = entity;
	}

	public void lookAt(Vec3d direction) {
		this.lookAt(direction.x, direction.y, direction.z);
	}

	public void lookAt(Entity entity, float yawSpeed, float pitchSpeed) {
		this.lookAt(entity.getX(), getLookingHeightFor(entity), entity.getZ(), yawSpeed, pitchSpeed);
	}

	public void lookAt(double x, double y, double z) {
		this.lookAt(x, y, z, (float)this.entity.getLookYawSpeed(), (float)this.entity.getLookPitchSpeed());
	}

	public void lookAt(double x, double y, double z, float yawSpeed, float pitchSpeed) {
		this.lookX = x;
		this.lookY = y;
		this.lookZ = z;
		this.yawSpeed = yawSpeed;
		this.pitchSpeed = pitchSpeed;
		this.active = true;
	}

	public void tick() {
		if (this.shouldStayHorizontal()) {
			this.entity.pitch = 0.0F;
		}

		if (this.active) {
			this.active = false;
			this.entity.headYaw = this.changeAngle(this.entity.headYaw, this.getTargetYaw(), this.yawSpeed);
			this.entity.pitch = this.changeAngle(this.entity.pitch, this.getTargetPitch(), this.pitchSpeed);
		} else {
			this.entity.headYaw = this.changeAngle(this.entity.headYaw, this.entity.bodyYaw, 10.0F);
		}

		if (!this.entity.getNavigation().isIdle()) {
			this.entity.headYaw = MathHelper.stepAngleTowards(this.entity.headYaw, this.entity.bodyYaw, (float)this.entity.getBodyYawSpeed());
		}
	}

	protected boolean shouldStayHorizontal() {
		return true;
	}

	public boolean isActive() {
		return this.active;
	}

	public double getLookX() {
		return this.lookX;
	}

	public double getLookY() {
		return this.lookY;
	}

	public double getLookZ() {
		return this.lookZ;
	}

	protected float getTargetPitch() {
		double d = this.lookX - this.entity.getX();
		double e = this.lookY - this.entity.getEyeY();
		double f = this.lookZ - this.entity.getZ();
		double g = (double)MathHelper.sqrt(d * d + f * f);
		return (float)(-(MathHelper.atan2(e, g) * 180.0F / (float)Math.PI));
	}

	protected float getTargetYaw() {
		double d = this.lookX - this.entity.getX();
		double e = this.lookZ - this.entity.getZ();
		return (float)(MathHelper.atan2(e, d) * 180.0F / (float)Math.PI) - 90.0F;
	}

	protected float changeAngle(float from, float to, float max) {
		float f = MathHelper.subtractAngles(from, to);
		float g = MathHelper.clamp(f, -max, max);
		return from + g;
	}

	private static double getLookingHeightFor(Entity entity) {
		return entity instanceof LivingEntity ? entity.getEyeY() : (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0;
	}
}
