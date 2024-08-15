package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.PlayerCapeModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;

@Environment(EnvType.CLIENT)
public class CapeFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
	private final BipedEntityModel<PlayerEntityRenderState> model;

	public CapeFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context, EntityModelLoader modelLoader) {
		super(context);
		this.model = new PlayerCapeModel<>(modelLoader.getModelPart(EntityModelLayers.PLAYER_CAPE));
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, PlayerEntityRenderState playerEntityRenderState, float f, float g
	) {
		if (!playerEntityRenderState.invisible && playerEntityRenderState.capeVisible) {
			SkinTextures skinTextures = playerEntityRenderState.skinTextures;
			if (skinTextures.capeTexture() != null) {
				if (!playerEntityRenderState.equippedChestStack.isOf(Items.ELYTRA)) {
					VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(skinTextures.capeTexture()));
					this.getContextModel().copyBipedStateTo(this.model);
					this.model.setAngles(playerEntityRenderState);
					this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
				}
			}
		}
	}
}
