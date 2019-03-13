package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ExplosionEmitterParticle extends NoRenderParticle {
	private int age_;
	private final int maxAge_ = 8;

	private ExplosionEmitterParticle(World world, double d, double e, double f) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
	}

	@Override
	public void update() {
		for (int i = 0; i < 6; i++) {
			double d = this.posX + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
			double e = this.posY + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
			double f = this.posZ + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
			this.world.method_8406(ParticleTypes.field_11236, d, e, f, (double)((float)this.age_ / (float)this.maxAge_), 0.0, 0.0);
		}

		this.age_++;
		if (this.age_ == this.maxAge_) {
			this.markDead();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		public Particle method_3037(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new ExplosionEmitterParticle(world, d, e, f);
		}
	}
}
