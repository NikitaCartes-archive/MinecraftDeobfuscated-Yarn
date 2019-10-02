package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public interface ModelWithArms {
	void setArmAngle(float f, Arm arm, MatrixStack matrixStack);
}
