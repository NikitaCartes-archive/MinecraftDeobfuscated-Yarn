package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.util.Arm;

@Environment(EnvType.CLIENT)
public interface ModelWithArms {
	void setArmAngle(float f, Arm arm, class_4587 arg);
}
