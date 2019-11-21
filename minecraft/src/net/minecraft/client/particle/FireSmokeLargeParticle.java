package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class FireSmokeLargeParticle extends FireSmokeParticle {
	protected FireSmokeLargeParticle(World world, double x, double y, double z, double d, double e, double f, SpriteProvider spriteProvider) {
		super(world, x, y, z, d, e, f, 2.5F, spriteProvider);
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider field_17817;

		public Factory(SpriteProvider spriteProvider) {
			this.field_17817 = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new FireSmokeLargeParticle(world, d, e, f, g, h, i, this.field_17817);
		}
	}
}
