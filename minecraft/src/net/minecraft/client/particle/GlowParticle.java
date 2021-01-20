package net.minecraft.client.particle;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class GlowParticle extends SpriteBillboardParticle {
	private static final Random RANDOM = new Random();
	private final SpriteProvider spriteProvider;

	private GlowParticle(ClientWorld world, double x, double y, double z, double d, double e, double f, SpriteProvider spriteProvider) {
		super(world, x, y, z, 0.5 - RANDOM.nextDouble(), e, 0.5 - RANDOM.nextDouble());
		this.spriteProvider = spriteProvider;
		this.velocityY *= 0.2F;
		if (d == 0.0 && f == 0.0) {
			this.velocityX *= 0.1F;
			this.velocityZ *= 0.1F;
		}

		this.scale *= 0.75F;
		this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
		if (this.random.nextBoolean()) {
			this.setColor(0.6F, 1.0F, 0.8F);
		} else {
			this.setColor(0.08F, 0.4F, 0.4F);
		}

		this.collidesWithWorld = false;
		this.setSpriteForAge(spriteProvider);
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public int getBrightness(float tint) {
		float f = ((float)this.age + tint) / (float)this.maxAge;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		int i = super.getBrightness(tint);
		int j = i & 0xFF;
		int k = i >> 16 & 0xFF;
		j += (int)(f * 15.0F * 16.0F);
		if (j > 240) {
			j = 240;
		}

		return j | k << 16;
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
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			return new GlowParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
		}
	}
}
