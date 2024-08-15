package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class FeatureRenderer<S extends EntityRenderState, M extends EntityModel<? super S>> {
	private final FeatureRendererContext<S, M> context;

	public FeatureRenderer(FeatureRendererContext<S, M> context) {
		this.context = context;
	}

	protected static <S extends LivingEntityRenderState> void render(
		EntityModel<S> contextModel, Identifier texture, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, S state, int color
	) {
		if (!state.invisible) {
			contextModel.setAngles(state);
			renderModel(contextModel, texture, matrices, vertexConsumers, light, state, color);
		}
	}

	protected static void renderModel(
		EntityModel<?> model, Identifier texture, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntityRenderState state, int color
	) {
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(texture));
		model.render(matrices, vertexConsumer, light, LivingEntityRenderer.getOverlay(state, 0.0F), color);
	}

	public M getContextModel() {
		return this.context.getModel();
	}

	protected Identifier getTexture(S state) {
		return this.context.getTexture(state);
	}

	public abstract void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, S state, float limbAngle, float limbDistance);
}
