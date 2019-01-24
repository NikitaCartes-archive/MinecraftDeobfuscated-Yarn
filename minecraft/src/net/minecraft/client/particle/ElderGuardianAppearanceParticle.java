package net.minecraft.client.particle;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ElderGuardianAppearanceParticle extends Particle {
	private LivingEntity guardian;

	protected ElderGuardianAppearanceParticle(World world, double d, double e, double f) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.colorRed = 1.0F;
		this.colorGreen = 1.0F;
		this.colorBlue = 1.0F;
		this.velocityX = 0.0;
		this.velocityY = 0.0;
		this.velocityZ = 0.0;
		this.gravityStrength = 0.0F;
		this.maxAge = 30;
	}

	@Override
	public int getParticleGroup() {
		return 3;
	}

	@Override
	public void update() {
		super.update();
		if (this.guardian == null) {
			ElderGuardianEntity elderGuardianEntity = new ElderGuardianEntity(this.world);
			elderGuardianEntity.method_7010();
			this.guardian = elderGuardianEntity;
		}
	}

	@Override
	public void buildGeometry(BufferBuilder bufferBuilder, Entity entity, float f, float g, float h, float i, float j, float k) {
		if (this.guardian != null) {
			EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderManager();
			entityRenderDispatcher.setRenderPosition(Particle.cameraX, Particle.cameraY, Particle.cameraZ);
			float l = 1.0F / ElderGuardianEntity.field_17492;
			float m = ((float)this.age + f) / (float)this.maxAge;
			GlStateManager.depthMask(true);
			GlStateManager.enableBlend();
			GlStateManager.enableDepthTest();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			float n = 240.0F;
			GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0F, 240.0F);
			GlStateManager.pushMatrix();
			float o = 0.05F + 0.5F * MathHelper.sin(m * (float) Math.PI);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, o);
			GlStateManager.translatef(0.0F, 1.8F, 0.0F);
			GlStateManager.rotatef(180.0F - entity.yaw, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(60.0F - 150.0F * m - entity.pitch, 1.0F, 0.0F, 0.0F);
			GlStateManager.translatef(0.0F, -0.4F, -1.5F);
			GlStateManager.scalef(l, l, l);
			this.guardian.yaw = 0.0F;
			this.guardian.headYaw = 0.0F;
			this.guardian.prevYaw = 0.0F;
			this.guardian.prevHeadYaw = 0.0F;
			entityRenderDispatcher.render(this.guardian, 0.0, 0.0, 0.0, 0.0F, f, false);
			GlStateManager.popMatrix();
			GlStateManager.enableDepthTest();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		public Particle method_3042(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new ElderGuardianAppearanceParticle(world, d, e, f);
		}
	}
}
