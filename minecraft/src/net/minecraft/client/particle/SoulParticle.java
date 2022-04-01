package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(EnvType.CLIENT)
public class SoulParticle extends AbstractSlowingParticle {
	private final SpriteProvider spriteProvider;
	protected boolean field_38667;

	SoulParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
		super(world, x, y, z, velocityX, velocityY, velocityZ);
		this.spriteProvider = spriteProvider;
		this.scale(1.5F);
		this.setSpriteForAge(spriteProvider);
	}

	@Override
	public int getBrightness(float tint) {
		return this.field_38667 ? 240 : super.getBrightness(tint);
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		super.tick();
		this.setSpriteForAge(this.spriteProvider);
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			SoulParticle soulParticle = new SoulParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
			soulParticle.setAlpha(1.0F);
			return soulParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_7361 implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider field_38668;

		public class_7361(SpriteProvider spriteProvider) {
			this.field_38668 = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			SoulParticle soulParticle = new SoulParticle(clientWorld, d, e, f, g, h, i, this.field_38668);
			soulParticle.setAlpha(1.0F);
			soulParticle.field_38667 = true;
			return soulParticle;
		}
	}
}
