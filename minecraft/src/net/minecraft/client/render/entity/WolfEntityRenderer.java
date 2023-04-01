package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.CrownFeatureRenderer;
import net.minecraft.client.render.entity.feature.WolfCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WolfEntityRenderer extends MobEntityRenderer<WolfEntity, WolfEntityModel<WolfEntity>> {
	private static final Identifier WILD_TEXTURE = new Identifier("textures/entity/wolf/wolf.png");
	private static final Identifier TAMED_TEXTURE = new Identifier("textures/entity/wolf/wolf_tame.png");
	private static final Identifier ANGRY_TEXTURE = new Identifier("textures/entity/wolf/wolf_angry.png");

	public WolfEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new WolfEntityModel<>(context.getPart(EntityModelLayers.WOLF)), 0.5F);
		this.addFeature(new WolfCollarFeatureRenderer(this));
		this.addFeature(new CrownFeatureRenderer<>(this, context.getModelLoader()));
	}

	protected float getAnimationProgress(WolfEntity wolfEntity, float f) {
		return wolfEntity.getTailAngle();
	}

	public void render(WolfEntity wolfEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		if (wolfEntity.isFurWet()) {
			float h = wolfEntity.getFurWetBrightnessMultiplier(g);
			this.model.setColorMultiplier(h, h, h);
		}

		super.render(wolfEntity, f, g, matrixStack, vertexConsumerProvider, i);
		if (wolfEntity.isFurWet()) {
			this.model.setColorMultiplier(1.0F, 1.0F, 1.0F);
		}
	}

	public Identifier getTexture(WolfEntity wolfEntity) {
		if (wolfEntity.isTamed()) {
			return TAMED_TEXTURE;
		} else {
			return wolfEntity.hasAngerTime() ? ANGRY_TEXTURE : WILD_TEXTURE;
		}
	}
}
