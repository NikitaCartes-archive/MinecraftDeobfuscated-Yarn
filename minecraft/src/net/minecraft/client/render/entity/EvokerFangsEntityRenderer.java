package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EvokerFangsEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class EvokerFangsEntityRenderer extends EntityRenderer<EvokerFangsEntity> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/illager/evoker_fangs.png");
	private final EvokerFangsEntityModel<EvokerFangsEntity> model;

	public EvokerFangsEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.model = new EvokerFangsEntityModel<>(context.getPart(EntityModelLayers.EVOKER_FANGS));
	}

	public void render(EvokerFangsEntity evokerFangsEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		float h = evokerFangsEntity.getAnimationProgress(g);
		if (h != 0.0F) {
			float j = 2.0F;
			if (h > 0.9F) {
				j *= (1.0F - h) / 0.1F;
			}

			matrixStack.push();
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F - evokerFangsEntity.getYaw()));
			matrixStack.scale(-j, -j, j);
			float k = 0.03125F;
			matrixStack.translate(0.0, -0.626, 0.0);
			matrixStack.scale(0.5F, 0.5F, 0.5F);
			this.model.setAngles(evokerFangsEntity, h, 0.0F, 0.0F, evokerFangsEntity.getYaw(), evokerFangsEntity.getPitch());
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(TEXTURE));
			this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
			matrixStack.pop();
			super.render(evokerFangsEntity, f, g, matrixStack, vertexConsumerProvider, i);
		}
	}

	public Identifier getTexture(EvokerFangsEntity evokerFangsEntity) {
		return TEXTURE;
	}
}
