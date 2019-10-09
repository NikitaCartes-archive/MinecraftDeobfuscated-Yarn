package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.entity.mob.ZombiePigmanEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ZombiePigmanEntityRenderer extends BipedEntityRenderer<ZombiePigmanEntity, ZombieEntityModel<ZombiePigmanEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/zombie_pigman.png");

	public ZombiePigmanEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new ZombieEntityModel<>(RenderLayer::getEntityCutoutNoCull, 0.0F, false), 0.5F);
		this.addFeature(
			new ArmorBipedFeatureRenderer<>(
				this, new ZombieEntityModel(RenderLayer::getEntityCutoutNoCull, 0.5F, true), new ZombieEntityModel(RenderLayer::getEntityCutoutNoCull, 1.0F, true)
			)
		);
	}

	public Identifier method_4093(ZombiePigmanEntity zombiePigmanEntity) {
		return SKIN;
	}
}
