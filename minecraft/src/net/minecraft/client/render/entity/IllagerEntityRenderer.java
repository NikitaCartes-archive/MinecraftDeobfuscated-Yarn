package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.entity.mob.IllagerEntity;

@Environment(EnvType.CLIENT)
public abstract class IllagerEntityRenderer<T extends IllagerEntity> extends MobEntityRenderer<T, IllagerEntityModel<T>> {
	protected IllagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, IllagerEntityModel<T> illagerEntityModel, float f) {
		super(entityRenderDispatcher, illagerEntityModel, f);
		this.addFeature(new HeadFeatureRenderer<>(this));
	}

	public IllagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new IllagerEntityModel<>(0.0F, 0.0F, 64, 64), 0.5F);
		this.addFeature(new HeadFeatureRenderer<>(this));
	}

	protected void scale(T illagerEntity, float f) {
		float g = 0.9375F;
		GlStateManager.scalef(0.9375F, 0.9375F, 0.9375F);
	}
}
