package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class EndRodParticle extends AnimatedParticle {
	public EndRodParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, 176, 8, -5.0E-4F);
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
		this.size *= 0.75F;
		this.maxAge = 60 + this.random.nextInt(12);
		this.setTargetColor(15916745);
	}

	@Override
	public void move(double d, double e, double f) {
		this.setBoundingBox(this.getBoundingBox().offset(d, e, f));
		this.repositionFromBoundingBox();
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		public Particle method_3024(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new EndRodParticle(world, d, e, f, g, h, i);
		}
	}
}
