package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class SpitParticle extends ExplosionSmokeParticle {
	protected SpitParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
		this.gravityStrength = 0.5F;
	}

	@Override
	public void update() {
		super.update();
		this.velocityY = this.velocityY - (0.004 + 0.04 * (double)this.gravityStrength);
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new SpitParticle(world, d, e, f, g, h, i);
		}
	}
}
