package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CrossbowPosing {
	public static void hold(ModelPart holdingArm, ModelPart otherArm, ModelPart head, boolean rightArmed) {
		ModelPart modelPart = rightArmed ? holdingArm : otherArm;
		ModelPart modelPart2 = rightArmed ? otherArm : holdingArm;
		modelPart.yaw = (rightArmed ? -0.3F : 0.3F) + head.yaw;
		modelPart2.yaw = (rightArmed ? 0.6F : -0.6F) + head.yaw;
		modelPart.pitch = (float) (-Math.PI / 2) + head.pitch + 0.1F;
		modelPart2.pitch = -1.5F + head.pitch;
	}

	public static void charge(ModelPart holdingArm, ModelPart pullingArm, LivingEntity actor, boolean rightArmed) {
		ModelPart modelPart = rightArmed ? holdingArm : pullingArm;
		ModelPart modelPart2 = rightArmed ? pullingArm : holdingArm;
		modelPart.yaw = rightArmed ? -0.8F : 0.8F;
		modelPart.pitch = -0.97079635F;
		modelPart2.pitch = modelPart.pitch;
		float f = (float)CrossbowItem.getPullTime(actor.getActiveItem());
		float g = MathHelper.clamp((float)actor.getItemUseTime(), 0.0F, f);
		float h = g / f;
		modelPart2.yaw = MathHelper.lerp(h, 0.4F, 0.85F) * (float)(rightArmed ? 1 : -1);
		modelPart2.pitch = MathHelper.lerp(h, modelPart2.pitch, (float) (-Math.PI / 2));
	}

	public static <T extends MobEntity> void method_29351(ModelPart modelPart, ModelPart modelPart2, T mobEntity, float f, float g) {
		float h = MathHelper.sin(f * (float) Math.PI);
		float i = MathHelper.sin((1.0F - (1.0F - f) * (1.0F - f)) * (float) Math.PI);
		modelPart.roll = 0.0F;
		modelPart2.roll = 0.0F;
		modelPart.yaw = (float) (Math.PI / 20);
		modelPart2.yaw = (float) (-Math.PI / 20);
		if (mobEntity.getMainArm() == Arm.RIGHT) {
			modelPart.pitch = -1.8849558F + MathHelper.cos(g * 0.09F) * 0.15F;
			modelPart2.pitch = -0.0F + MathHelper.cos(g * 0.19F) * 0.5F;
			modelPart.pitch += h * 2.2F - i * 0.4F;
			modelPart2.pitch += h * 1.2F - i * 0.4F;
		} else {
			modelPart.pitch = -0.0F + MathHelper.cos(g * 0.19F) * 0.5F;
			modelPart2.pitch = -1.8849558F + MathHelper.cos(g * 0.09F) * 0.15F;
			modelPart.pitch += h * 1.2F - i * 0.4F;
			modelPart2.pitch += h * 2.2F - i * 0.4F;
		}

		method_29350(modelPart, modelPart2, g);
	}

	public static void method_29350(ModelPart modelPart, ModelPart modelPart2, float f) {
		modelPart.roll = modelPart.roll + MathHelper.cos(f * 0.09F) * 0.05F + 0.05F;
		modelPart2.roll = modelPart2.roll - (MathHelper.cos(f * 0.09F) * 0.05F + 0.05F);
		modelPart.pitch = modelPart.pitch + MathHelper.sin(f * 0.067F) * 0.05F;
		modelPart2.pitch = modelPart2.pitch - MathHelper.sin(f * 0.067F) * 0.05F;
	}

	public static void method_29352(ModelPart modelPart, ModelPart modelPart2, boolean bl, float f, float g) {
		float h = MathHelper.sin(f * (float) Math.PI);
		float i = MathHelper.sin((1.0F - (1.0F - f) * (1.0F - f)) * (float) Math.PI);
		modelPart2.roll = 0.0F;
		modelPart.roll = 0.0F;
		modelPart2.yaw = -(0.1F - h * 0.6F);
		modelPart.yaw = 0.1F - h * 0.6F;
		float j = (float) -Math.PI / (bl ? 1.5F : 2.25F);
		modelPart2.pitch = j;
		modelPart.pitch = j;
		modelPart2.pitch += h * 1.2F - i * 0.4F;
		modelPart.pitch += h * 1.2F - i * 0.4F;
		method_29350(modelPart2, modelPart, g);
	}
}
