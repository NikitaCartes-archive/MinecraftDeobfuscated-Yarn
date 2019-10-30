package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class FishingParticle extends SpriteBillboardParticle {
	private final SpriteProvider spriteProvider;

	private FishingParticle(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
		super(world, x, y, z, 0.0, 0.0, 0.0);
		this.spriteProvider = spriteProvider;
		this.velocityX *= 0.3F;
		this.velocityY = Math.random() * 0.2F + 0.1F;
		this.velocityZ *= 0.3F;
		this.setBoundingBoxSpacing(0.01F, 0.01F);
		this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
		this.setSpriteForAge(spriteProvider);
		this.gravityStrength = 0.0F;
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.velocityZ = velocityZ;
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
		int i = 60 - this.maxAge;
		if (this.maxAge-- <= 0) {
			this.markDead();
		} else {
			this.velocityY = this.velocityY - (double)this.gravityStrength;
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= 0.98F;
			this.velocityY *= 0.98F;
			this.velocityZ *= 0.98F;
			float f = (float)i * 0.001F;
			this.setBoundingBoxSpacing(f, f);
			this.setSprite(this.spriteProvider.getSprite(i % 4, 4));
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider field_17889;

		public Factory(SpriteProvider spriteProvider) {
			this.field_17889 = spriteProvider;
		}

		public Particle method_3115(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new FishingParticle(world, d, e, f, g, h, i, this.field_17889);
		}
	}
}
