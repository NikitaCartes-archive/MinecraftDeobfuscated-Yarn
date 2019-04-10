package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class TotemParticle extends AnimatedParticle {
	private TotemParticle(World world, double d, double e, double f, double g, double h, double i, SpriteProvider spriteProvider) {
		super(world, d, e, f, spriteProvider, -0.05F);
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
		this.scale *= 0.75F;
		this.maxAge = 60 + this.random.nextInt(12);
		this.setSpriteForAge(spriteProvider);
		if (this.random.nextInt(4) == 0) {
			this.setColor(0.6F + this.random.nextFloat() * 0.2F, 0.6F + this.random.nextFloat() * 0.3F, this.random.nextFloat() * 0.2F);
		} else {
			this.setColor(0.1F + this.random.nextFloat() * 0.2F, 0.4F + this.random.nextFloat() * 0.3F, this.random.nextFloat() * 0.2F);
		}

		this.setResistance(0.6F);
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider field_17887;

		public Factory(SpriteProvider spriteProvider) {
			this.field_17887 = spriteProvider;
		}

		public Particle method_3113(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new TotemParticle(world, d, e, f, g, h, i, this.field_17887);
		}
	}
}
