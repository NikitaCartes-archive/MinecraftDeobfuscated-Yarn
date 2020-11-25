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

    public static <T extends MobEntity> void method_29351(ModelPart modelPart, ModelPart modelPart2, T mobEntity, float f, float g) {
        float h = MathHelper.sin(f * (float)Math.PI);
        float i = MathHelper.sin((1.0f - (1.0f - f) * (1.0f - f)) * (float)Math.PI);
        modelPart.roll = 0.0f;
        modelPart2.roll = 0.0f;
        modelPart.yaw = 0.15707964f;
        modelPart2.yaw = -0.15707964f;
        if (mobEntity.getMainArm() == Arm.RIGHT) {
            modelPart.pitch = -1.8849558f + MathHelper.cos(g * 0.09f) * 0.15f;
            modelPart2.pitch = -0.0f + MathHelper.cos(g * 0.19f) * 0.5f;
            modelPart.pitch += h * 2.2f - i * 0.4f;
            modelPart2.pitch += h * 1.2f - i * 0.4f;
        } else {
            modelPart.pitch = -0.0f + MathHelper.cos(g * 0.19f) * 0.5f;
            modelPart2.pitch = -1.8849558f + MathHelper.cos(g * 0.09f) * 0.15f;
            modelPart.pitch += h * 1.2f - i * 0.4f;
            modelPart2.pitch += h * 2.2f - i * 0.4f;
        }
        CrossbowPosing.method_32789(modelPart, modelPart2, g);
    }

    public static void method_29350(ModelPart modelPart, float f, float g) {
        modelPart.roll += g * (MathHelper.cos(f * 0.09f) * 0.05f + 0.05f);
        modelPart.pitch += g * (MathHelper.sin(f * 0.067f) * 0.05f);
    }

    public static void method_32789(ModelPart modelPart, ModelPart modelPart2, float f) {
        CrossbowPosing.method_29350(modelPart, f, 1.0f);
        CrossbowPosing.method_29350(modelPart2, f, -1.0f);
    }

    public static void method_29352(ModelPart modelPart, ModelPart modelPart2, boolean bl, float f, float g) {
        float j;
        float h = MathHelper.sin(f * (float)Math.PI);
        float i = MathHelper.sin((1.0f - (1.0f - f) * (1.0f - f)) * (float)Math.PI);
        modelPart2.roll = 0.0f;
        modelPart.roll = 0.0f;
        modelPart2.yaw = -(0.1f - h * 0.6f);
        modelPart.yaw = 0.1f - h * 0.6f;
        modelPart2.pitch = j = (float)(-Math.PI) / (bl ? 1.5f : 2.25f);
        modelPart.pitch = j;
        modelPart2.pitch += h * 1.2f - i * 0.4f;
        modelPart.pitch += h * 1.2f - i * 0.4f;
        CrossbowPosing.method_32789(modelPart2, modelPart, g);
    }
}

