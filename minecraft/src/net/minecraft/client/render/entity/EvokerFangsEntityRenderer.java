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

	public void render(EvokerFangsEntity model, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		float h = model.getAnimationProgress(g);
		if (h != 0.0F) {
			float j = 2.0F;
			if (h > 0.9F) {
				j = (float)((double)j * ((1.0 - (double)h) / 0.1F));
			}

			matrixStack.push();
			matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0F - model.yaw));
			matrixStack.scale(-j, -j, j);
			float k = 0.03125F;
			matrixStack.translate(0.0, -0.626F, 0.0);
			matrixStack.scale(0.5F, 0.5F, 0.5F);
			this.model.setAngles(model, h, 0.0F, 0.0F, model.yaw, model.pitch);
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(SKIN));
			this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
			matrixStack.pop();
			super.render(model, f, g, matrixStack, vertexConsumerProvider, i);
		}
	}

	public Identifier getTexture(EvokerFangsEntity evokerFangsEntity) {
		return SKIN;
	}
}
