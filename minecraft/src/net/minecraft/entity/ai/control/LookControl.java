package net.minecraft.entity.ai.control;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class LookControl {
	protected final MobEntity entity;
	protected float field_6359;
	protected float field_6358;
	protected boolean active;
	protected double lookX;
	protected double lookY;
	protected double lookZ;

	public LookControl(MobEntity mobEntity) {
		this.entity = mobEntity;
	}

	public void lookAt(Entity entity, float f, float g) {
		this.lookX = entity.x;
		if (entity instanceof LivingEntity) {
			this.lookY = entity.y + (double)entity.getEyeHeight();
		} else {
			this.lookY = (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0;
		}

		this.lookZ = entity.z;
		this.field_6359 = f;
		this.field_6358 = g;
		this.active = true;
	}

	public void lookAt(double d, double e, double f, float g, float h) {
		this.lookX = d;
		this.lookY = e;
		this.lookZ = f;
		this.field_6359 = g;
		this.field_6358 = h;
		this.active = true;
	}

	public void tick() {
		this.entity.pitch = 0.0F;
		if (this.active) {
			this.active = false;
			double d = this.lookX - this.entity.x;
			double e = this.lookY - (this.entity.y + (double)this.entity.getEyeHeight());
			double f = this.lookZ - this.entity.z;
			double g = (double)MathHelper.sqrt(d * d + f * f);
			float h = (float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F;
			float i = (float)(-(MathHelper.atan2(e, g) * 180.0F / (float)Math.PI));
			this.entity.pitch = this.method_6229(this.entity.pitch, i, this.field_6358);
			this.entity.headYaw = this.method_6229(this.entity.headYaw, h, this.field_6359);
		} else {
			this.entity.headYaw = this.method_6229(this.entity.headYaw, this.entity.field_6283, 10.0F);
		}

		float j = MathHelper.wrapDegrees(this.entity.headYaw - this.entity.field_6283);
		if (!this.entity.getNavigation().isIdle()) {
			if (j < -75.0F) {
				this.entity.headYaw = this.entity.field_6283 - 75.0F;
			}

			if (j > 75.0F) {
				this.entity.headYaw = this.entity.field_6283 + 75.0F;
			}
		}
	}

	protected float method_6229(float f, float g, float h) {
		float i = MathHelper.wrapDegrees(g - f);
		if (i > h) {
			i = h;
		}

		if (i < -h) {
			i = -h;
		}

		return f + i;
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
}
