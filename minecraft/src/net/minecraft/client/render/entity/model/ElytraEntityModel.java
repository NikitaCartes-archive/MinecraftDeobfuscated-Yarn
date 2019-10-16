package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ElytraEntityModel<T extends LivingEntity> extends AnimalModel<T> {
	private final ModelPart field_3364;
	private final ModelPart field_3365 = new ModelPart(this, 22, 0);

	public ElytraEntityModel() {
		this.field_3365.addCuboid(-10.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, 1.0F);
		this.field_3364 = new ModelPart(this, 22, 0);
		this.field_3364.mirror = true;
		this.field_3364.addCuboid(0.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, 1.0F);
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.<ModelPart>of();
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.<ModelPart>of(this.field_3365, this.field_3364);
	}

	public void method_17079(T livingEntity, float f, float g, float h, float i, float j, float k) {
		float l = (float) (Math.PI / 12);
		float m = (float) (-Math.PI / 12);
		float n = 0.0F;
		float o = 0.0F;
		if (livingEntity.isFallFlying()) {
			float p = 1.0F;
			Vec3d vec3d = livingEntity.getVelocity();
			if (vec3d.y < 0.0) {
				Vec3d vec3d2 = vec3d.normalize();
				p = 1.0F - (float)Math.pow(-vec3d2.y, 1.5);
			}

			l = p * (float) (Math.PI / 9) + (1.0F - p) * l;
			m = p * (float) (-Math.PI / 2) + (1.0F - p) * m;
		} else if (livingEntity.isInSneakingPose()) {
			l = (float) (Math.PI * 2.0 / 9.0);
			m = (float) (-Math.PI / 4);
			n = 3.0F;
			o = 0.08726646F;
		}

		this.field_3365.pivotX = 5.0F;
		this.field_3365.pivotY = n;
		if (livingEntity instanceof AbstractClientPlayerEntity) {
			AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)livingEntity;
			abstractClientPlayerEntity.elytraPitch = (float)((double)abstractClientPlayerEntity.elytraPitch + (double)(l - abstractClientPlayerEntity.elytraPitch) * 0.1);
			abstractClientPlayerEntity.elytraYaw = (float)((double)abstractClientPlayerEntity.elytraYaw + (double)(o - abstractClientPlayerEntity.elytraYaw) * 0.1);
			abstractClientPlayerEntity.elytraRoll = (float)((double)abstractClientPlayerEntity.elytraRoll + (double)(m - abstractClientPlayerEntity.elytraRoll) * 0.1);
			this.field_3365.pitch = abstractClientPlayerEntity.elytraPitch;
			this.field_3365.yaw = abstractClientPlayerEntity.elytraYaw;
			this.field_3365.roll = abstractClientPlayerEntity.elytraRoll;
		} else {
			this.field_3365.pitch = l;
			this.field_3365.roll = m;
			this.field_3365.yaw = o;
		}

		this.field_3364.pivotX = -this.field_3365.pivotX;
		this.field_3364.yaw = -this.field_3365.yaw;
		this.field_3364.pivotY = this.field_3365.pivotY;
		this.field_3364.pitch = this.field_3365.pitch;
		this.field_3364.roll = -this.field_3365.roll;
	}
}
