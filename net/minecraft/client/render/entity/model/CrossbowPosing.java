/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
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
}

