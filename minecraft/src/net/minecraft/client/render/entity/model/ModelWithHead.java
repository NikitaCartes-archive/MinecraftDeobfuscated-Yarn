package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;

@Environment(EnvType.CLIENT)
public interface ModelWithHead {
	ModelPart getHead();

	default void setHeadAngle(float f) {
		this.getHead().applyTransform(f);
	}
}
