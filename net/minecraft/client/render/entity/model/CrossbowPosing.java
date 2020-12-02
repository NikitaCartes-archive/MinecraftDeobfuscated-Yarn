/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class CrossbowPosing {
    public static void hold(ModelPart holdingArm, ModelPart otherArm, ModelPart head, boolean rightArmed) {
        ModelPart modelPart = rightArmed ? holdingArm : otherArm;
        ModelPart modelPart2 = rightArmed ? otherArm : holdingArm;
        modelPart.yaw = (rightArmed ? -0.3f : 0.3f) + head.yaw;
        modelPart2.yaw = (rightArmed ? 0.6f : -0.6f) + head.yaw;
        modelPart.pitch = -1.5707964f + head.pitch + 0.1f;
        modelPart2.pitch = -1.5f + head.pitch;
    }

    public static void charge(ModelPart holdingArm, ModelPart pullingArm, LivingEntity actor, boolean rightArmed) {
        ModelPart modelPart = rightArmed ? holdingArm : pullingArm;
        ModelPart modelPart2 = rightArmed ? pullingArm : holdingArm;
        modelPart.yaw = rightArmed ? -0.8f : 0.8f;
        modelPart2.pitch = modelPart.pitch = -0.97079635f;
        float f = CrossbowItem.getPullTime(actor.getActiveItem());
        float g = MathHelper.clamp((float)actor.getItemUseTime(), 0.0f, f);
        float h = g / f;
        modelPart2.yaw = MathHelper.lerp(h, 0.4f, 0.85f) * (float)(rightArmed ? 1 : -1);
        modelPart2.pitch = MathHelper.lerp(h, modelPart2.pitch, -1.5707964f);
    }

    public static <T extends MobEntity> void meleeAttack(ModelPart leftArm, ModelPart rightArm, T actor, float swingProgress, float animationProgress) {
        float f = MathHelper.sin(swingProgress * (float)Math.PI);
        float g = MathHelper.sin((1.0f - (1.0f - swingProgress) * (1.0f - swingProgress)) * (float)Math.PI);
        leftArm.roll = 0.0f;
        rightArm.roll = 0.0f;
        leftArm.yaw = 0.15707964f;
        rightArm.yaw = -0.15707964f;
        if (actor.getMainArm() == Arm.RIGHT) {
            leftArm.pitch = -1.8849558f + MathHelper.cos(animationProgress * 0.09f) * 0.15f;
            rightArm.pitch = -0.0f + MathHelper.cos(animationProgress * 0.19f) * 0.5f;
            leftArm.pitch += f * 2.2f - g * 0.4f;
            rightArm.pitch += f * 1.2f - g * 0.4f;
        } else {
            leftArm.pitch = -0.0f + MathHelper.cos(animationProgress * 0.19f) * 0.5f;
            rightArm.pitch = -1.8849558f + MathHelper.cos(animationProgress * 0.09f) * 0.15f;
            leftArm.pitch += f * 1.2f - g * 0.4f;
            rightArm.pitch += f * 2.2f - g * 0.4f;
        }
        CrossbowPosing.swingArms(leftArm, rightArm, animationProgress);
    }

    public static void swingArm(ModelPart arm, float animationProgress, float sigma) {
        arm.roll += sigma * (MathHelper.cos(animationProgress * 0.09f) * 0.05f + 0.05f);
        arm.pitch += sigma * (MathHelper.sin(animationProgress * 0.067f) * 0.05f);
    }

    public static void swingArms(ModelPart leftArm, ModelPart rightArm, float animationProgress) {
        CrossbowPosing.swingArm(leftArm, animationProgress, 1.0f);
        CrossbowPosing.swingArm(rightArm, animationProgress, -1.0f);
    }

    public static void meleeAttack(ModelPart leftArm, ModelPart rightArm, boolean attacking, float swingProgress, float animationProgress) {
        float h;
        float f = MathHelper.sin(swingProgress * (float)Math.PI);
        float g = MathHelper.sin((1.0f - (1.0f - swingProgress) * (1.0f - swingProgress)) * (float)Math.PI);
        rightArm.roll = 0.0f;
        leftArm.roll = 0.0f;
        rightArm.yaw = -(0.1f - f * 0.6f);
        leftArm.yaw = 0.1f - f * 0.6f;
        rightArm.pitch = h = (float)(-Math.PI) / (attacking ? 1.5f : 2.25f);
        leftArm.pitch = h;
        rightArm.pitch += f * 1.2f - g * 0.4f;
        leftArm.pitch += f * 1.2f - g * 0.4f;
        CrossbowPosing.swingArms(rightArm, leftArm, animationProgress);
    }
}

