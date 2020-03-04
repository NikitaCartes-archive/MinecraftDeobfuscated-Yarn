package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CrossbowPosing {
	public static void hold(ModelPart holdingArm, ModelPart otherArm, ModelPart head, boolean rightArmed) {
		holdingArm.yaw = (rightArmed ? -0.3F : 0.3F) + head.yaw;
		otherArm.yaw = (rightArmed ? 0.6F : -0.6F) + head.yaw;
		holdingArm.pitch = (float) (-Math.PI / 2) + head.pitch + 0.1F;
		otherArm.pitch = -1.5F + head.pitch;
	}

	public static void charge(ModelPart holdingArm, ModelPart pullingArm, LivingEntity actor, boolean rightArmed) {
		holdingArm.yaw = rightArmed ? -0.8F : 0.8F;
		holdingArm.pitch = -0.97079635F;
		pullingArm.pitch = holdingArm.pitch;
		float f = (float)CrossbowItem.getPullTime(actor.getActiveItem());
		float g = MathHelper.clamp((float)actor.getItemUseTime(), 0.0F, f);
		float h = g / f;
		pullingArm.yaw = MathHelper.lerp(h, 0.4F, 0.85F) * (float)(rightArmed ? 1 : -1);
		pullingArm.pitch = MathHelper.lerp(h, pullingArm.pitch, (float) (-Math.PI / 2));
	}
}
