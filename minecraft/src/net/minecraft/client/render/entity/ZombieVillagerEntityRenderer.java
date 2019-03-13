package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ZombieVillagerEntityRenderer extends BipedEntityRenderer<ZombieVillagerEntity, ZombieVillagerEntityModel<ZombieVillagerEntity>> {
	private static final Identifier field_4835 = new Identifier("textures/entity/zombie_villager/zombie_villager.png");

	public ZombieVillagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ReloadableResourceManager reloadableResourceManager) {
		super(entityRenderDispatcher, new ZombieVillagerEntityModel<>(), 0.5F);
		this.method_4046(new ArmorBipedFeatureRenderer<>(this, new ZombieVillagerEntityModel(0.5F, true), new ZombieVillagerEntityModel(1.0F, true)));
		this.method_4046(new VillagerClothingFeatureRenderer<>(this, reloadableResourceManager, "zombie_villager"));
	}

	protected Identifier method_4175(ZombieVillagerEntity zombieVillagerEntity) {
		return field_4835;
	}

	protected void method_4176(ZombieVillagerEntity zombieVillagerEntity, float f, float g, float h) {
		if (zombieVillagerEntity.isConverting()) {
			g += (float)(Math.cos((double)zombieVillagerEntity.age * 3.25) * Math.PI * 0.25);
		}

		super.setupTransforms(zombieVillagerEntity, f, g, h);
	}
}
