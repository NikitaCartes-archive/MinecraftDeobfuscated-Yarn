package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;

@Environment(EnvType.CLIENT)
public interface ModelWithHead {
	Cuboid getHead();

	default void setHeadAngle(float f) {
		this.getHead().applyTransform(f);
	}
}
