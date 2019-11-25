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
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class ProjectileEntityRenderer<T extends ProjectileEntity> extends EntityRenderer<T> {
	public ProjectileEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void render(T projectileEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.lerp(g, projectileEntity.prevYaw, projectileEntity.yaw) - 90.0F));
		matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.lerp(g, projectileEntity.prevPitch, projectileEntity.pitch)));
		int j = 0;
		float h = 0.0F;
		float k = 0.5F;
		float l = 0.0F;
		float m = 0.15625F;
		float n = 0.0F;
		float o = 0.15625F;
		float p = 0.15625F;
		float q = 0.3125F;
		float r = 0.05625F;
		float s = (float)projectileEntity.shake - g;
		if (s > 0.0F) {
			float t = -MathHelper.sin(s * 3.0F) * s;
			matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(t));
		}

		matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(45.0F));
		matrixStack.scale(0.05625F, 0.05625F, 0.05625F);
		matrixStack.translate(-4.0, 0.0, 0.0);
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutout(this.getTexture(projectileEntity)));
		MatrixStack.Entry entry = matrixStack.peek();
		Matrix4f matrix4f = entry.getModel();
		Matrix3f matrix3f = entry.getNormal();
		this.method_23153(matrix4f, matrix3f, vertexConsumer, -7, -2, -2, 0.0F, 0.15625F, 1, 0, 0, i);
		this.method_23153(matrix4f, matrix3f, vertexConsumer, -7, -2, 2, 0.15625F, 0.15625F, 1, 0, 0, i);
		this.method_23153(matrix4f, matrix3f, vertexConsumer, -7, 2, 2, 0.15625F, 0.3125F, 1, 0, 0, i);
		this.method_23153(matrix4f, matrix3f, vertexConsumer, -7, 2, -2, 0.0F, 0.3125F, 1, 0, 0, i);
		this.method_23153(matrix4f, matrix3f, vertexConsumer, -7, 2, -2, 0.0F, 0.15625F, -1, 0, 0, i);
		this.method_23153(matrix4f, matrix3f, vertexConsumer, -7, 2, 2, 0.15625F, 0.15625F, -1, 0, 0, i);
		this.method_23153(matrix4f, matrix3f, vertexConsumer, -7, -2, 2, 0.15625F, 0.3125F, -1, 0, 0, i);
		this.method_23153(matrix4f, matrix3f, vertexConsumer, -7, -2, -2, 0.0F, 0.3125F, -1, 0, 0, i);

		for (int u = 0; u < 4; u++) {
			matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0F));
			this.method_23153(matrix4f, matrix3f, vertexConsumer, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, i);
			this.method_23153(matrix4f, matrix3f, vertexConsumer, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, i);
			this.method_23153(matrix4f, matrix3f, vertexConsumer, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, i);
			this.method_23153(matrix4f, matrix3f, vertexConsumer, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, i);
		}

		matrixStack.pop();
		super.render(projectileEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	public void method_23153(
		Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer vertexConsumer, int i, int j, int k, float f, float g, int l, int m, int n, int o
	) {
		vertexConsumer.vertex(matrix4f, (float)i, (float)j, (float)k)
			.color(255, 255, 255, 255)
			.texture(f, g)
			.overlay(OverlayTexture.DEFAULT_UV)
			.light(o)
			.normal(matrix3f, (float)l, (float)n, (float)m)
			.next();
	}
}
