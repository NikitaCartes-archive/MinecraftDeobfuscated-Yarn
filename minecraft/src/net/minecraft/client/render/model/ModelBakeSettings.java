package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Rotation3;

@Environment(EnvType.CLIENT)
public interface ModelBakeSettings {
	default Rotation3 getRotation() {
		return Rotation3.identity();
	}

	default boolean isShaded() {
		return false;
	}
}
