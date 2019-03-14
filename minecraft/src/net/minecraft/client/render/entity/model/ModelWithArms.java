package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.AbsoluteHand;

@Environment(EnvType.CLIENT)
public interface ModelWithArms {
	void setArmAngle(float f, AbsoluteHand absoluteHand);
}
