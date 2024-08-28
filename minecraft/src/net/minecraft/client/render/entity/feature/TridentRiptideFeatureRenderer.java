package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.model.TridentRiptideEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TridentRiptideFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
	public static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/trident_riptide.png");
	private final TridentRiptideEntityModel model;

	public TridentRiptideFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context, EntityModelLoader modelLoader) {
		super(context);
		this.model = new TridentRiptideEntityModel(modelLoader.getModelPart(EntityModelLayers.SPIN_ATTACK));
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, PlayerEntityRenderState playerEntityRenderState, float f, float g
	) {
		if (playerEntityRenderState.usingRiptide) {
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(TEXTURE));
			this.model.setAngles(playerEntityRenderState);
			this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
		}
	}
}
