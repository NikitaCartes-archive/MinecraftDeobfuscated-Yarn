package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TridentRiptideFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
	public static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/trident_riptide.png");
	private static final int field_53226 = 2;
	private final Model model;
	private final ModelPart[] modelParts = new ModelPart[2];

	public TridentRiptideFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context, EntityModelLoader modelLoader) {
		super(context);
		ModelPart modelPart = modelLoader.getModelPart(EntityModelLayers.SPIN_ATTACK);
		this.model = new Model.SinglePartModel(modelPart, RenderLayer::getEntityCutoutNoCull);

		for (int i = 0; i < 2; i++) {
			this.modelParts[i] = modelPart.getChild(getPartName(i));
		}
	}

	private static String getPartName(int index) {
		return "box" + index;
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		for (int i = 0; i < 2; i++) {
			float f = -3.2F + 9.6F * (float)(i + 1);
			float g = 0.75F * (float)(i + 1);
			modelPartData.addChild(
				getPartName(i), ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -16.0F + f, -8.0F, 16.0F, 32.0F, 16.0F), ModelTransform.NONE.withScale(g)
			);
		}

		return TexturedModelData.of(modelData, 64, 64);
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, PlayerEntityRenderState playerEntityRenderState, float f, float g
	) {
		if (playerEntityRenderState.usingRiptide) {
			for (int j = 0; j < this.modelParts.length; j++) {
				float h = playerEntityRenderState.age * (float)(-(45 + (j + 1) * 5));
				this.modelParts[j].yaw = MathHelper.wrapDegrees(h) * (float) (Math.PI / 180.0);
			}

			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(TEXTURE));
			this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
		}
	}
}
