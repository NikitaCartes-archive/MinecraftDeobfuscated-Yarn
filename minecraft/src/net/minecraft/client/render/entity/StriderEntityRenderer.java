package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.StriderEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class StriderEntityRenderer extends MobEntityRenderer<StriderEntity, StriderEntityModel<StriderEntity>> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/strider/strider.png");
	private static final Identifier COLD_TEXTURE = Identifier.ofVanilla("textures/entity/strider/strider_cold.png");
	private static final float BABY_SHADOW_RADIUS_SCALE = 0.5F;

	public StriderEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new StriderEntityModel<>(context.getPart(EntityModelLayers.STRIDER)), 0.5F);
		this.addFeature(
			new SaddleFeatureRenderer<>(
				this, new StriderEntityModel<>(context.getPart(EntityModelLayers.STRIDER_SADDLE)), Identifier.ofVanilla("textures/entity/strider/strider_saddle.png")
			)
		);
	}

	public Identifier getTexture(StriderEntity striderEntity) {
		return striderEntity.isCold() ? COLD_TEXTURE : TEXTURE;
	}

	protected float getShadowRadius(StriderEntity striderEntity) {
		float f = super.getShadowRadius(striderEntity);
		return striderEntity.isBaby() ? f * 0.5F : f;
	}

	protected void scale(StriderEntity striderEntity, MatrixStack matrixStack, float f) {
		float g = striderEntity.getScaleFactor();
		matrixStack.scale(g, g, g);
	}

	protected boolean isShaking(StriderEntity striderEntity) {
		return super.isShaking(striderEntity) || striderEntity.isCold();
	}
}
