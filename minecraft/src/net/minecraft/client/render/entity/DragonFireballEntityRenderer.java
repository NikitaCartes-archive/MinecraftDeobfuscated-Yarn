package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.Matrix3f;
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
		DragonFireballEntity dragonFireballEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i
	) {
		matrixStack.push();
		matrixStack.scale(2.0F, 2.0F, 2.0F);
		matrixStack.multiply(this.renderManager.camera.method_23767());
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0F));
		MatrixStack.Entry entry = matrixStack.peek();
		Matrix4f matrix4f = entry.getModel();
		Matrix3f matrix3f = entry.getNormal();
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(SKIN));
		method_23837(vertexConsumer, matrix4f, matrix3f, i, 0.0F, 0, 0, 1);
		method_23837(vertexConsumer, matrix4f, matrix3f, i, 1.0F, 0, 1, 1);
		method_23837(vertexConsumer, matrix4f, matrix3f, i, 1.0F, 1, 1, 0);
		method_23837(vertexConsumer, matrix4f, matrix3f, i, 0.0F, 1, 0, 0);
		matrixStack.pop();
		super.render(dragonFireballEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	private static void method_23837(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, int i, float f, int j, int k, int l) {
		vertexConsumer.vertex(matrix4f, f - 0.5F, (float)j - 0.25F, 0.0F)
			.color(255, 255, 255, 255)
			.texture((float)k, (float)l)
			.overlay(OverlayTexture.DEFAULT_UV)
			.light(i)
			.method_23763(matrix3f, 0.0F, 1.0F, 0.0F)
			.next();
	}

	public Identifier method_3905(DragonFireballEntity dragonFireballEntity) {
		return SKIN;
	}
}
