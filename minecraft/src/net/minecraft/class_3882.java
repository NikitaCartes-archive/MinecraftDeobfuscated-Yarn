package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;

@Environment(EnvType.CLIENT)
public interface class_3882 {
	Cuboid getHead();

	default void applyHeadTransform(float f) {
		this.getHead().applyTransform(f);
	}
}
