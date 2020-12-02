package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DustParticleEffect;

@Environment(EnvType.CLIENT)
public class RedDustParticle extends AbstractDustParticle<DustParticleEffect> {
	protected RedDustParticle(
		ClientWorld world, double d, double e, double f, double g, double h, double i, DustParticleEffect dustParticleEffect, SpriteProvider spriteProvider
	) {
		super(world, d, e, f, g, h, i, dustParticleEffect, spriteProvider);
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
