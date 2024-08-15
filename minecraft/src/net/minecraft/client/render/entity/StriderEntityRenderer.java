package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.StriderEntityModel;
import net.minecraft.client.render.entity.state.StriderEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class StriderEntityRenderer extends MobEntityRenderer<StriderEntity, StriderEntityRenderState, StriderEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/strider/strider.png");
	private static final Identifier COLD_TEXTURE = Identifier.ofVanilla("textures/entity/strider/strider_cold.png");
	private static final float BABY_SHADOW_RADIUS_SCALE = 0.5F;

	public StriderEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new StriderEntityModel(context.getPart(EntityModelLayers.STRIDER)), 0.5F);
		this.addFeature(
			new SaddleFeatureRenderer<>(
				this, new StriderEntityModel(context.getPart(EntityModelLayers.STRIDER_SADDLE)), Identifier.ofVanilla("textures/entity/strider/strider_saddle.png")
			)
		);
	}

	public Identifier getTexture(StriderEntityRenderState striderEntityRenderState) {
		return striderEntityRenderState.cold ? COLD_TEXTURE : TEXTURE;
	}

	protected float getShadowRadius(StriderEntityRenderState striderEntityRenderState) {
		float f = super.getShadowRadius(striderEntityRenderState);
		return striderEntityRenderState.baby ? f * 0.5F : f;
	}

	public StriderEntityRenderState getRenderState() {
		return new StriderEntityRenderState();
	}

	public void updateRenderState(StriderEntity striderEntity, StriderEntityRenderState striderEntityRenderState, float f) {
		super.updateRenderState(striderEntity, striderEntityRenderState, f);
		striderEntityRenderState.saddled = striderEntity.isSaddled();
		striderEntityRenderState.cold = striderEntity.isCold();
		striderEntityRenderState.hasPassengers = striderEntity.hasPassengers();
	}

	protected void scale(StriderEntityRenderState striderEntityRenderState, MatrixStack matrixStack) {
		float f = striderEntityRenderState.ageScale;
		matrixStack.scale(f, f, f);
	}

	protected boolean isShaking(StriderEntityRenderState striderEntityRenderState) {
		return super.isShaking(striderEntityRenderState) || striderEntityRenderState.cold;
	}
}
