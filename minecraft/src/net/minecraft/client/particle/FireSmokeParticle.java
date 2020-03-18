package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class FireSmokeParticle extends AscendingParticle {
	protected FireSmokeParticle(World world, double x, double y, double z, double d, double e, double f, float g, SpriteProvider spriteProvider) {
		super(world, x, y, z, 0.1F, 0.1F, 0.1F, d, e, f, g, spriteProvider, 0.3F, 8, 0.004, true);
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new FireSmokeParticle(world, d, e, f, g, h, i, 1.0F, this.spriteProvider);
		}
	}
}
