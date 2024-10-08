package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DustParticleEffect;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class RedDustParticle extends AbstractDustParticle<DustParticleEffect> {
	protected RedDustParticle(
		ClientWorld world,
		double x,
		double y,
		double z,
		double velocityX,
		double velocityY,
		double velocityZ,
		DustParticleEffect parameters,
		SpriteProvider spriteProvider
	) {
		super(world, x, y, z, velocityX, velocityY, velocityZ, parameters, spriteProvider);
		float f = this.random.nextFloat() * 0.4F + 0.6F;
		Vector3f vector3f = parameters.getColor();
		this.red = this.darken(vector3f.x(), f);
		this.green = this.darken(vector3f.y(), f);
		this.blue = this.darken(vector3f.z(), f);
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
