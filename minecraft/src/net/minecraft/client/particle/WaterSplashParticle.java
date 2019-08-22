package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class WaterSplashParticle extends RainSplashParticle {
	private WaterSplashParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f);
		this.gravityStrength = 0.04F;
		if (h == 0.0 && (g != 0.0 || i != 0.0)) {
			this.velocityX = g;
			this.velocityY = 0.1;
			this.velocityZ = i;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class SplashFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public SplashFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle method_3102(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			WaterSplashParticle waterSplashParticle = new WaterSplashParticle(world, d, e, f, g, h, i);
			waterSplashParticle.setSprite(this.spriteProvider);
			return waterSplashParticle;
		}
	}
}
