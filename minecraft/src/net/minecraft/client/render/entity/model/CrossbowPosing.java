package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

/**
 * Utility class to help posing when a crossbow is involved.
 */
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
		float f = (float)CrossbowItem.getPullTime(actor.getActiveItem(), actor);
		float g = MathHelper.clamp((float)actor.getItemUseTime(), 0.0F, f);
		float h = g / f;
		modelPart2.yaw = MathHelper.lerp(h, 0.4F, 0.85F) * (float)(rightArmed ? 1 : -1);
		modelPart2.pitch = MathHelper.lerp(h, modelPart2.pitch, (float) (-Math.PI / 2));
	}

	public static <T extends MobEntity> void meleeAttack(ModelPart leftArm, ModelPart rightArm, T actor, float swingProgress, float animationProgress) {
		float f = MathHelper.sin(swingProgress * (float) Math.PI);
		float g = MathHelper.sin((1.0F - (1.0F - swingProgress) * (1.0F - swingProgress)) * (float) Math.PI);
		leftArm.roll = 0.0F;
		rightArm.roll = 0.0F;
		leftArm.yaw = (float) (Math.PI / 20);
		rightArm.yaw = (float) (-Math.PI / 20);
		if (actor.getMainArm() == Arm.RIGHT) {
			leftArm.pitch = -1.8849558F + MathHelper.cos(animationProgress * 0.09F) * 0.15F;
			rightArm.pitch = -0.0F + MathHelper.cos(animationProgress * 0.19F) * 0.5F;
			leftArm.pitch += f * 2.2F - g * 0.4F;
			rightArm.pitch += f * 1.2F - g * 0.4F;
		} else {
			leftArm.pitch = -0.0F + MathHelper.cos(animationProgress * 0.19F) * 0.5F;
			rightArm.pitch = -1.8849558F + MathHelper.cos(animationProgress * 0.09F) * 0.15F;
			leftArm.pitch += f * 1.2F - g * 0.4F;
			rightArm.pitch += f * 2.2F - g * 0.4F;
		}

		swingArms(leftArm, rightArm, animationProgress);
	}

	public static void swingArm(ModelPart arm, float animationProgress, float sigma) {
		arm.roll = arm.roll + sigma * (MathHelper.cos(animationProgress * 0.09F) * 0.05F + 0.05F);
		arm.pitch = arm.pitch + sigma * MathHelper.sin(animationProgress * 0.067F) * 0.05F;
	}

	public static void swingArms(ModelPart leftArm, ModelPart rightArm, float animationProgress) {
		swingArm(leftArm, animationProgress, 1.0F);
		swingArm(rightArm, animationProgress, -1.0F);
	}

	public static void meleeAttack(ModelPart leftArm, ModelPart rightArm, boolean attacking, float swingProgress, float animationProgress) {
		float f = MathHelper.sin(swingProgress * (float) Math.PI);
		float g = MathHelper.sin((1.0F - (1.0F - swingProgress) * (1.0F - swingProgress)) * (float) Math.PI);
		rightArm.roll = 0.0F;
		leftArm.roll = 0.0F;
		rightArm.yaw = -(0.1F - f * 0.6F);
		leftArm.yaw = 0.1F - f * 0.6F;
		float h = (float) -Math.PI / (attacking ? 1.5F : 2.25F);
		rightArm.pitch = h;
		leftArm.pitch = h;
		rightArm.pitch += f * 1.2F - g * 0.4F;
		leftArm.pitch += f * 1.2F - g * 0.4F;
		swingArms(rightArm, leftArm, animationProgress);
	}
}
