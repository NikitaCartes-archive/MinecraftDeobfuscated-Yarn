package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.StriderEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class StriderEntityRenderer extends MobEntityRenderer<StriderEntity, StriderEntityModel<StriderEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/strider/strider.png");
	private static final Identifier COLD_TEXTURE = new Identifier("textures/entity/strider/strider_cold.png");

	public StriderEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new StriderEntityModel<>(), 0.5F);
		this.addFeature(new SaddleFeatureRenderer<>(this, new StriderEntityModel<>(), new Identifier("textures/entity/strider/strider_saddle.png")));
	}

	public Identifier getTexture(StriderEntity striderEntity) {
		return striderEntity.isCold() ? COLD_TEXTURE : TEXTURE;
	}

	protected void scale(StriderEntity striderEntity, MatrixStack matrixStack, float f) {
		if (striderEntity.isBaby()) {
			matrixStack.scale(0.5F, 0.5F, 0.5F);
			this.shadowRadius = 0.25F;
		} else {
			this.shadowRadius = 0.5F;
		}
	}

	protected boolean isShaking(StriderEntity striderEntity) {
		return striderEntity.isCold();
	}
}
