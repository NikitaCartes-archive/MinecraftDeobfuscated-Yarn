package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class WhiteAshParticle extends AscendingParticle {
	private static final int COLOR = 12235202;

	protected WhiteAshParticle(
		ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, float scaleMultiplier, SpriteProvider spriteProvider
	) {
		super(world, x, y, z, 0.1F, -0.1F, 0.1F, velocityX, velocityY, velocityZ, scaleMultiplier, spriteProvider, 0.0F, 20, 0.0125F, false);
		this.red = (float)ColorHelper.Argb.getRed(12235202) / 255.0F;
		this.green = (float)ColorHelper.Argb.getGreen(12235202) / 255.0F;
		this.blue = (float)ColorHelper.Argb.getBlue(12235202) / 255.0F;
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<SimpleParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			Random random = clientWorld.random;
			double j = (double)random.nextFloat() * -1.9 * (double)random.nextFloat() * 0.1;
			double k = (double)random.nextFloat() * -0.5 * (double)random.nextFloat() * 0.1 * 5.0;
			double l = (double)random.nextFloat() * -1.9 * (double)random.nextFloat() * 0.1;
			return new WhiteAshParticle(clientWorld, d, e, f, j, k, l, 1.0F, this.spriteProvider);
		}
	}
}
