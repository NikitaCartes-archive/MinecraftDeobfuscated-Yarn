package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class Deadmau5FeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	public Deadmau5FeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4181(
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i,
		AbstractClientPlayerEntity abstractClientPlayerEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l,
		float m
	) {
		if ("deadmau5".equals(abstractClientPlayerEntity.getName().getString())
			&& abstractClientPlayerEntity.hasSkinTexture()
			&& !abstractClientPlayerEntity.isInvisible()) {
			VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.getEntitySolid(abstractClientPlayerEntity.getSkinTexture()));
			int n = LivingEntityRenderer.method_23622(abstractClientPlayerEntity, 0.0F);

			for (int o = 0; o < 2; o++) {
				float p = MathHelper.lerp(h, abstractClientPlayerEntity.prevYaw, abstractClientPlayerEntity.yaw)
					- MathHelper.lerp(h, abstractClientPlayerEntity.prevBodyYaw, abstractClientPlayerEntity.bodyYaw);
				float q = MathHelper.lerp(h, abstractClientPlayerEntity.prevPitch, abstractClientPlayerEntity.pitch);
				matrixStack.push();
				matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(p));
				matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(q));
				matrixStack.translate((double)(0.375F * (float)(o * 2 - 1)), 0.0, 0.0);
				matrixStack.translate(0.0, -0.375, 0.0);
				matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(-q));
				matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-p));
				float r = 1.3333334F;
				matrixStack.scale(1.3333334F, 1.3333334F, 1.3333334F);
				this.getModel().renderEars(matrixStack, vertexConsumer, 0.0625F, i, n);
				matrixStack.pop();
			}
		}
	}
}
