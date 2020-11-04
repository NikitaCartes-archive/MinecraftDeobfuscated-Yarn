package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

@Environment(EnvType.CLIENT)
public abstract class MobEntityRenderer<T extends MobEntity, M extends EntityModel<T>> extends LivingEntityRenderer<T, M> {
	public MobEntityRenderer(class_5617.class_5618 arg, M entityModel, float f) {
		super(arg, entityModel, f);
	}

	protected boolean hasLabel(T mobEntity) {
		return super.hasLabel(mobEntity) && (mobEntity.shouldRenderName() || mobEntity.hasCustomName() && mobEntity == this.dispatcher.targetedEntity);
	}

	public boolean shouldRender(T mobEntity, Frustum frustum, double d, double e, double f) {
		if (super.shouldRender(mobEntity, frustum, d, e, f)) {
			return true;
		} else {
			Entity entity = mobEntity.getHoldingEntity();
			return entity != null ? frustum.isVisible(entity.getVisibilityBoundingBox()) : false;
		}
	}

	public void render(T mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
		Entity entity = mobEntity.getHoldingEntity();
		if (entity != null) {
			this.method_4073(mobEntity, g, matrixStack, vertexConsumerProvider, entity);
		}
	}

	private <E extends Entity> void method_4073(T mobEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, E entity) {
		matrixStack.push();
		Vec3d vec3d = entity.method_30951(f);
		double d = (double)(MathHelper.lerp(f, mobEntity.bodyYaw, mobEntity.prevBodyYaw) * (float) (Math.PI / 180.0)) + (Math.PI / 2);
		Vec3d vec3d2 = mobEntity.method_29919();
		double e = Math.cos(d) * vec3d2.z + Math.sin(d) * vec3d2.x;
		double g = Math.sin(d) * vec3d2.z - Math.cos(d) * vec3d2.x;
		double h = MathHelper.lerp((double)f, mobEntity.prevX, mobEntity.getX()) + e;
		double i = MathHelper.lerp((double)f, mobEntity.prevY, mobEntity.getY()) + vec3d2.y;
		double j = MathHelper.lerp((double)f, mobEntity.prevZ, mobEntity.getZ()) + g;
		matrixStack.translate(e, vec3d2.y, g);
		float k = (float)(vec3d.x - h);
		float l = (float)(vec3d.y - i);
		float m = (float)(vec3d.z - j);
		float n = 0.025F;
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getLeash());
		Matrix4f matrix4f = matrixStack.peek().getModel();
		float o = MathHelper.fastInverseSqrt(k * k + m * m) * 0.025F / 2.0F;
		float p = m * o;
		float q = k * o;
		BlockPos blockPos = new BlockPos(mobEntity.getCameraPosVec(f));
		BlockPos blockPos2 = new BlockPos(entity.getCameraPosVec(f));
		int r = this.getBlockLight(mobEntity, blockPos);
		int s = this.dispatcher.getRenderer(entity).getBlockLight(entity, blockPos2);
		int t = mobEntity.world.getLightLevel(LightType.SKY, blockPos);
		int u = mobEntity.world.getLightLevel(LightType.SKY, blockPos2);

		for (int v = 0; v <= 24; v++) {
			method_23187(vertexConsumer, matrix4f, k, l, m, r, s, t, u, 0.025F, 0.025F, p, q, v, false);
		}

		for (int v = 24; v >= 0; v--) {
			method_23187(vertexConsumer, matrix4f, k, l, m, r, s, t, u, 0.025F, 0.0F, p, q, v, true);
		}

		matrixStack.pop();
	}

	private static void method_23187(
		VertexConsumer vertexConsumer,
		Matrix4f matrix4f,
		float f,
		float g,
		float h,
		int i,
		int j,
		int k,
		int l,
		float m,
		float n,
		float o,
		float p,
		int q,
		boolean bl
	) {
		float r = (float)q / 24.0F;
		int s = (int)MathHelper.lerp(r, (float)i, (float)j);
		int t = (int)MathHelper.lerp(r, (float)k, (float)l);
		int u = LightmapTextureManager.pack(s, t);
		float v = q % 2 == (bl ? 1 : 0) ? 0.7F : 1.0F;
		float w = 0.5F * v;
		float x = 0.4F * v;
		float y = 0.3F * v;
		float z = f * r;
		float aa = g > 0.0F ? g * r * r : g - g * (1.0F - r) * (1.0F - r);
		float ab = h * r;
		vertexConsumer.vertex(matrix4f, z - o, aa + n, ab + p).color(w, x, y, 1.0F).light(u).next();
		vertexConsumer.vertex(matrix4f, z + o, aa + m - n, ab - p).color(w, x, y, 1.0F).light(u).next();
	}
}
