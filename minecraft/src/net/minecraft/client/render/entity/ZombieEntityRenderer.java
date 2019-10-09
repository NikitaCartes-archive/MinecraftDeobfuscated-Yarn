package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.entity.mob.ZombieEntity;

@Environment(EnvType.CLIENT)
public class ZombieEntityRenderer extends ZombieBaseEntityRenderer<ZombieEntity, ZombieEntityModel<ZombieEntity>> {
	public ZombieEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(
			entityRenderDispatcher,
			new ZombieEntityModel<>(RenderLayer::getEntitySolid, 0.0F, false),
			new ZombieEntityModel<>(RenderLayer::getEntitySolid, 0.5F, true),
			new ZombieEntityModel<>(RenderLayer::getEntitySolid, 1.0F, true)
		);
	}
}
