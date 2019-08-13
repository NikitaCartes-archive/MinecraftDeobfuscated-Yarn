package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ModelBakeSettings {
	default ModelRotation getRotation() {
		return ModelRotation.field_5350;
	}

	default boolean isUvLocked() {
		return false;
	}
}
