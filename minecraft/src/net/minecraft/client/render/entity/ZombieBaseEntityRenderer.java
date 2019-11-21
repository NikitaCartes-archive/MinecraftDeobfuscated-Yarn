package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class ZombieBaseEntityRenderer<T extends ZombieEntity, M extends ZombieEntityModel<T>> extends BipedEntityRenderer<T, M> {
	private static final Identifier SKIN = new Identifier("textures/entity/zombie/zombie.png");

	protected ZombieBaseEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, M zombieEntityModel, M zombieEntityModel2, M zombieEntityModel3) {
		super(entityRenderDispatcher, zombieEntityModel, 0.5F);
		this.addFeature(new ArmorBipedFeatureRenderer<>(this, zombieEntityModel2, zombieEntityModel3));
	}

	public Identifier getTexture(ZombieEntity zombieEntity) {
		return SKIN;
	}

	protected void setupTransforms(T zombieEntity, MatrixStack matrixStack, float f, float g, float h) {
		if (zombieEntity.isConvertingInWater()) {
			g += (float)(Math.cos((double)zombieEntity.age * 3.25) * Math.PI * 0.25);
		}

		super.setupTransforms(zombieEntity, matrixStack, f, g, h);
	}
}
