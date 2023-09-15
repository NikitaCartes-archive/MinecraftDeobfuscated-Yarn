package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class Deadmau5FeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	public Deadmau5FeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void render(
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		int i,
		AbstractClientPlayerEntity abstractClientPlayerEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l
	) {
		if ("deadmau5".equals(abstractClientPlayerEntity.getName().getString()) && !abstractClientPlayerEntity.isInvisible()) {
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(abstractClientPlayerEntity.getSkinTextures().texture()));
			int m = LivingEntityRenderer.getOverlay(abstractClientPlayerEntity, 0.0F);

			for (int n = 0; n < 2; n++) {
				float o = MathHelper.lerp(h, abstractClientPlayerEntity.prevYaw, abstractClientPlayerEntity.getYaw())
					- MathHelper.lerp(h, abstractClientPlayerEntity.prevBodyYaw, abstractClientPlayerEntity.bodyYaw);
				float p = MathHelper.lerp(h, abstractClientPlayerEntity.prevPitch, abstractClientPlayerEntity.getPitch());
				matrixStack.push();
				matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(o));
				matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(p));
				matrixStack.translate(0.375F * (float)(n * 2 - 1), 0.0F, 0.0F);
				matrixStack.translate(0.0F, -0.375F, 0.0F);
				matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-p));
				matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-o));
				float q = 1.3333334F;
				matrixStack.scale(1.3333334F, 1.3333334F, 1.3333334F);
				this.getContextModel().renderEars(matrixStack, vertexConsumer, i, m);
				matrixStack.pop();
			}
		}
	}
}
