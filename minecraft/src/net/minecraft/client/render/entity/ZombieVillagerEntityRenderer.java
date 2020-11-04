package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ZombieVillagerEntityRenderer extends BipedEntityRenderer<ZombieVillagerEntity, ZombieVillagerEntityModel<ZombieVillagerEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/zombie_villager/zombie_villager.png");

	public ZombieVillagerEntityRenderer(class_5617.class_5618 arg) {
		super(arg, new ZombieVillagerEntityModel<>(arg.method_32167(EntityModelLayers.ZOMBIE_VILLAGER)), 0.5F);
		this.addFeature(
			new ArmorFeatureRenderer<>(
				this,
				new ZombieVillagerEntityModel(arg.method_32167(EntityModelLayers.ZOMBIE_VILLAGER_INNER_ARMOR)),
				new ZombieVillagerEntityModel(arg.method_32167(EntityModelLayers.ZOMBIE_VILLAGER_OUTER_ARMOR))
			)
		);
		this.addFeature(new VillagerClothingFeatureRenderer<>(this, arg.method_32169(), "zombie_villager"));
	}

	public Identifier getTexture(ZombieVillagerEntity zombieVillagerEntity) {
		return TEXTURE;
	}

	protected boolean isShaking(ZombieVillagerEntity zombieVillagerEntity) {
		return zombieVillagerEntity.isConverting();
	}
}
