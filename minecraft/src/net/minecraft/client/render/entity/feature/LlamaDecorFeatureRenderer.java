package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.LlamaEntityModel;
import net.minecraft.client.render.entity.state.LlamaEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class LlamaDecorFeatureRenderer extends FeatureRenderer<LlamaEntityRenderState, LlamaEntityModel> {
	private static final Identifier[] LLAMA_DECOR = new Identifier[]{
		Identifier.ofVanilla("textures/entity/llama/decor/white.png"),
		Identifier.ofVanilla("textures/entity/llama/decor/orange.png"),
		Identifier.ofVanilla("textures/entity/llama/decor/magenta.png"),
		Identifier.ofVanilla("textures/entity/llama/decor/light_blue.png"),
		Identifier.ofVanilla("textures/entity/llama/decor/yellow.png"),
		Identifier.ofVanilla("textures/entity/llama/decor/lime.png"),
		Identifier.ofVanilla("textures/entity/llama/decor/pink.png"),
		Identifier.ofVanilla("textures/entity/llama/decor/gray.png"),
		Identifier.ofVanilla("textures/entity/llama/decor/light_gray.png"),
		Identifier.ofVanilla("textures/entity/llama/decor/cyan.png"),
		Identifier.ofVanilla("textures/entity/llama/decor/purple.png"),
		Identifier.ofVanilla("textures/entity/llama/decor/blue.png"),
		Identifier.ofVanilla("textures/entity/llama/decor/brown.png"),
		Identifier.ofVanilla("textures/entity/llama/decor/green.png"),
		Identifier.ofVanilla("textures/entity/llama/decor/red.png"),
		Identifier.ofVanilla("textures/entity/llama/decor/black.png")
	};
	private static final Identifier TRADER_LLAMA_DECOR = Identifier.ofVanilla("textures/entity/llama/decor/trader_llama.png");
	private final LlamaEntityModel model;
	private final LlamaEntityModel babyModel;

	public LlamaDecorFeatureRenderer(FeatureRendererContext<LlamaEntityRenderState, LlamaEntityModel> context, EntityModelLoader loader) {
		super(context);
		this.model = new LlamaEntityModel(loader.getModelPart(EntityModelLayers.LLAMA_DECOR));
		this.babyModel = new LlamaEntityModel(loader.getModelPart(EntityModelLayers.LLAMA_BABY_DECOR));
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LlamaEntityRenderState llamaEntityRenderState, float f, float g
	) {
		Identifier identifier;
		if (llamaEntityRenderState.carpetColor != null) {
			identifier = LLAMA_DECOR[llamaEntityRenderState.carpetColor.getId()];
		} else {
			if (!llamaEntityRenderState.trader) {
				return;
			}

			identifier = TRADER_LLAMA_DECOR;
		}

		LlamaEntityModel llamaEntityModel = llamaEntityRenderState.baby ? this.babyModel : this.model;
		llamaEntityModel.setAngles(llamaEntityRenderState);
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(identifier));
		llamaEntityModel.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
	}
}
