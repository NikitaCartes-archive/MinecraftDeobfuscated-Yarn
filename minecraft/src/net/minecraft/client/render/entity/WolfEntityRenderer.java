package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.WolfArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.WolfCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

@Environment(EnvType.CLIENT)
public class WolfEntityRenderer extends MobEntityRenderer<WolfEntity, WolfEntityModel<WolfEntity>> {
	public WolfEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new WolfEntityModel<>(context.getPart(EntityModelLayers.WOLF)), 0.5F);
		this.addFeature(new WolfArmorFeatureRenderer(this, context.getModelLoader()));
		this.addFeature(new WolfCollarFeatureRenderer(this));
	}

	protected float getAnimationProgress(WolfEntity wolfEntity, float f) {
		return wolfEntity.getTailAngle();
	}

	public void render(WolfEntity wolfEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		if (wolfEntity.isFurWet()) {
			float h = wolfEntity.getFurWetBrightnessMultiplier(g);
			this.model.setColorMultiplier(ColorHelper.Argb.fromFloats(1.0F, h, h, h));
		}

		super.render(wolfEntity, f, g, matrixStack, vertexConsumerProvider, i);
		if (wolfEntity.isFurWet()) {
			this.model.setColorMultiplier(-1);
		}
	}

	public Identifier getTexture(WolfEntity wolfEntity) {
		return wolfEntity.getTextureId();
	}
}
