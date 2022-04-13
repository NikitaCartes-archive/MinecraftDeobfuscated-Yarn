package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.IllagerEntity;

@Environment(EnvType.CLIENT)
public abstract class IllagerEntityRenderer<T extends IllagerEntity> extends MobEntityRenderer<T, IllagerEntityModel<T>> {
	protected IllagerEntityRenderer(EntityRendererFactory.Context ctx, IllagerEntityModel<T> model, float shadowRadius) {
		super(ctx, model, shadowRadius);
		this.addFeature(new HeadFeatureRenderer<>(this, ctx.getModelLoader(), ctx.getHeldItemRenderer()));
	}

	protected void scale(T illagerEntity, MatrixStack matrixStack, float f) {
		float g = 0.9375F;
		matrixStack.scale(0.9375F, 0.9375F, 0.9375F);
	}
}
