package net.minecraft.entity;

import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;

public record Transformation(TransformationType type, @Nullable Entity entity, @Nullable GameProfile playerSkin) {
	public static final Transformation EMPTY = new Transformation(TransformationType.EMPTY, null, null);

	public static Transformation get(Entity entity) {
		return entity instanceof LivingEntity livingEntity ? livingEntity.getTransformedLook() : EMPTY;
	}

	public void updateState(LivingEntity owner) {
		if (this.entity != null) {
			this.entity.updateTransformationState(owner);
		}
	}

	public float getScale() {
		return this.type.scale();
	}

	public double getCameraOffset(double multiplier) {
		return (double)Math.max(this.getHeightModifier(), 0.5F) * multiplier;
	}

	public float getReachDistance(float original) {
		return MathHelper.clamp(this.getHeightModifier(), 1.0F, 10.0F) * original;
	}

	private float getHeightModifier() {
		if (this.entity == null) {
			return this.getScale();
		} else {
			float f = this.entity.getHeight() * this.getScale();
			float g = EntityType.PLAYER.getHeight();
			return f / g;
		}
	}

	public EntityDimensions scaleDimensions(EntityPose pose, EntityDimensions original) {
		return this.entity == null ? original.scaled(this.getScale()) : this.entity.getDimensions(pose).scaled(this.getScale());
	}

	public float getEyeHeight(EntityPose pose, float original) {
		return this.entity == null ? original * this.getScale() : this.entity.getEyeHeight(pose) * this.getScale();
	}

	public float getStepHeight(float original) {
		float f = this.entity != null ? this.entity.getStepHeight() : original;
		float g = Math.max(this.getScale(), 1.0F);
		return Math.max(f * g, original);
	}

	public boolean isEmpty() {
		return this.type.isEmpty();
	}

	public boolean canFly() {
		return this.entity != null && this.entity.canFly();
	}

	public boolean canBreatheInWater() {
		if (this.entity == null) {
			return false;
		} else {
			return this.entity instanceof LivingEntity livingEntity ? livingEntity.canBreatheInWater() : true;
		}
	}

	public boolean isLandBased() {
		return this.entity instanceof LivingEntity livingEntity ? livingEntity.isLandBased() : true;
	}

	public boolean isHurtByWater() {
		if (this.entity instanceof LivingEntity livingEntity && livingEntity.hurtByWater()) {
			return true;
		}

		return false;
	}
}
