package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5605;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ElytraEntityModel<T extends LivingEntity> extends AnimalModel<T> {
	private final ModelPart field_27412;
	private final ModelPart field_3365;

	public ElytraEntityModel(ModelPart modelPart) {
		this.field_3365 = modelPart.method_32086("left_wing");
		this.field_27412 = modelPart.method_32086("right_wing");
	}

	public static class_5607 method_31994() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		class_5605 lv3 = new class_5605(1.0F);
		lv2.method_32117(
			"left_wing",
			class_5606.method_32108().method_32101(22, 0).method_32098(-10.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, lv3),
			class_5603.method_32091(5.0F, 0.0F, 0.0F, (float) (Math.PI / 12), 0.0F, (float) (-Math.PI / 12))
		);
		lv2.method_32117(
			"right_wing",
			class_5606.method_32108().method_32101(22, 0).method_32096().method_32098(0.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, lv3),
			class_5603.method_32091(-5.0F, 0.0F, 0.0F, (float) (Math.PI / 12), 0.0F, (float) (Math.PI / 12))
		);
		return class_5607.method_32110(lv, 64, 32);
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.<ModelPart>of();
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.<ModelPart>of(this.field_3365, this.field_27412);
	}

	public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {
		float k = (float) (Math.PI / 12);
		float l = (float) (-Math.PI / 12);
		float m = 0.0F;
		float n = 0.0F;
		if (livingEntity.isFallFlying()) {
			float o = 1.0F;
			Vec3d vec3d = livingEntity.getVelocity();
			if (vec3d.y < 0.0) {
				Vec3d vec3d2 = vec3d.normalize();
				o = 1.0F - (float)Math.pow(-vec3d2.y, 1.5);
			}

			k = o * (float) (Math.PI / 9) + (1.0F - o) * k;
			l = o * (float) (-Math.PI / 2) + (1.0F - o) * l;
		} else if (livingEntity.isInSneakingPose()) {
			k = (float) (Math.PI * 2.0 / 9.0);
			l = (float) (-Math.PI / 4);
			m = 3.0F;
			n = 0.08726646F;
		}

		this.field_3365.pivotY = m;
		if (livingEntity instanceof AbstractClientPlayerEntity) {
			AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)livingEntity;
			abstractClientPlayerEntity.elytraPitch = (float)((double)abstractClientPlayerEntity.elytraPitch + (double)(k - abstractClientPlayerEntity.elytraPitch) * 0.1);
			abstractClientPlayerEntity.elytraYaw = (float)((double)abstractClientPlayerEntity.elytraYaw + (double)(n - abstractClientPlayerEntity.elytraYaw) * 0.1);
			abstractClientPlayerEntity.elytraRoll = (float)((double)abstractClientPlayerEntity.elytraRoll + (double)(l - abstractClientPlayerEntity.elytraRoll) * 0.1);
			this.field_3365.pitch = abstractClientPlayerEntity.elytraPitch;
			this.field_3365.yaw = abstractClientPlayerEntity.elytraYaw;
			this.field_3365.roll = abstractClientPlayerEntity.elytraRoll;
		} else {
			this.field_3365.pitch = k;
			this.field_3365.roll = l;
			this.field_3365.yaw = n;
		}

		this.field_27412.yaw = -this.field_3365.yaw;
		this.field_27412.pivotY = this.field_3365.pivotY;
		this.field_27412.pitch = this.field_3365.pitch;
		this.field_27412.roll = -this.field_3365.roll;
	}
}
