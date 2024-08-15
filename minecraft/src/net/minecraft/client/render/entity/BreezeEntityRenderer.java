package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.BreezeEyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.BreezeWindFeatureRenderer;
import net.minecraft.client.render.entity.model.BreezeEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.BreezeEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BreezeEntityRenderer extends MobEntityRenderer<BreezeEntity, BreezeEntityRenderState, BreezeEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/breeze/breeze.png");

	public BreezeEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new BreezeEntityModel(context.getPart(EntityModelLayers.BREEZE)), 0.5F);
		this.addFeature(new BreezeWindFeatureRenderer(context, this));
		this.addFeature(new BreezeEyesFeatureRenderer(this));
	}

	public void render(BreezeEntityRenderState breezeEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		BreezeEntityModel breezeEntityModel = this.getModel();
		updatePartVisibility(breezeEntityModel, breezeEntityModel.getHead(), breezeEntityModel.getRods());
		super.render(breezeEntityRenderState, matrixStack, vertexConsumerProvider, i);
	}

	public Identifier getTexture(BreezeEntityRenderState breezeEntityRenderState) {
		return TEXTURE;
	}

	public BreezeEntityRenderState getRenderState() {
		return new BreezeEntityRenderState();
	}

	public void updateRenderState(BreezeEntity breezeEntity, BreezeEntityRenderState breezeEntityRenderState, float f) {
		super.updateRenderState(breezeEntity, breezeEntityRenderState, f);
		breezeEntityRenderState.shootingAnimationState.copyFrom(breezeEntity.shootingAnimationState);
		breezeEntityRenderState.slidingAnimationState.copyFrom(breezeEntity.slidingAnimationState);
		breezeEntityRenderState.slidingBackAnimationState.copyFrom(breezeEntity.slidingBackAnimationState);
		breezeEntityRenderState.inhalingAnimationState.copyFrom(breezeEntity.inhalingAnimationState);
		breezeEntityRenderState.longJumpingAnimationState.copyFrom(breezeEntity.longJumpingAnimationState);
	}

	public static BreezeEntityModel updatePartVisibility(BreezeEntityModel model, ModelPart... modelParts) {
		model.getHead().visible = false;
		model.getEyes().visible = false;
		model.getRods().visible = false;
		model.getWindBody().visible = false;

		for (ModelPart modelPart : modelParts) {
			modelPart.visible = true;
		}

		return model;
	}
}
