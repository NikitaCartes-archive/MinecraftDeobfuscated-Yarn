package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.GuardianEntityModel;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class GuardianEntityRenderer extends MobEntityRenderer<GuardianEntity, GuardianEntityModel> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/guardian.png");
	private static final Identifier EXPLOSION_BEAM_TEXTURE = new Identifier("textures/entity/guardian_beam.png");
	private static final RenderLayer LAYER = RenderLayer.getEntityCutoutNoCull(EXPLOSION_BEAM_TEXTURE);

	public GuardianEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		this(entityRenderDispatcher, 0.5F);
	}

	protected GuardianEntityRenderer(EntityRenderDispatcher dispatcher, float f) {
		super(dispatcher, new GuardianEntityModel(), f);
	}

	public boolean shouldRender(GuardianEntity guardianEntity, Frustum frustum, double d, double e, double f) {
		if (super.shouldRender(guardianEntity, frustum, d, e, f)) {
			return true;
		} else {
			if (guardianEntity.hasBeamTarget()) {
				LivingEntity livingEntity = guardianEntity.getBeamTarget();
				if (livingEntity != null) {
					Vec3d vec3d = this.fromLerpedPosition(livingEntity, (double)livingEntity.getHeight() * 0.5, 1.0F);
					Vec3d vec3d2 = this.fromLerpedPosition(guardianEntity, (double)guardianEntity.getStandingEyeHeight(), 1.0F);
					return frustum.isVisible(new Box(vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y, vec3d.z));
				}
			}

			return false;
		}
	}

	private Vec3d fromLerpedPosition(LivingEntity entity, double yOffset, float delta) {
		double d = MathHelper.lerp((double)delta, entity.lastRenderX, entity.getX());
		double e = MathHelper.lerp((double)delta, entity.lastRenderY, entity.getY()) + yOffset;
		double f = MathHelper.lerp((double)delta, entity.lastRenderZ, entity.getZ());
		return new Vec3d(d, e, f);
	}

	public void render(GuardianEntity guardianEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		super.render(guardianEntity, f, g, matrixStack, vertexConsumerProvider, i);
		LivingEntity livingEntity = guardianEntity.getBeamTarget();
		if (livingEntity != null) {
			float h = guardianEntity.getBeamProgress(g);
			float j = (float)guardianEntity.world.getTime() + g;
			float k = j * 0.5F % 1.0F;
			float l = guardianEntity.getStandingEyeHeight();
			matrixStack.push();
			matrixStack.translate(0.0, (double)l, 0.0);
			Vec3d vec3d = this.fromLerpedPosition(livingEntity, (double)livingEntity.getHeight() * 0.5, g);
			Vec3d vec3d2 = this.fromLerpedPosition(guardianEntity, (double)l, g);
			Vec3d vec3d3 = vec3d.subtract(vec3d2);
			float m = (float)(vec3d3.length() + 1.0);
			vec3d3 = vec3d3.normalize();
			float n = (float)Math.acos(vec3d3.y);
			float o = (float)Math.atan2(vec3d3.z, vec3d3.x);
			matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(((float) (Math.PI / 2) - o) * (180.0F / (float)Math.PI)));
			matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(n * (180.0F / (float)Math.PI)));
			int p = 1;
			float q = j * 0.05F * -1.5F;
			float r = h * h;
			int s = 64 + (int)(r * 191.0F);
			int t = 32 + (int)(r * 191.0F);
			int u = 128 - (int)(r * 64.0F);
			float v = 0.2F;
			float w = 0.282F;
			float x = MathHelper.cos(q + (float) (Math.PI * 3.0 / 4.0)) * 0.282F;
			float y = MathHelper.sin(q + (float) (Math.PI * 3.0 / 4.0)) * 0.282F;
			float z = MathHelper.cos(q + (float) (Math.PI / 4)) * 0.282F;
			float aa = MathHelper.sin(q + (float) (Math.PI / 4)) * 0.282F;
			float ab = MathHelper.cos(q + ((float) Math.PI * 5.0F / 4.0F)) * 0.282F;
			float ac = MathHelper.sin(q + ((float) Math.PI * 5.0F / 4.0F)) * 0.282F;
			float ad = MathHelper.cos(q + ((float) Math.PI * 7.0F / 4.0F)) * 0.282F;
			float ae = MathHelper.sin(q + ((float) Math.PI * 7.0F / 4.0F)) * 0.282F;
			float af = MathHelper.cos(q + (float) Math.PI) * 0.2F;
			float ag = MathHelper.sin(q + (float) Math.PI) * 0.2F;
			float ah = MathHelper.cos(q + 0.0F) * 0.2F;
			float ai = MathHelper.sin(q + 0.0F) * 0.2F;
			float aj = MathHelper.cos(q + (float) (Math.PI / 2)) * 0.2F;
			float ak = MathHelper.sin(q + (float) (Math.PI / 2)) * 0.2F;
			float al = MathHelper.cos(q + (float) (Math.PI * 3.0 / 2.0)) * 0.2F;
			float am = MathHelper.sin(q + (float) (Math.PI * 3.0 / 2.0)) * 0.2F;
			float ao = 0.0F;
			float ap = 0.4999F;
			float aq = -1.0F + k;
			float ar = m * 2.5F + aq;
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
			MatrixStack.Entry entry = matrixStack.peek();
			Matrix4f matrix4f = entry.getModel();
			Matrix3f matrix3f = entry.getNormal();
			method_23173(vertexConsumer, matrix4f, matrix3f, af, m, ag, s, t, u, 0.4999F, ar);
			method_23173(vertexConsumer, matrix4f, matrix3f, af, 0.0F, ag, s, t, u, 0.4999F, aq);
			method_23173(vertexConsumer, matrix4f, matrix3f, ah, 0.0F, ai, s, t, u, 0.0F, aq);
			method_23173(vertexConsumer, matrix4f, matrix3f, ah, m, ai, s, t, u, 0.0F, ar);
			method_23173(vertexConsumer, matrix4f, matrix3f, aj, m, ak, s, t, u, 0.4999F, ar);
			method_23173(vertexConsumer, matrix4f, matrix3f, aj, 0.0F, ak, s, t, u, 0.4999F, aq);
			method_23173(vertexConsumer, matrix4f, matrix3f, al, 0.0F, am, s, t, u, 0.0F, aq);
			method_23173(vertexConsumer, matrix4f, matrix3f, al, m, am, s, t, u, 0.0F, ar);
			float as = 0.0F;
			if (guardianEntity.age % 2 == 0) {
				as = 0.5F;
			}

			method_23173(vertexConsumer, matrix4f, matrix3f, x, m, y, s, t, u, 0.5F, as + 0.5F);
			method_23173(vertexConsumer, matrix4f, matrix3f, z, m, aa, s, t, u, 1.0F, as + 0.5F);
			method_23173(vertexConsumer, matrix4f, matrix3f, ad, m, ae, s, t, u, 1.0F, as);
			method_23173(vertexConsumer, matrix4f, matrix3f, ab, m, ac, s, t, u, 0.5F, as);
			matrixStack.pop();
		}
	}

	private static void method_23173(
		VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, float f, float g, float h, int i, int j, int k, float l, float m
	) {
		vertexConsumer.vertex(matrix4f, f, g, h)
			.color(i, j, k, 255)
			.texture(l, m)
			.overlay(OverlayTexture.DEFAULT_UV)
			.light(15728880)
			.normal(matrix3f, 0.0F, 1.0F, 0.0F)
			.next();
	}

	public Identifier getTexture(GuardianEntity guardianEntity) {
		return TEXTURE;
	}
}
