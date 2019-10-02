package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EvokerFangsEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class EvokerFangsEntityRenderer extends EntityRenderer<EvokerFangsEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/illager/evoker_fangs.png");
	private final EvokerFangsEntityModel<EvokerFangsEntity> model = new EvokerFangsEntityModel<>();

	public EvokerFangsEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void method_3962(
		EvokerFangsEntity evokerFangsEntity,
		double d,
		double e,
		double f,
		float g,
		float h,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage
	) {
		float i = evokerFangsEntity.getAnimationProgress(h);
		if (i != 0.0F) {
			float j = 2.0F;
			if (i > 0.9F) {
				j = (float)((double)j * ((1.0 - (double)i) / 0.1F));
			}

			matrixStack.push();
			matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(90.0F - evokerFangsEntity.yaw, true));
			matrixStack.scale(-j, -j, j);
			float k = 0.03125F;
			matrixStack.translate(0.0, -0.626F, 0.0);
			int l = evokerFangsEntity.getLightmapCoordinates();
			VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.method_23017(SKIN));
			OverlayTexture.clearDefaultOverlay(vertexConsumer);
			this.model.setAngles(evokerFangsEntity, i, 0.0F, 0.0F, evokerFangsEntity.yaw, evokerFangsEntity.pitch, 0.03125F);
			this.model.method_22957(matrixStack, vertexConsumer, l);
			vertexConsumer.clearDefaultOverlay();
			matrixStack.pop();
			super.render(evokerFangsEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
		}
	}

	public Identifier method_3963(EvokerFangsEntity evokerFangsEntity) {
		return SKIN;
	}
}
