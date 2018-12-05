package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.vehicle.MobSpawnerMinecartEntity;

@Environment(EnvType.CLIENT)
public class MinecartSpawnerEntityRenderer extends MinecartEntityRenderer<MobSpawnerMinecartEntity> {
	public MinecartSpawnerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}
}
