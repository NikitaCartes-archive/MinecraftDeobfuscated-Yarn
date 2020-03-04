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
        holdingArm.yaw = (rightArmed ? -0.3f : 0.3f) + head.yaw;
        otherArm.yaw = (rightArmed ? 0.6f : -0.6f) + head.yaw;
        holdingArm.pitch = -1.5707964f + head.pitch + 0.1f;
        otherArm.pitch = -1.5f + head.pitch;
    }

    public static void charge(ModelPart holdingArm, ModelPart pullingArm, LivingEntity actor, boolean rightArmed) {
        holdingArm.yaw = rightArmed ? -0.8f : 0.8f;
        pullingArm.pitch = holdingArm.pitch = -0.97079635f;
        float f = CrossbowItem.getPullTime(actor.getActiveItem());
        float g = MathHelper.clamp((float)actor.getItemUseTime(), 0.0f, f);
        float h = g / f;
        pullingArm.yaw = MathHelper.lerp(h, 0.4f, 0.85f) * (float)(rightArmed ? 1 : -1);
        pullingArm.pitch = MathHelper.lerp(h, pullingArm.pitch, -1.5707964f);
    }
}

