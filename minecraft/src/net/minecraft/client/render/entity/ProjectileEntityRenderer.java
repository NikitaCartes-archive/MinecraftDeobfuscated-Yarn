package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public abstract class ProjectileEntityRenderer<T extends ProjectileEntity> extends EntityRenderer<T> {
	public ProjectileEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void method_3875(
		T projectileEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage
	) {
		matrixStack.push();
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(MathHelper.lerp(h, projectileEntity.prevYaw, projectileEntity.yaw) - 90.0F));
		matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(MathHelper.lerp(h, projectileEntity.prevPitch, projectileEntity.pitch)));
		int i = 0;
		float j = 0.0F;
		float k = 0.5F;
		float l = 0.0F;
		float m = 0.15625F;
		float n = 0.0F;
		float o = 0.15625F;
		float p = 0.15625F;
		float q = 0.3125F;
		float r = 0.05625F;
		float s = (float)projectileEntity.shake - h;
		if (s > 0.0F) {
			float t = -MathHelper.sin(s * 3.0F) * s;
			matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(t));
		}

		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(45.0F));
		matrixStack.scale(0.05625F, 0.05625F, 0.05625F);
		matrixStack.translate(-4.0, 0.0, 0.0);
		int u = projectileEntity.getLightmapCoordinates();
		VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.getEntityCutoutNoCull(this.getTexture(projectileEntity)));
		Matrix4f matrix4f = matrixStack.peek();
		this.method_23153(matrix4f, vertexConsumer, -7, -2, -2, 0.0F, 0.15625F, 1, 0, 0, u);
		this.method_23153(matrix4f, vertexConsumer, -7, -2, 2, 0.15625F, 0.15625F, 1, 0, 0, u);
		this.method_23153(matrix4f, vertexConsumer, -7, 2, 2, 0.15625F, 0.3125F, 1, 0, 0, u);
		this.method_23153(matrix4f, vertexConsumer, -7, 2, -2, 0.0F, 0.3125F, 1, 0, 0, u);
		this.method_23153(matrix4f, vertexConsumer, -7, 2, -2, 0.0F, 0.15625F, -1, 0, 0, u);
		this.method_23153(matrix4f, vertexConsumer, -7, 2, 2, 0.15625F, 0.15625F, -1, 0, 0, u);
		this.method_23153(matrix4f, vertexConsumer, -7, -2, 2, 0.15625F, 0.3125F, -1, 0, 0, u);
		this.method_23153(matrix4f, vertexConsumer, -7, -2, -2, 0.0F, 0.3125F, -1, 0, 0, u);

		for (int v = 0; v < 4; v++) {
			matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(90.0F));
			this.method_23153(matrix4f, vertexConsumer, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, u);
			this.method_23153(matrix4f, vertexConsumer, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, u);
			this.method_23153(matrix4f, vertexConsumer, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, u);
			this.method_23153(matrix4f, vertexConsumer, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, u);
		}

		matrixStack.pop();
		super.render(projectileEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
	}

	public void method_23153(Matrix4f matrix4f, VertexConsumer vertexConsumer, int i, int j, int k, float f, float g, int l, int m, int n, int o) {
		vertexConsumer.vertex(matrix4f, (float)i, (float)j, (float)k)
			.color(255, 255, 255, 255)
			.texture(f, g)
			.defaultOverlay(OverlayTexture.field_21444)
			.light(o)
			.normal((float)l, (float)n, (float)m)
			.next();
	}
}
