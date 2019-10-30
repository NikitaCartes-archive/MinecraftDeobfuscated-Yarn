package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EvokerFangsEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EvokerFangsEntityRenderer extends EntityRenderer<EvokerFangsEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/illager/evoker_fangs.png");
	private final EvokerFangsEntityModel<EvokerFangsEntity> model = new EvokerFangsEntityModel<>();

	public EvokerFangsEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void method_3962(
		EvokerFangsEntity model, double x, double y, double d, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider
	) {
		float h = model.getAnimationProgress(g);
		if (h != 0.0F) {
			float i = 2.0F;
			if (h > 0.9F) {
				i = (float)((double)i * ((1.0 - (double)h) / 0.1F));
			}

			matrixStack.push();
			matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(90.0F - model.yaw));
			matrixStack.scale(-i, -i, i);
			float j = 0.03125F;
			matrixStack.translate(0.0, -0.626F, 0.0);
			matrixStack.scale(0.5F, 0.5F, 0.5F);
			int k = model.getLightmapCoordinates();
			this.model.setAngles(model, h, 0.0F, 0.0F, model.yaw, model.pitch, 0.03125F);
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(SKIN));
			this.model.render(matrixStack, vertexConsumer, k, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F);
			matrixStack.pop();
			super.render(model, x, y, d, f, g, matrixStack, vertexConsumerProvider);
		}
	}

	public Identifier method_3963(EvokerFangsEntity evokerFangsEntity) {
		return SKIN;
	}
}
