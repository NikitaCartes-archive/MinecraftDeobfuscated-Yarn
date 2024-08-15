package net.minecraft.client.render.entity;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.GuardianEntityModel;
import net.minecraft.client.render.entity.state.GuardianEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class GuardianEntityRenderer extends MobEntityRenderer<GuardianEntity, GuardianEntityRenderState, GuardianEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/guardian.png");
	private static final Identifier EXPLOSION_BEAM_TEXTURE = Identifier.ofVanilla("textures/entity/guardian_beam.png");
	private static final RenderLayer LAYER = RenderLayer.getEntityCutoutNoCull(EXPLOSION_BEAM_TEXTURE);

	public GuardianEntityRenderer(EntityRendererFactory.Context context) {
		this(context, 0.5F, EntityModelLayers.GUARDIAN);
	}

	protected GuardianEntityRenderer(EntityRendererFactory.Context ctx, float shadowRadius, EntityModelLayer layer) {
		super(ctx, new GuardianEntityModel(ctx.getPart(layer)), shadowRadius);
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

	public void render(GuardianEntityRenderState guardianEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		super.render(guardianEntityRenderState, matrixStack, vertexConsumerProvider, i);
		Vec3d vec3d = guardianEntityRenderState.beamTargetPos;
		if (vec3d != null) {
			float f = guardianEntityRenderState.beamTicks * 0.5F % 1.0F;
			matrixStack.push();
			matrixStack.translate(0.0F, guardianEntityRenderState.standingEyeHeight, 0.0F);
			renderBeam(
				matrixStack,
				vertexConsumerProvider.getBuffer(LAYER),
				vec3d.subtract(guardianEntityRenderState.cameraPosVec),
				guardianEntityRenderState.beamTicks,
				guardianEntityRenderState.baseScale,
				f
			);
			matrixStack.pop();
		}
	}

	private static void renderBeam(MatrixStack matrices, VertexConsumer vertexConsumer, Vec3d vec3d, float beamTicks, float f, float g) {
		float h = (float)(vec3d.length() + 1.0);
		vec3d = vec3d.normalize();
		float i = (float)Math.acos(vec3d.y);
		float j = (float) (Math.PI / 2) - (float)Math.atan2(vec3d.z, vec3d.x);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j * (180.0F / (float)Math.PI)));
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(i * (180.0F / (float)Math.PI)));
		float k = beamTicks * 0.05F * -1.5F;
		float l = f * f;
		int m = 64 + (int)(l * 191.0F);
		int n = 32 + (int)(l * 191.0F);
		int o = 128 - (int)(l * 64.0F);
		float p = 0.2F;
		float q = 0.282F;
		float r = MathHelper.cos(k + (float) (Math.PI * 3.0 / 4.0)) * 0.282F;
		float s = MathHelper.sin(k + (float) (Math.PI * 3.0 / 4.0)) * 0.282F;
		float t = MathHelper.cos(k + (float) (Math.PI / 4)) * 0.282F;
		float u = MathHelper.sin(k + (float) (Math.PI / 4)) * 0.282F;
		float v = MathHelper.cos(k + ((float) Math.PI * 5.0F / 4.0F)) * 0.282F;
		float w = MathHelper.sin(k + ((float) Math.PI * 5.0F / 4.0F)) * 0.282F;
		float x = MathHelper.cos(k + ((float) Math.PI * 7.0F / 4.0F)) * 0.282F;
		float y = MathHelper.sin(k + ((float) Math.PI * 7.0F / 4.0F)) * 0.282F;
		float z = MathHelper.cos(k + (float) Math.PI) * 0.2F;
		float aa = MathHelper.sin(k + (float) Math.PI) * 0.2F;
		float ab = MathHelper.cos(k + 0.0F) * 0.2F;
		float ac = MathHelper.sin(k + 0.0F) * 0.2F;
		float ad = MathHelper.cos(k + (float) (Math.PI / 2)) * 0.2F;
		float ae = MathHelper.sin(k + (float) (Math.PI / 2)) * 0.2F;
		float af = MathHelper.cos(k + (float) (Math.PI * 3.0 / 2.0)) * 0.2F;
		float ag = MathHelper.sin(k + (float) (Math.PI * 3.0 / 2.0)) * 0.2F;
		float ai = 0.0F;
		float aj = 0.4999F;
		float ak = -1.0F + g;
		float al = ak + h * 2.5F;
		MatrixStack.Entry entry = matrices.peek();
		vertex(vertexConsumer, entry, z, h, aa, m, n, o, 0.4999F, al);
		vertex(vertexConsumer, entry, z, 0.0F, aa, m, n, o, 0.4999F, ak);
		vertex(vertexConsumer, entry, ab, 0.0F, ac, m, n, o, 0.0F, ak);
		vertex(vertexConsumer, entry, ab, h, ac, m, n, o, 0.0F, al);
		vertex(vertexConsumer, entry, ad, h, ae, m, n, o, 0.4999F, al);
		vertex(vertexConsumer, entry, ad, 0.0F, ae, m, n, o, 0.4999F, ak);
		vertex(vertexConsumer, entry, af, 0.0F, ag, m, n, o, 0.0F, ak);
		vertex(vertexConsumer, entry, af, h, ag, m, n, o, 0.0F, al);
		float am = MathHelper.floor(beamTicks) % 2 == 0 ? 0.5F : 0.0F;
		vertex(vertexConsumer, entry, r, h, s, m, n, o, 0.5F, am + 0.5F);
		vertex(vertexConsumer, entry, t, h, u, m, n, o, 1.0F, am + 0.5F);
		vertex(vertexConsumer, entry, x, h, y, m, n, o, 1.0F, am);
		vertex(vertexConsumer, entry, v, h, w, m, n, o, 0.5F, am);
	}

	private static void vertex(VertexConsumer vertexConsumer, MatrixStack.Entry matrix, float x, float y, float z, int red, int green, int blue, float u, float v) {
		vertexConsumer.vertex(matrix, x, y, z)
			.color(red, green, blue, 255)
			.texture(u, v)
			.overlay(OverlayTexture.DEFAULT_UV)
			.light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
			.normal(matrix, 0.0F, 1.0F, 0.0F);
	}

	public Identifier getTexture(GuardianEntityRenderState guardianEntityRenderState) {
		return TEXTURE;
	}

	public GuardianEntityRenderState getRenderState() {
		return new GuardianEntityRenderState();
	}

	public void updateRenderState(GuardianEntity guardianEntity, GuardianEntityRenderState guardianEntityRenderState, float f) {
		super.updateRenderState(guardianEntity, guardianEntityRenderState, f);
		guardianEntityRenderState.spikesExtension = guardianEntity.getSpikesExtension(f);
		guardianEntityRenderState.tailAngle = guardianEntity.getTailAngle(f);
		guardianEntityRenderState.cameraPosVec = guardianEntity.getCameraPosVec(f);
		Entity entity = getBeamTarget(guardianEntity);
		if (entity != null) {
			guardianEntityRenderState.rotationVec = guardianEntity.getRotationVec(f);
			guardianEntityRenderState.lookAtPos = entity.getCameraPosVec(f);
		} else {
			guardianEntityRenderState.rotationVec = null;
			guardianEntityRenderState.lookAtPos = null;
		}

		LivingEntity livingEntity = guardianEntity.getBeamTarget();
		if (livingEntity != null) {
			guardianEntityRenderState.beamProgress = guardianEntity.getBeamProgress(f);
			guardianEntityRenderState.beamTicks = guardianEntity.getBeamTicks() + f;
			guardianEntityRenderState.beamTargetPos = this.fromLerpedPosition(livingEntity, (double)livingEntity.getHeight() * 0.5, f);
		} else {
			guardianEntityRenderState.beamTargetPos = null;
		}
	}

	@Nullable
	private static Entity getBeamTarget(GuardianEntity guardian) {
		Entity entity = MinecraftClient.getInstance().getCameraEntity();
		return (Entity)(guardian.hasBeamTarget() ? guardian.getBeamTarget() : entity);
	}
}
