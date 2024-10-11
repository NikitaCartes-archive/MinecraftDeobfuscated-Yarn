package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LlamaSpitEntityModel;
import net.minecraft.client.render.entity.state.LlamaSpitEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class LlamaSpitEntityRenderer extends EntityRenderer<LlamaSpitEntity, LlamaSpitEntityRenderState> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/llama/spit.png");
	private final LlamaSpitEntityModel model;

	public LlamaSpitEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.model = new LlamaSpitEntityModel(context.getPart(EntityModelLayers.LLAMA_SPIT));
	}

	public void render(LlamaSpitEntityRenderState llamaSpitEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		matrixStack.translate(0.0F, 0.15F, 0.0F);
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(llamaSpitEntityRenderState.yaw - 90.0F));
		matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(llamaSpitEntityRenderState.pitch));
		this.model.setAngles(llamaSpitEntityRenderState);
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(TEXTURE));
		this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
		matrixStack.pop();
		super.render(llamaSpitEntityRenderState, matrixStack, vertexConsumerProvider, i);
	}

	public LlamaSpitEntityRenderState createRenderState() {
		return new LlamaSpitEntityRenderState();
	}

	public void updateRenderState(LlamaSpitEntity llamaSpitEntity, LlamaSpitEntityRenderState llamaSpitEntityRenderState, float f) {
		super.updateRenderState(llamaSpitEntity, llamaSpitEntityRenderState, f);
		llamaSpitEntityRenderState.pitch = llamaSpitEntity.getLerpedPitch(f);
		llamaSpitEntityRenderState.yaw = llamaSpitEntity.getLerpedYaw(f);
	}
}
