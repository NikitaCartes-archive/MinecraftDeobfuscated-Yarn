package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class ZombieBaseEntityRenderer<T extends ZombieEntity, M extends ZombieEntityModel<T>> extends BipedEntityRenderer<T, M> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/zombie/zombie.png");

	protected ZombieBaseEntityRenderer(EntityRenderDispatcher dispatcher, M zombieEntityModel, M zombieEntityModel2, M zombieEntityModel3) {
		super(dispatcher, zombieEntityModel, 0.5F);
		this.addFeature(new ArmorFeatureRenderer<>(this, zombieEntityModel2, zombieEntityModel3));
	}

	public Identifier method_4163(ZombieEntity zombieEntity) {
		return TEXTURE;
	}

	protected boolean method_25449(T zombieEntity) {
		return zombieEntity.isConvertingInWater();
	}
}
