package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.TexturedParticle;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class WaterSplashParticle extends RainSplashParticle {
	protected WaterSplashParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f);
		this.gravityStrength = 0.04F;
		this.setSpriteIndex(20 + this.random.nextInt(3));
		if (h == 0.0 && (g != 0.0 || i != 0.0)) {
			this.velocityX = g;
			this.velocityY = 0.1;
			this.velocityZ = i;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements FactoryParticle<TexturedParticle> {
		public Particle createParticle(TexturedParticle texturedParticle, World world, double d, double e, double f, double g, double h, double i) {
			return new WaterSplashParticle(world, d, e, f, g, h, i);
		}
	}
}
