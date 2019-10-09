package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public abstract class MobEntityRenderer<T extends MobEntity, M extends EntityModel<T>> extends LivingEntityRenderer<T, M> {
	public MobEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, M entityModel, float f) {
		super(entityRenderDispatcher, entityModel, f);
	}

	protected boolean method_4071(T mobEntity) {
		return super.method_4055(mobEntity) && (mobEntity.shouldRenderName() || mobEntity.hasCustomName() && mobEntity == this.renderManager.targetedEntity);
	}

	public boolean method_4068(T mobEntity, Frustum frustum, double d, double e, double f) {
		if (super.isVisible(mobEntity, frustum, d, e, f)) {
			return true;
		} else {
			Entity entity = mobEntity.getHoldingEntity();
			return entity != null ? frustum.method_23093(entity.getVisibilityBoundingBox()) : false;
		}
	}

	public void method_4072(
		T mobEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage
	) {
		super.method_4054(mobEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
		Entity entity = mobEntity.getHoldingEntity();
		if (entity != null) {
			method_4073(mobEntity, h, matrixStack, layeredVertexConsumerStorage, entity);
		}
	}

	public static void method_4073(MobEntity mobEntity, float f, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, Entity entity) {
		matrixStack.push();
		double d = (double)(MathHelper.lerp(f * 0.5F, entity.yaw, entity.prevYaw) * (float) (Math.PI / 180.0));
		double e = (double)(MathHelper.lerp(f * 0.5F, entity.pitch, entity.prevPitch) * (float) (Math.PI / 180.0));
		double g = Math.cos(d);
		double h = Math.sin(d);
		double i = Math.sin(e);
		if (entity instanceof AbstractDecorationEntity) {
			g = 0.0;
			h = 0.0;
			i = -1.0;
		}

		double j = Math.cos(e);
		double k = MathHelper.lerp((double)f, entity.prevX, entity.getX()) - g * 0.7 - h * 0.5 * j;
		double l = MathHelper.lerp((double)f, entity.prevY + (double)entity.getStandingEyeHeight() * 0.7, entity.getY() + (double)entity.getStandingEyeHeight() * 0.7)
			- i * 0.5
			- 0.25;
		double m = MathHelper.lerp((double)f, entity.prevZ, entity.getZ()) - h * 0.7 + g * 0.5 * j;
		double n = (double)(MathHelper.lerp(f, mobEntity.bodyYaw, mobEntity.prevBodyYaw) * (float) (Math.PI / 180.0)) + (Math.PI / 2);
		g = Math.cos(n) * (double)mobEntity.getWidth() * 0.4;
		h = Math.sin(n) * (double)mobEntity.getWidth() * 0.4;
		double o = MathHelper.lerp((double)f, mobEntity.prevX, mobEntity.getX()) + g;
		double p = MathHelper.lerp((double)f, mobEntity.prevY, mobEntity.getY());
		double q = MathHelper.lerp((double)f, mobEntity.prevZ, mobEntity.getZ()) + h;
		matrixStack.translate(g, -(1.6 - (double)mobEntity.getHeight()) * 0.5, h);
		float r = (float)(k - o);
		float s = (float)(l - p);
		float t = (float)(m - q);
		float u = 0.025F;
		VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.getLeash());
		Matrix4f matrix4f = matrixStack.peek();
		float v = MathHelper.fastInverseSqrt(r * r + t * t) * 0.025F / 2.0F;
		float w = t * v;
		float x = r * v;
		method_23186(vertexConsumer, matrix4f, r, s, t, 0.025F, 0.025F, w, x);
		method_23186(vertexConsumer, matrix4f, r, s, t, 0.025F, 0.0F, w, x);
		matrixStack.pop();
	}

	public static void method_23186(VertexConsumer vertexConsumer, Matrix4f matrix4f, float f, float g, float h, float i, float j, float k, float l) {
		int m = 24;

		for (int n = 0; n < 24; n++) {
			method_23187(vertexConsumer, matrix4f, f, g, h, i, j, 24, n, false, k, l);
			method_23187(vertexConsumer, matrix4f, f, g, h, i, j, 24, n + 1, true, k, l);
		}
	}

	public static void method_23187(
		VertexConsumer vertexConsumer, Matrix4f matrix4f, float f, float g, float h, float i, float j, int k, int l, boolean bl, float m, float n
	) {
		float o = 0.5F;
		float p = 0.4F;
		float q = 0.3F;
		if (l % 2 == 0) {
			o *= 0.7F;
			p *= 0.7F;
			q *= 0.7F;
		}

		float r = (float)l / (float)k;
		float s = f * r;
		float t = g * (r * r + r) * 0.5F + ((float)k - (float)l) / ((float)k * 0.75F) + 0.125F;
		float u = h * r;
		if (!bl) {
			vertexConsumer.vertex(matrix4f, s + m, t + i - j, u - n).color(o, p, q, 1.0F).next();
		}

		vertexConsumer.vertex(matrix4f, s - m, t + j, u + n).color(o, p, q, 1.0F).next();
		if (bl) {
			vertexConsumer.vertex(matrix4f, s + m, t + i - j, u - n).color(o, p, q, 1.0F).next();
		}
	}
}
