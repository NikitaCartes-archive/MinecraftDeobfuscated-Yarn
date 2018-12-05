package net.minecraft.client.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ChestAnimationProgress {
	float getAnimationProgress(float f);
}
