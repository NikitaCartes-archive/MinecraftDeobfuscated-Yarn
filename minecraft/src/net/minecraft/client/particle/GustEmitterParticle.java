package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;

@Environment(EnvType.CLIENT)
public class GustEmitterParticle extends NoRenderParticle {
	GustEmitterParticle(ClientWorld clientWorld, double d, double e, double f) {
		super(clientWorld, d, e, f, 0.0, 0.0, 0.0);
		this.maxAge = 7;
	}

	@Override
	public void tick() {
		for (int i = 0; i < 3; i++) {
			double d = this.x + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
			double e = this.y + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
			double f = this.z + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
			this.world.addParticle(ParticleTypes.GUST, d, e, f, (double)((float)this.age / (float)this.maxAge), 0.0, 0.0);
		}

		if (this.age++ == this.maxAge) {
			this.markDead();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			return new GustEmitterParticle(clientWorld, d, e, f);
		}
	}
}
