package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class ZombieVillagerEntityRenderer extends BipedEntityRenderer<ZombieVillagerEntity, ZombieVillagerEntityModel<ZombieVillagerEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/zombie_villager/zombie_villager.png");

	public ZombieVillagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ReloadableResourceManager reloadableResourceManager) {
		super(entityRenderDispatcher, new ZombieVillagerEntityModel<>(RenderLayer::getEntityCutoutNoCull, 0.0F, false), 0.5F);
		this.addFeature(
			new ArmorBipedFeatureRenderer<>(
				this, new ZombieVillagerEntityModel(RenderLayer::getEntitySolid, 0.5F, true), new ZombieVillagerEntityModel(RenderLayer::getEntitySolid, 1.0F, true)
			)
		);
		this.addFeature(new VillagerClothingFeatureRenderer<>(this, reloadableResourceManager, "zombie_villager"));
	}

	public Identifier method_4175(ZombieVillagerEntity zombieVillagerEntity) {
		return SKIN;
	}

	protected void method_4176(ZombieVillagerEntity zombieVillagerEntity, MatrixStack matrixStack, float f, float g, float h) {
		if (zombieVillagerEntity.isConverting()) {
			g += (float)(Math.cos((double)zombieVillagerEntity.age * 3.25) * Math.PI * 0.25);
		}

		super.setupTransforms(zombieVillagerEntity, matrixStack, f, g, h);
	}
}
