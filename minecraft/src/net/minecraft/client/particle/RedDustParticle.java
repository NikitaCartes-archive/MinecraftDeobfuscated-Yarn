package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class RedDustParticle extends SpriteBillboardParticle {
	private final SpriteProvider spriteProvider;

	private RedDustParticle(
		World world,
		double x,
		double y,
		double z,
		double velocityX,
		double velocityY,
		double velocityZ,
		DustParticleEffect dustParticleEffect,
		SpriteProvider spriteProvider
	) {
		super(world, x, y, z, velocityX, velocityY, velocityZ);
		this.spriteProvider = spriteProvider;
		this.velocityX *= 0.1F;
		this.velocityY *= 0.1F;
		this.velocityZ *= 0.1F;
		float f = (float)Math.random() * 0.4F + 0.6F;
		this.colorRed = ((float)(Math.random() * 0.2F) + 0.8F) * dustParticleEffect.getRed() * f;
		this.colorGreen = ((float)(Math.random() * 0.2F) + 0.8F) * dustParticleEffect.getGreen() * f;
		this.colorBlue = ((float)(Math.random() * 0.2F) + 0.8F) * dustParticleEffect.getBlue() * f;
		this.scale = this.scale * 0.75F * dustParticleEffect.getScale();
		int i = (int)(8.0 / (Math.random() * 0.8 + 0.2));
		this.maxAge = (int)Math.max((float)i * dustParticleEffect.getScale(), 1.0F);
		this.setSpriteForAge(spriteProvider);
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public float getSize(float tickDelta) {
		return this.scale * MathHelper.clamp(((float)this.age + tickDelta) / (float)this.maxAge * 32.0F, 0.0F, 1.0F);
	}

	@Override
	public void tick() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.setSpriteForAge(this.spriteProvider);
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			if (this.y == this.prevPosY) {
				this.velocityX *= 1.1;
				this.velocityZ *= 1.1;
			}

			this.velocityX *= 0.96F;
			this.velocityY *= 0.96F;
			this.velocityZ *= 0.96F;
			if (this.onGround) {
				this.velocityX *= 0.7F;
				this.velocityZ *= 0.7F;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DustParticleEffect> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DustParticleEffect dustParticleEffect, World world, double d, double e, double f, double g, double h, double i) {
			return new RedDustParticle(world, d, e, f, g, h, i, dustParticleEffect, this.spriteProvider);
		}
	}
}
