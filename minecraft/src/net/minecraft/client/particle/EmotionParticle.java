package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class EmotionParticle extends SpriteBillboardParticle {
	private EmotionParticle(World world, double x, double y, double z) {
		super(world, x, y, z, 0.0, 0.0, 0.0);
		this.velocityX *= 0.01F;
		this.velocityY *= 0.01F;
		this.velocityZ *= 0.01F;
		this.velocityY += 0.1;
		this.scale *= 1.5F;
		this.maxAge = 16;
		this.collidesWithWorld = false;
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
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			if (this.y == this.prevPosY) {
				this.velocityX *= 1.1;
				this.velocityZ *= 1.1;
			}

			this.velocityX *= 0.86F;
			this.velocityY *= 0.86F;
			this.velocityZ *= 0.86F;
			if (this.onGround) {
				this.velocityX *= 0.7F;
				this.velocityZ *= 0.7F;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class AngryVillagerFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public AngryVillagerFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			EmotionParticle emotionParticle = new EmotionParticle(world, d, e + 0.5, f);
			emotionParticle.setSprite(this.spriteProvider);
			emotionParticle.setColor(1.0F, 1.0F, 1.0F);
			return emotionParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class HeartFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public HeartFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			EmotionParticle emotionParticle = new EmotionParticle(world, d, e, f);
			emotionParticle.setSprite(this.spriteProvider);
			return emotionParticle;
		}
	}
}
