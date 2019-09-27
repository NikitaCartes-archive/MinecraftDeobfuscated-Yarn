package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4590;

@Environment(EnvType.CLIENT)
public interface ModelBakeSettings {
	default class_4590 getRotation() {
		return class_4590.method_22931();
	}

	default boolean isUvLocked() {
		return false;
	}
}
