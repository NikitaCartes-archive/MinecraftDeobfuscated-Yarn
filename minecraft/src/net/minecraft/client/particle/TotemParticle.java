package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class TotemParticle extends AnimatedParticle {
	public TotemParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, 176, 8, -0.05F);
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
		this.size *= 0.75F;
		this.maxAge = 60 + this.random.nextInt(12);
		if (this.random.nextInt(4) == 0) {
			this.setColor(0.6F + this.random.nextFloat() * 0.2F, 0.6F + this.random.nextFloat() * 0.3F, this.random.nextFloat() * 0.2F);
		} else {
			this.setColor(0.1F + this.random.nextFloat() * 0.2F, 0.4F + this.random.nextFloat() * 0.3F, this.random.nextFloat() * 0.2F);
		}

		this.method_3091(0.6F);
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		public Particle method_3113(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new TotemParticle(world, d, e, f, g, h, i);
		}
	}
}
