package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ModelRotationContainer {
	default ModelRotation getRotation() {
		return ModelRotation.X0_Y0;
	}

	default boolean isUvLocked() {
		return false;
	}
}
