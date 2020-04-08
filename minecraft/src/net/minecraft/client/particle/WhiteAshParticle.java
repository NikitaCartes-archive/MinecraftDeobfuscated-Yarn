package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class WhiteAshParticle extends AscendingParticle {
	protected WhiteAshParticle(
		World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, float scaleMultiplier, SpriteProvider spriteProvider
	) {
		super(world, x, y, z, 0.1F, -0.1F, 0.1F, velocityX, velocityY, velocityZ, scaleMultiplier, spriteProvider, 0.0F, 20, -5.0E-4, false);
		this.colorRed = 0.7294118F;
		this.colorGreen = 0.69411767F;
		this.colorBlue = 0.7607843F;
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new WhiteAshParticle(world, d, e, f, g, h, i, 1.0F, this.spriteProvider);
		}
	}
}
