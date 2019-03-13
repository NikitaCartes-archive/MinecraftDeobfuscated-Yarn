package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class ZombieBaseEntityRenderer<T extends ZombieEntity, M extends ZombieEntityModel<T>> extends BipedEntityRenderer<T, M> {
	private static final Identifier field_4819 = new Identifier("textures/entity/zombie/zombie.png");

	protected ZombieBaseEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, M zombieEntityModel, M zombieEntityModel2, M zombieEntityModel3) {
		super(entityRenderDispatcher, zombieEntityModel, 0.5F);
		this.method_4046(new ArmorBipedFeatureRenderer<>(this, zombieEntityModel2, zombieEntityModel3));
	}

	protected Identifier method_4163(ZombieEntity zombieEntity) {
		return field_4819;
	}

	protected void method_17144(T zombieEntity, float f, float g, float h) {
		if (zombieEntity.isDrowning()) {
			g += (float)(Math.cos((double)zombieEntity.age * 3.25) * Math.PI * 0.25);
		}

		super.setupTransforms(zombieEntity, f, g, h);
	}
}
