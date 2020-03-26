package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.PigSaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.StriderEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class StriderEntityRenderer extends MobEntityRenderer<StriderEntity, StriderEntityModel<StriderEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/strider/strider.png");

	public StriderEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new StriderEntityModel<>(), 0.5F);
		this.addFeature(new PigSaddleFeatureRenderer<>(this, new StriderEntityModel<>(), new Identifier("textures/entity/strider/strider_saddle.png")));
	}

	public Identifier getTexture(StriderEntity striderEntity) {
		return TEXTURE;
	}

	protected void scale(StriderEntity striderEntity, MatrixStack matrixStack, float f) {
		float g = 0.9375F;
		if (striderEntity.isBaby()) {
			g *= 0.5F;
			this.shadowRadius = 0.25F;
		} else {
			this.shadowRadius = 0.5F;
		}

		matrixStack.scale(g, g, g);
	}

	protected boolean isShaking(StriderEntity striderEntity) {
		return striderEntity.isCold();
	}
}
