package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(EnvType.CLIENT)
public class SnowflakeParticle extends SpriteBillboardParticle {
	private final SpriteProvider spriteProvider;

	protected SnowflakeParticle(
		ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider
	) {
		super(world, x, y, z);
		this.spriteProvider = spriteProvider;
		this.velocityX = velocityX + (Math.random() * 2.0 - 1.0) * 0.05F;
		this.velocityY = velocityY + (Math.random() * 2.0 - 1.0) * 0.05F;
		this.velocityZ = velocityZ + (Math.random() * 2.0 - 1.0) * 0.05F;
		this.scale = 0.1F * (this.random.nextFloat() * this.random.nextFloat() * 1.0F + 1.0F);
		this.maxAge = (int)(16.0 / ((double)this.random.nextFloat() * 0.8 + 0.2)) + 2;
		this.setSpriteForAge(spriteProvider);
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
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
			this.velocityY -= 0.009;
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= 0.95F;
			this.velocityY *= 0.9F;
			this.velocityZ *= 0.95F;
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
			SnowflakeParticle snowflakeParticle = new SnowflakeParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
			snowflakeParticle.setColor(0.923F, 0.964F, 0.999F);
			return snowflakeParticle;
		}
	}
}
