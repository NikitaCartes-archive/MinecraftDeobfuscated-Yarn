package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ElytraEntityModel<T extends LivingEntity> extends EntityModel<T> {
	private final Cuboid field_3364;
	private final Cuboid field_3365 = new Cuboid(this, 22, 0);

	public ElytraEntityModel() {
		this.field_3365.addBox(-10.0F, 0.0F, 0.0F, 10, 20, 2, 1.0F);
		this.field_3364 = new Cuboid(this, 22, 0);
		this.field_3364.mirror = true;
		this.field_3364.addBox(0.0F, 0.0F, 0.0F, 10, 20, 2, 1.0F);
	}

	public void method_17078(T livingEntity, float f, float g, float h, float i, float j, float k) {
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableCull();
		if (livingEntity.isChild()) {
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(0.0F, 1.5F, -0.1F);
			this.field_3365.render(k);
			this.field_3364.render(k);
			GlStateManager.popMatrix();
		} else {
			this.field_3365.render(k);
			this.field_3364.render(k);
		}
	}

	public void method_17079(T livingEntity, float f, float g, float h, float i, float j, float k) {
		super.setAngles(livingEntity, f, g, h, i, j, k);
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

		this.field_3365.rotationPointX = 5.0F;
		this.field_3365.rotationPointY = n;
		if (livingEntity instanceof AbstractClientPlayerEntity) {
			AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)livingEntity;
			abstractClientPlayerEntity.field_3900 = (float)((double)abstractClientPlayerEntity.field_3900 + (double)(l - abstractClientPlayerEntity.field_3900) * 0.1);
			abstractClientPlayerEntity.field_3899 = (float)((double)abstractClientPlayerEntity.field_3899 + (double)(o - abstractClientPlayerEntity.field_3899) * 0.1);
			abstractClientPlayerEntity.field_3898 = (float)((double)abstractClientPlayerEntity.field_3898 + (double)(m - abstractClientPlayerEntity.field_3898) * 0.1);
			this.field_3365.pitch = abstractClientPlayerEntity.field_3900;
			this.field_3365.yaw = abstractClientPlayerEntity.field_3899;
			this.field_3365.roll = abstractClientPlayerEntity.field_3898;
		} else {
			this.field_3365.pitch = l;
			this.field_3365.roll = m;
			this.field_3365.yaw = o;
		}

		this.field_3364.rotationPointX = -this.field_3365.rotationPointX;
		this.field_3364.yaw = -this.field_3365.yaw;
		this.field_3364.rotationPointY = this.field_3365.rotationPointY;
		this.field_3364.pitch = this.field_3365.pitch;
		this.field_3364.roll = -this.field_3365.roll;
	}
}
