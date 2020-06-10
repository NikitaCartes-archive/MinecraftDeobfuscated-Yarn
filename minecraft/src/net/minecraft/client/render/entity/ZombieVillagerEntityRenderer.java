package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ZombieVillagerEntityRenderer extends BipedEntityRenderer<ZombieVillagerEntity, ZombieVillagerEntityModel<ZombieVillagerEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/zombie_villager/zombie_villager.png");

	public ZombieVillagerEntityRenderer(EntityRenderDispatcher dispatcher, ReloadableResourceManager reloadableResourceManager) {
		super(dispatcher, new ZombieVillagerEntityModel<>(0.0F, false), 0.5F);
		this.addFeature(new ArmorFeatureRenderer<>(this, new ZombieVillagerEntityModel(0.5F, true), new ZombieVillagerEntityModel(1.0F, true)));
		this.addFeature(new VillagerClothingFeatureRenderer<>(this, reloadableResourceManager, "zombie_villager"));
	}

	public Identifier getTexture(ZombieVillagerEntity zombieVillagerEntity) {
		return TEXTURE;
	}

	protected boolean isShaking(ZombieVillagerEntity zombieVillagerEntity) {
		return zombieVillagerEntity.isConverting();
	}
}
