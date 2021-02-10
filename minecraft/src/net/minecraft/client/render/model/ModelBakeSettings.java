package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.AffineTransformation;

@Environment(EnvType.CLIENT)
public interface ModelBakeSettings {
	default AffineTransformation getRotation() {
		return AffineTransformation.identity();
	}

	default boolean isUvLocked() {
		return false;
	}
}
