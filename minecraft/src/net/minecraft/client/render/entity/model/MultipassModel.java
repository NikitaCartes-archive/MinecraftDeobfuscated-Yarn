package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public interface MultipassModel {
	void renderPass(Entity entity, float f, float g, float h, float i, float j, float k);
}
