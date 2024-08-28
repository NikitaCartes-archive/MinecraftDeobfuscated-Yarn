package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.SlimeEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.client.render.entity.state.SlimeEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class SlimeOverlayFeatureRenderer extends FeatureRenderer<SlimeEntityRenderState, SlimeEntityModel> {
	private final SlimeEntityModel model;

	public SlimeOverlayFeatureRenderer(FeatureRendererContext<SlimeEntityRenderState, SlimeEntityModel> context, EntityModelLoader loader) {
		super(context);
		this.model = new SlimeEntityModel(loader.getModelPart(EntityModelLayers.SLIME_OUTER));
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, SlimeEntityRenderState slimeEntityRenderState, float f, float g
	) {
		boolean bl = slimeEntityRenderState.hasOutline && slimeEntityRenderState.invisible;
		if (!slimeEntityRenderState.invisible || bl) {
			VertexConsumer vertexConsumer;
			if (bl) {
				vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getOutline(SlimeEntityRenderer.TEXTURE));
			} else {
				vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(SlimeEntityRenderer.TEXTURE));
			}

			this.model.setAngles(slimeEntityRenderState);
			this.model.render(matrixStack, vertexConsumer, i, LivingEntityRenderer.getOverlay(slimeEntityRenderState, 0.0F));
		}
	}
}
