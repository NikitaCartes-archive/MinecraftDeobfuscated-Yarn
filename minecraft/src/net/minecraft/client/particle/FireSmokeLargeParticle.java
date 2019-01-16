package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class FireSmokeLargeParticle extends FireSmokeParticle {
	protected FireSmokeLargeParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i, 2.5F);
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new FireSmokeLargeParticle(world, d, e, f, g, h, i);
		}
	}
}
