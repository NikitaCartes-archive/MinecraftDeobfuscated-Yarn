package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.EvilVillagerEntityModel;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public abstract class IllagerEntityRenderer<T extends IllagerEntity> extends MobEntityRenderer<T, EvilVillagerEntityModel<T>> {
	protected IllagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, EvilVillagerEntityModel<T> evilVillagerEntityModel, float f) {
		super(entityRenderDispatcher, evilVillagerEntityModel, f);
		this.addFeature(new HeadFeatureRenderer<>(this));
	}

	protected void method_16460(T illagerEntity, MatrixStack matrixStack, float f) {
		float g = 0.9375F;
		matrixStack.scale(0.9375F, 0.9375F, 0.9375F);
	}
}
