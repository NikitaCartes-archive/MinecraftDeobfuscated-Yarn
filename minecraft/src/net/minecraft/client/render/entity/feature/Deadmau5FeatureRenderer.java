package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.Deadmau5EarsEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class Deadmau5FeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
	private final BipedEntityModel<PlayerEntityRenderState> model;

	public Deadmau5FeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context, EntityModelLoader modelLoader) {
		super(context);
		this.model = new Deadmau5EarsEntityModel(modelLoader.getModelPart(EntityModelLayers.PLAYER_EARS));
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, PlayerEntityRenderState playerEntityRenderState, float f, float g
	) {
		if ("deadmau5".equals(playerEntityRenderState.name) && !playerEntityRenderState.invisible) {
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(playerEntityRenderState.skinTextures.texture()));
			int j = LivingEntityRenderer.getOverlay(playerEntityRenderState, 0.0F);
			this.getContextModel().copyTransforms(this.model);
			this.model.setAngles(playerEntityRenderState);
			this.model.render(matrixStack, vertexConsumer, i, j);
		}
	}
}
