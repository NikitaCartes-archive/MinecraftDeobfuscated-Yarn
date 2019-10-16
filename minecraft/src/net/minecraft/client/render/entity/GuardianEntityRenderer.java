package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.GuardianEntityModel;
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
	private static final Identifier SKIN = new Identifier("textures/entity/guardian.png");
	private static final Identifier EXPLOSION_BEAM_TEX = new Identifier("textures/entity/guardian_beam.png");

	public GuardianEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		this(entityRenderDispatcher, 0.5F);
	}

	protected GuardianEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, float f) {
		super(entityRenderDispatcher, new GuardianEntityModel(), f);
	}

	public boolean method_3978(GuardianEntity guardianEntity, Frustum frustum, double d, double e, double f) {
		if (super.method_4068(guardianEntity, frustum, d, e, f)) {
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

	private Vec3d fromLerpedPosition(LivingEntity livingEntity, double d, float f) {
		double e = MathHelper.lerp((double)f, livingEntity.prevRenderX, livingEntity.getX());
		double g = MathHelper.lerp((double)f, livingEntity.prevRenderY, livingEntity.getY()) + d;
		double h = MathHelper.lerp((double)f, livingEntity.prevRenderZ, livingEntity.getZ());
		return new Vec3d(e, g, h);
	}

	public void method_3977(
		GuardianEntity guardianEntity,
		double d,
		double e,
		double f,
		float g,
		float h,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage
	) {
		super.method_4072(guardianEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
		LivingEntity livingEntity = guardianEntity.getBeamTarget();
		if (livingEntity != null) {
			float i = guardianEntity.getBeamProgress(h);
			float j = (float)guardianEntity.world.getTime() + h;
			float k = j * 0.5F % 1.0F;
			float l = guardianEntity.getStandingEyeHeight();
			matrixStack.push();
			matrixStack.translate(0.0, (double)l, 0.0);
			Vec3d vec3d = this.fromLerpedPosition(livingEntity, (double)livingEntity.getHeight() * 0.5, h);
			Vec3d vec3d2 = this.fromLerpedPosition(guardianEntity, (double)l, h);
			Vec3d vec3d3 = vec3d.subtract(vec3d2);
			float m = (float)(vec3d3.length() + 1.0);
			vec3d3 = vec3d3.normalize();
			float n = (float)Math.acos(vec3d3.y);
			float o = (float)Math.atan2(vec3d3.z, vec3d3.x);
			matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(((float) (Math.PI / 2) - o) * (180.0F / (float)Math.PI)));
			matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(n * (180.0F / (float)Math.PI)));
			int p = 1;
			float q = j * 0.05F * -1.5F;
			float r = i * i;
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
			VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.getEntityCutoutNoCull(EXPLOSION_BEAM_TEX));
			Matrix4f matrix4f = matrixStack.peek();
			method_23173(vertexConsumer, matrix4f, af, m, ag, s, t, u, 0.4999F, ar);
			method_23173(vertexConsumer, matrix4f, af, 0.0F, ag, s, t, u, 0.4999F, aq);
			method_23173(vertexConsumer, matrix4f, ah, 0.0F, ai, s, t, u, 0.0F, aq);
			method_23173(vertexConsumer, matrix4f, ah, m, ai, s, t, u, 0.0F, ar);
			method_23173(vertexConsumer, matrix4f, aj, m, ak, s, t, u, 0.4999F, ar);
			method_23173(vertexConsumer, matrix4f, aj, 0.0F, ak, s, t, u, 0.4999F, aq);
			method_23173(vertexConsumer, matrix4f, al, 0.0F, am, s, t, u, 0.0F, aq);
			method_23173(vertexConsumer, matrix4f, al, m, am, s, t, u, 0.0F, ar);
			float as = 0.0F;
			if (guardianEntity.age % 2 == 0) {
				as = 0.5F;
			}

			method_23173(vertexConsumer, matrix4f, x, m, y, s, t, u, 0.5F, as + 0.5F);
			method_23173(vertexConsumer, matrix4f, z, m, aa, s, t, u, 1.0F, as + 0.5F);
			method_23173(vertexConsumer, matrix4f, ad, m, ae, s, t, u, 1.0F, as);
			method_23173(vertexConsumer, matrix4f, ab, m, ac, s, t, u, 0.5F, as);
			matrixStack.pop();
		}
	}

	private static void method_23173(VertexConsumer vertexConsumer, Matrix4f matrix4f, float f, float g, float h, int i, int j, int k, float l, float m) {
		vertexConsumer.vertex(matrix4f, f, g, h)
			.color(i, j, k, 255)
			.texture(l, m)
			.defaultOverlay(OverlayTexture.DEFAULT_UV)
			.light(15728880)
			.normal(0.0F, 1.0F, 0.0F)
			.next();
	}

	public Identifier method_3976(GuardianEntity guardianEntity) {
		return SKIN;
	}
}
