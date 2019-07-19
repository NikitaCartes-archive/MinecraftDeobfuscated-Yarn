package net.minecraft.client.particle;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ElderGuardianAppearanceParticle extends Particle {
	private LivingEntity guardian;

	private ElderGuardianAppearanceParticle(World world, double x, double y, double z) {
		super(world, x, y, z);
		this.gravityStrength = 0.0F;
		this.maxAge = 30;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.CUSTOM;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.guardian == null) {
			ElderGuardianEntity elderGuardianEntity = EntityType.ELDER_GUARDIAN.create(this.world);
			elderGuardianEntity.straightenTail();
			this.guardian = elderGuardianEntity;
		}
	}

	@Override
	public void buildGeometry(BufferBuilder bufferBuilder, Camera camera, float tickDelta, float f, float g, float h, float i, float j) {
		if (this.guardian != null) {
			EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderManager();
			entityRenderDispatcher.setRenderPosition(Particle.cameraX, Particle.cameraY, Particle.cameraZ);
			float k = 1.0F / ElderGuardianEntity.field_17492;
			float l = ((float)this.age + tickDelta) / (float)this.maxAge;
			GlStateManager.depthMask(true);
			GlStateManager.enableBlend();
			GlStateManager.enableDepthTest();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			float m = 240.0F;
			GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0F, 240.0F);
			GlStateManager.pushMatrix();
			float n = 0.05F + 0.5F * MathHelper.sin(l * (float) Math.PI);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, n);
			GlStateManager.translatef(0.0F, 1.8F, 0.0F);
			GlStateManager.rotatef(180.0F - camera.getYaw(), 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(60.0F - 150.0F * l - camera.getPitch(), 1.0F, 0.0F, 0.0F);
			GlStateManager.translatef(0.0F, -0.4F, -1.5F);
			GlStateManager.scalef(k, k, k);
			this.guardian.yaw = 0.0F;
			this.guardian.headYaw = 0.0F;
			this.guardian.prevYaw = 0.0F;
			this.guardian.prevHeadYaw = 0.0F;
			entityRenderDispatcher.render(this.guardian, 0.0, 0.0, 0.0, 0.0F, tickDelta, false);
			GlStateManager.popMatrix();
			GlStateManager.enableDepthTest();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new ElderGuardianAppearanceParticle(world, d, e, f);
		}
	}
}
