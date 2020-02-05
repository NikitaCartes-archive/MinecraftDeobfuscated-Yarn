package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class AshParticle extends AscendingParticle {
	protected AshParticle(World world, double d, double e, double f, double g, double h, double i, float j, SpriteProvider spriteProvider) {
		super(world, d, e, f, 0.1F, -0.1F, 0.1F, g, h, i, j, spriteProvider, 0.5F, 20, -0.004);
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new AshParticle(world, d, e, f, g, h, i, 1.0F, this.spriteProvider);
		}
	}
}
