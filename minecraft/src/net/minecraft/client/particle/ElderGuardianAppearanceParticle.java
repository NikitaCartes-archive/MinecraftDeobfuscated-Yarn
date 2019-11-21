package net.minecraft.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
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
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		if (this.guardian != null) {
			EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderManager();
			float f = 1.0F / ElderGuardianEntity.field_17492;
			float g = ((float)this.age + tickDelta) / (float)this.maxAge;
			RenderSystem.depthMask(true);
			RenderSystem.enableBlend();
			RenderSystem.enableDepthTest();
			RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			float h = 240.0F;
			RenderSystem.glMultiTexCoord2f(33986, 240.0F, 240.0F);
			RenderSystem.pushMatrix();
			float i = 0.05F + 0.5F * MathHelper.sin(g * (float) Math.PI);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, i);
			RenderSystem.translatef(0.0F, 1.8F, 0.0F);
			RenderSystem.rotatef(180.0F - camera.getYaw(), 0.0F, 1.0F, 0.0F);
			RenderSystem.rotatef(60.0F - 150.0F * g - camera.getPitch(), 1.0F, 0.0F, 0.0F);
			RenderSystem.translatef(0.0F, -0.4F, -1.5F);
			RenderSystem.scalef(f, f, f);
			this.guardian.yaw = 0.0F;
			this.guardian.headYaw = 0.0F;
			this.guardian.prevYaw = 0.0F;
			this.guardian.prevHeadYaw = 0.0F;
			VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
			entityRenderDispatcher.render(this.guardian, 0.0, 0.0, 0.0, 0.0F, tickDelta, new MatrixStack(), immediate, 15728880);
			immediate.draw();
			RenderSystem.popMatrix();
			RenderSystem.enableDepthTest();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new ElderGuardianAppearanceParticle(world, d, e, f);
		}
	}
}
