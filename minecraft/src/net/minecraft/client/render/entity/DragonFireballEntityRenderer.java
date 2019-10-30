package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DragonFireballEntityRenderer extends EntityRenderer<DragonFireballEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/enderdragon/dragon_fireball.png");

	public DragonFireballEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void method_3906(
		DragonFireballEntity dragonFireballEntity,
		double d,
		double e,
		double f,
		float g,
		float h,
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider
	) {
		matrixStack.push();
		matrixStack.scale(2.0F, 2.0F, 2.0F);
		float i = 1.0F;
		float j = 0.5F;
		float k = 0.25F;
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0F - this.renderManager.cameraYaw));
		float l = (float)(this.renderManager.gameOptions.perspective == 2 ? -1 : 1) * -this.renderManager.cameraPitch;
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(l));
		Matrix4f matrix4f = matrixStack.peekModel();
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(SKIN));
		int m = dragonFireballEntity.getLightmapCoordinates();
		vertexConsumer.vertex(matrix4f, -0.5F, -0.25F, 0.0F)
			.color(255, 255, 255, 255)
			.texture(0.0F, 1.0F)
			.overlay(OverlayTexture.DEFAULT_UV)
			.light(m)
			.normal(0.0F, 1.0F, 0.0F)
			.next();
		vertexConsumer.vertex(matrix4f, 0.5F, -0.25F, 0.0F)
			.color(255, 255, 255, 255)
			.texture(1.0F, 1.0F)
			.overlay(OverlayTexture.DEFAULT_UV)
			.light(m)
			.normal(0.0F, 1.0F, 0.0F)
			.next();
		vertexConsumer.vertex(matrix4f, 0.5F, 0.75F, 0.0F)
			.color(255, 255, 255, 255)
			.texture(1.0F, 0.0F)
			.overlay(OverlayTexture.DEFAULT_UV)
			.light(m)
			.normal(0.0F, 1.0F, 0.0F)
			.next();
		vertexConsumer.vertex(matrix4f, -0.5F, 0.75F, 0.0F)
			.color(255, 255, 255, 255)
			.texture(0.0F, 0.0F)
			.overlay(OverlayTexture.DEFAULT_UV)
			.light(m)
			.normal(0.0F, 1.0F, 0.0F)
			.next();
		matrixStack.pop();
		super.render(dragonFireballEntity, d, e, f, g, h, matrixStack, vertexConsumerProvider);
	}

	public Identifier method_3905(DragonFireballEntity dragonFireballEntity) {
		return SKIN;
	}
}
