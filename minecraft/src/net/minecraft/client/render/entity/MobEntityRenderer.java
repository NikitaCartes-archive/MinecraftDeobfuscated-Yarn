package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
	public static final int field_32940 = 24;

	public MobEntityRenderer(EntityRendererFactory.Context context, M entityModel, float f) {
		super(context, entityModel, f);
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

	private <E extends Entity> void method_4073(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider provider, E holdingEntity) {
		matrices.push();
		Vec3d vec3d = holdingEntity.method_30951(tickDelta);
		double d = (double)(MathHelper.lerp(tickDelta, entity.bodyYaw, entity.prevBodyYaw) * (float) (Math.PI / 180.0)) + (Math.PI / 2);
		Vec3d vec3d2 = entity.method_29919();
		double e = Math.cos(d) * vec3d2.z + Math.sin(d) * vec3d2.x;
		double f = Math.sin(d) * vec3d2.z - Math.cos(d) * vec3d2.x;
		double g = MathHelper.lerp((double)tickDelta, entity.prevX, entity.getX()) + e;
		double h = MathHelper.lerp((double)tickDelta, entity.prevY, entity.getY()) + vec3d2.y;
		double i = MathHelper.lerp((double)tickDelta, entity.prevZ, entity.getZ()) + f;
		matrices.translate(e, vec3d2.y, f);
		float j = (float)(vec3d.x - g);
		float k = (float)(vec3d.y - h);
		float l = (float)(vec3d.z - i);
		float m = 0.025F;
		VertexConsumer vertexConsumer = provider.getBuffer(RenderLayer.getLeash());
		Matrix4f matrix4f = matrices.peek().getModel();
		float n = MathHelper.fastInverseSqrt(j * j + l * l) * 0.025F / 2.0F;
		float o = l * n;
		float p = j * n;
		BlockPos blockPos = new BlockPos(entity.getCameraPosVec(tickDelta));
		BlockPos blockPos2 = new BlockPos(holdingEntity.getCameraPosVec(tickDelta));
		int q = this.getBlockLight(entity, blockPos);
		int r = this.dispatcher.getRenderer(holdingEntity).getBlockLight(holdingEntity, blockPos2);
		int s = entity.world.getLightLevel(LightType.SKY, blockPos);
		int t = entity.world.getLightLevel(LightType.SKY, blockPos2);

		for (int u = 0; u <= 24; u++) {
			method_23187(vertexConsumer, matrix4f, j, k, l, q, r, s, t, 0.025F, 0.025F, o, p, u, false);
		}

		for (int u = 24; u >= 0; u--) {
			method_23187(vertexConsumer, matrix4f, j, k, l, q, r, s, t, 0.025F, 0.0F, o, p, u, true);
		}

		matrices.pop();
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
