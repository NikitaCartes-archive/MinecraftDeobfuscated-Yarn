package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.ColorHelper;

@Environment(EnvType.CLIENT)
public class DustPlumeParticle extends AscendingParticle {
	private static final int COLOR = 12235202;

	protected DustPlumeParticle(
		ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, float scaleMultiplier, SpriteProvider spriteProvider
	) {
		super(world, x, y, z, 0.7F, 0.6F, 0.7F, velocityX, velocityY + 0.15F, velocityZ, scaleMultiplier, spriteProvider, 0.5F, 7, 0.5F, false);
		float f = (float)Math.random() * 0.2F;
		this.red = (float)ColorHelper.Argb.getRed(12235202) / 255.0F - f;
		this.green = (float)ColorHelper.Argb.getGreen(12235202) / 255.0F - f;
		this.blue = (float)ColorHelper.Argb.getBlue(12235202) / 255.0F - f;
	}

	@Override
	public void tick() {
		this.gravityStrength = 0.88F * this.gravityStrength;
		this.velocityMultiplier = 0.92F * this.velocityMultiplier;
		super.tick();
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<SimpleParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			return new DustPlumeParticle(clientWorld, d, e, f, g, h, i, 1.0F, this.spriteProvider);
		}
	}
}
