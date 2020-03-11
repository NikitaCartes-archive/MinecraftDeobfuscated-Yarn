package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class ZombieBaseEntityRenderer<T extends ZombieEntity, M extends ZombieEntityModel<T>> extends BipedEntityRenderer<T, M> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/zombie/zombie.png");

	protected ZombieBaseEntityRenderer(EntityRenderDispatcher dispatcher, M zombieEntityModel, M zombieEntityModel2, M zombieEntityModel3) {
		super(dispatcher, zombieEntityModel, 0.5F);
		this.addFeature(new ArmorBipedFeatureRenderer<>(this, zombieEntityModel2, zombieEntityModel3));
	}

	public Identifier getTexture(ZombieEntity zombieEntity) {
		return TEXTURE;
	}

	protected boolean isShaking(T zombieEntity) {
		return zombieEntity.isConvertingInWater();
	}
}
