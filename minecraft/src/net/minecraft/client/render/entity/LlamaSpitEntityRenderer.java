package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LlamaSpitEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class LlamaSpitEntityRenderer extends EntityRenderer<LlamaSpitEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/llama/spit.png");
	private final LlamaSpitEntityModel<LlamaSpitEntity> model;

	public LlamaSpitEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.model = new LlamaSpitEntityModel<>(context.getPart(EntityModelLayers.LLAMA_SPIT));
	}

	public void render(LlamaSpitEntity llamaSpitEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		matrixStack.translate(0.0, 0.15F, 0.0);
		matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.lerp(g, llamaSpitEntity.prevYaw, llamaSpitEntity.yaw) - 90.0F));
		matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.lerp(g, llamaSpitEntity.prevPitch, llamaSpitEntity.pitch)));
		this.model.setAngles(llamaSpitEntity, g, 0.0F, -0.1F, 0.0F, 0.0F);
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(TEXTURE));
		this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
		matrixStack.pop();
		super.render(llamaSpitEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	public Identifier getTexture(LlamaSpitEntity llamaSpitEntity) {
		return TEXTURE;
	}
}
