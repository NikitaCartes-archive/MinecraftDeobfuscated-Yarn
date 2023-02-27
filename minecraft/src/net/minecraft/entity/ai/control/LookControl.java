package net.minecraft.entity.ai.control;

import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * The look control adjusts a mob's rotations to look at a target position.
 */
public class LookControl implements Control {
	protected final MobEntity entity;
	protected float maxYawChange;
	protected float maxPitchChange;
	protected int lookAtTimer;
	protected double x;
	protected double y;
	protected double z;

	public LookControl(MobEntity entity) {
		this.entity = entity;
	}

	public void lookAt(Vec3d direction) {
		this.lookAt(direction.x, direction.y, direction.z);
	}

	public void lookAt(Entity entity) {
		this.lookAt(entity.getX(), getLookingHeightFor(entity), entity.getZ());
	}

	public void lookAt(Entity entity, float maxYawChange, float maxPitchChange) {
		this.lookAt(entity.getX(), getLookingHeightFor(entity), entity.getZ(), maxYawChange, maxPitchChange);
	}

	public void lookAt(double x, double y, double z) {
		this.lookAt(x, y, z, (float)this.entity.getMaxLookYawChange(), (float)this.entity.getMaxLookPitchChange());
	}

	public void lookAt(double x, double y, double z, float maxYawChange, float maxPitchChange) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.maxYawChange = maxYawChange;
		this.maxPitchChange = maxPitchChange;
		this.lookAtTimer = 2;
	}

	public void tick() {
		if (this.shouldStayHorizontal()) {
			this.entity.setPitch(0.0F);
		}

		if (this.lookAtTimer > 0) {
			this.lookAtTimer--;
			this.getTargetYaw().ifPresent(yaw -> this.entity.headYaw = this.changeAngle(this.entity.headYaw, yaw, this.maxYawChange));
			this.getTargetPitch().ifPresent(pitch -> this.entity.setPitch(this.changeAngle(this.entity.getPitch(), pitch, this.maxPitchChange)));
		} else {
			this.entity.headYaw = this.changeAngle(this.entity.headYaw, this.entity.bodyYaw, 10.0F);
		}

		this.clampHeadYaw();
	}

	protected void clampHeadYaw() {
		if (!this.entity.getNavigation().isIdle()) {
			this.entity.headYaw = MathHelper.clampAngle(this.entity.headYaw, this.entity.bodyYaw, (float)this.entity.getMaxHeadRotation());
		}
	}

	protected boolean shouldStayHorizontal() {
		return true;
	}

	public boolean isLookingAtSpecificPosition() {
		return this.lookAtTimer > 0;
	}

	public double getLookX() {
		return this.x;
	}

	public double getLookY() {
		return this.y;
	}

	public double getLookZ() {
		return this.z;
	}

	protected Optional<Float> getTargetPitch() {
		double d = this.x - this.entity.getX();
		double e = this.y - this.entity.getEyeY();
		double f = this.z - this.entity.getZ();
		double g = Math.sqrt(d * d + f * f);
		return !(Math.abs(e) > 1.0E-5F) && !(Math.abs(g) > 1.0E-5F) ? Optional.empty() : Optional.of((float)(-(MathHelper.atan2(e, g) * 180.0F / (float)Math.PI)));
	}

	protected Optional<Float> getTargetYaw() {
		double d = this.x - this.entity.getX();
		double e = this.z - this.entity.getZ();
		return !(Math.abs(e) > 1.0E-5F) && !(Math.abs(d) > 1.0E-5F)
			? Optional.empty()
			: Optional.of((float)(MathHelper.atan2(e, d) * 180.0F / (float)Math.PI) - 90.0F);
	}

	/**
	 * Changes the angle from {@code from} to {@code to}, or by {@code max} degrees
	 * if {@code to} is too big a change.
	 * 
	 * <p>This is the same as {@link MobEntity#changeAngle(float, float, float)}.
	 */
	protected float changeAngle(float from, float to, float max) {
		float f = MathHelper.subtractAngles(from, to);
		float g = MathHelper.clamp(f, -max, max);
		return from + g;
	}

	private static double getLookingHeightFor(Entity entity) {
		return entity instanceof LivingEntity ? entity.getEyeY() : (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0;
	}
}
