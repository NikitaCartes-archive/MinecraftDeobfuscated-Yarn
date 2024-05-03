package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ShriekParticleEffect;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public class ShriekParticle extends SpriteBillboardParticle {
	private static final float X_ROTATION = 1.0472F;
	private int delay;

	ShriekParticle(ClientWorld world, double x, double y, double z, int delay) {
		super(world, x, y, z, 0.0, 0.0, 0.0);
		this.scale = 0.85F;
		this.delay = delay;
		this.maxAge = 30;
		this.gravityStrength = 0.0F;
		this.velocityX = 0.0;
		this.velocityY = 0.1;
		this.velocityZ = 0.0;
	}

	@Override
	public float getSize(float tickDelta) {
		return this.scale * MathHelper.clamp(((float)this.age + tickDelta) / (float)this.maxAge * 0.75F, 0.0F, 1.0F);
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		if (this.delay <= 0) {
			this.alpha = 1.0F - MathHelper.clamp(((float)this.age + tickDelta) / (float)this.maxAge, 0.0F, 1.0F);
			Quaternionf quaternionf = new Quaternionf();
			quaternionf.rotationX(-1.0472F);
			this.method_60373(vertexConsumer, camera, quaternionf, tickDelta);
			quaternionf.rotationYXZ((float) -Math.PI, 1.0472F, 0.0F);
			this.method_60373(vertexConsumer, camera, quaternionf, tickDelta);
		}
	}

	@Override
	public int getBrightness(float tint) {
		return 240;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		if (this.delay > 0) {
			this.delay--;
		} else {
			super.tick();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<ShriekParticleEffect> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(ShriekParticleEffect shriekParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			ShriekParticle shriekParticle = new ShriekParticle(clientWorld, d, e, f, shriekParticleEffect.getDelay());
			shriekParticle.setSprite(this.spriteProvider);
			shriekParticle.setAlpha(1.0F);
			return shriekParticle;
		}
	}
}
