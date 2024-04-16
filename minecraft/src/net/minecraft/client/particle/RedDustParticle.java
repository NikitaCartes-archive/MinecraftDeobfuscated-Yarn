package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DustParticleEffect;

@Environment(EnvType.CLIENT)
public class RedDustParticle extends AbstractDustParticle<DustParticleEffect> {
	protected RedDustParticle(
		ClientWorld world,
		double x,
		double y,
		double z,
		double velocityX,
		double velocityY,
		double velocityZ,
		DustParticleEffect parameters,
		SpriteProvider spriteProvider
	) {
		super(world, x, y, z, velocityX, velocityY, velocityZ, parameters, spriteProvider);
		float f = this.random.nextFloat() * 0.4F + 0.6F;
		this.red = this.darken(parameters.getColor().x(), f);
		this.green = this.darken(parameters.getColor().y(), f);
		this.blue = this.darken(parameters.getColor().z(), f);
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DustParticleEffect> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DustParticleEffect dustParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			return new RedDustParticle(clientWorld, d, e, f, g, h, i, dustParticleEffect, this.spriteProvider);
		}
	}
}
