package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public interface LayerEntityRenderer<E extends LivingEntity> {
	void render(E livingEntity, float f, float g, float h, float i, float j, float k, float l);

	boolean shouldMergeTextures();
}
