package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VisibleRegion;
import net.minecraft.client.render.entity.model.GuardianEntityModel;
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

	public boolean method_3978(GuardianEntity guardianEntity, VisibleRegion visibleRegion, double d, double e, double f) {
		if (super.method_4068(guardianEntity, visibleRegion, d, e, f)) {
			return true;
		} else {
			if (guardianEntity.hasBeamTarget()) {
				LivingEntity livingEntity = guardianEntity.getBeamTarget();
				if (livingEntity != null) {
					Vec3d vec3d = this.fromLerpedPosition(livingEntity, (double)livingEntity.getHeight() * 0.5, 1.0F);
					Vec3d vec3d2 = this.fromLerpedPosition(guardianEntity, (double)guardianEntity.getStandingEyeHeight(), 1.0F);
					if (visibleRegion.intersects(new Box(vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y, vec3d.z))) {
						return true;
					}
				}
			}

			return false;
		}
	}

	private Vec3d fromLerpedPosition(LivingEntity livingEntity, double d, float f) {
		double e = MathHelper.lerp((double)f, livingEntity.prevRenderX, livingEntity.x);
		double g = MathHelper.lerp((double)f, livingEntity.prevRenderY, livingEntity.y) + d;
		double h = MathHelper.lerp((double)f, livingEntity.prevRenderZ, livingEntity.z);
		return new Vec3d(e, g, h);
	}

	public void method_3977(GuardianEntity guardianEntity, double d, double e, double f, float g, float h) {
		super.method_4072(guardianEntity, d, e, f, g, h);
		LivingEntity livingEntity = guardianEntity.getBeamTarget();
		if (livingEntity != null) {
			float i = guardianEntity.getBeamProgress(h);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
			this.bindTexture(EXPLOSION_BEAM_TEX);
			GlStateManager.texParameter(3553, 10242, 10497);
			GlStateManager.texParameter(3553, 10243, 10497);
			GlStateManager.disableLighting();
			GlStateManager.disableCull();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(true);
			float j = 240.0F;
			GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0F, 240.0F);
			GlStateManager.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
			);
			float k = (float)guardianEntity.world.getTime() + h;
			float l = k * 0.5F % 1.0F;
			float m = guardianEntity.getStandingEyeHeight();
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)d, (float)e + m, (float)f);
			Vec3d vec3d = this.fromLerpedPosition(livingEntity, (double)livingEntity.getHeight() * 0.5, h);
			Vec3d vec3d2 = this.fromLerpedPosition(guardianEntity, (double)m, h);
			Vec3d vec3d3 = vec3d.subtract(vec3d2);
			double n = vec3d3.length() + 1.0;
			vec3d3 = vec3d3.normalize();
			float o = (float)Math.acos(vec3d3.y);
			float p = (float)Math.atan2(vec3d3.z, vec3d3.x);
			GlStateManager.rotatef(((float) (Math.PI / 2) - p) * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(o * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
			int q = 1;
			double r = (double)k * 0.05 * -1.5;
			bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
			float s = i * i;
			int t = 64 + (int)(s * 191.0F);
			int u = 32 + (int)(s * 191.0F);
			int v = 128 - (int)(s * 64.0F);
			double w = 0.2;
			double x = 0.282;
			double y = 0.0 + Math.cos(r + (Math.PI * 3.0 / 4.0)) * 0.282;
			double z = 0.0 + Math.sin(r + (Math.PI * 3.0 / 4.0)) * 0.282;
			double aa = 0.0 + Math.cos(r + (Math.PI / 4)) * 0.282;
			double ab = 0.0 + Math.sin(r + (Math.PI / 4)) * 0.282;
			double ac = 0.0 + Math.cos(r + (Math.PI * 5.0 / 4.0)) * 0.282;
			double ad = 0.0 + Math.sin(r + (Math.PI * 5.0 / 4.0)) * 0.282;
			double ae = 0.0 + Math.cos(r + (Math.PI * 7.0 / 4.0)) * 0.282;
			double af = 0.0 + Math.sin(r + (Math.PI * 7.0 / 4.0)) * 0.282;
			double ag = 0.0 + Math.cos(r + Math.PI) * 0.2;
			double ah = 0.0 + Math.sin(r + Math.PI) * 0.2;
			double ai = 0.0 + Math.cos(r + 0.0) * 0.2;
			double aj = 0.0 + Math.sin(r + 0.0) * 0.2;
			double ak = 0.0 + Math.cos(r + (Math.PI / 2)) * 0.2;
			double al = 0.0 + Math.sin(r + (Math.PI / 2)) * 0.2;
			double am = 0.0 + Math.cos(r + (Math.PI * 3.0 / 2.0)) * 0.2;
			double an = 0.0 + Math.sin(r + (Math.PI * 3.0 / 2.0)) * 0.2;
			double ap = 0.0;
			double aq = 0.4999;
			double ar = (double)(-1.0F + l);
			double as = n * 2.5 + ar;
			bufferBuilder.vertex(ag, n, ah).texture(0.4999, as).color(t, u, v, 255).next();
			bufferBuilder.vertex(ag, 0.0, ah).texture(0.4999, ar).color(t, u, v, 255).next();
			bufferBuilder.vertex(ai, 0.0, aj).texture(0.0, ar).color(t, u, v, 255).next();
			bufferBuilder.vertex(ai, n, aj).texture(0.0, as).color(t, u, v, 255).next();
			bufferBuilder.vertex(ak, n, al).texture(0.4999, as).color(t, u, v, 255).next();
			bufferBuilder.vertex(ak, 0.0, al).texture(0.4999, ar).color(t, u, v, 255).next();
			bufferBuilder.vertex(am, 0.0, an).texture(0.0, ar).color(t, u, v, 255).next();
			bufferBuilder.vertex(am, n, an).texture(0.0, as).color(t, u, v, 255).next();
			double at = 0.0;
			if (guardianEntity.age % 2 == 0) {
				at = 0.5;
			}

			bufferBuilder.vertex(y, n, z).texture(0.5, at + 0.5).color(t, u, v, 255).next();
			bufferBuilder.vertex(aa, n, ab).texture(1.0, at + 0.5).color(t, u, v, 255).next();
			bufferBuilder.vertex(ae, n, af).texture(1.0, at).color(t, u, v, 255).next();
			bufferBuilder.vertex(ac, n, ad).texture(0.5, at).color(t, u, v, 255).next();
			tessellator.draw();
			GlStateManager.popMatrix();
		}
	}

	protected Identifier method_3976(GuardianEntity guardianEntity) {
		return SKIN;
	}
}
