package net.minecraft.client.particle;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleGroup;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class WaterSuspendParticle extends SpriteBillboardParticle {
	WaterSuspendParticle(ClientWorld world, SpriteProvider spriteProvider, double x, double y, double z) {
		super(world, x, y - 0.125, z);
		this.setBoundingBoxSpacing(0.01F, 0.01F);
		this.setSprite(spriteProvider);
		this.scale = this.scale * (this.random.nextFloat() * 0.6F + 0.2F);
		this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
		this.collidesWithWorld = false;
		this.velocityMultiplier = 1.0F;
		this.gravityStrength = 0.0F;
	}

	WaterSuspendParticle(ClientWorld world, SpriteProvider spriteProvider, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		super(world, x, y - 0.125, z, velocityX, velocityY, velocityZ);
		this.setBoundingBoxSpacing(0.01F, 0.01F);
		this.setSprite(spriteProvider);
		this.scale = this.scale * (this.random.nextFloat() * 0.6F + 0.6F);
		this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
		this.collidesWithWorld = false;
		this.velocityMultiplier = 1.0F;
		this.gravityStrength = 0.0F;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Environment(EnvType.CLIENT)
	public static class CrimsonSporeFactory implements ParticleFactory<SimpleParticleType> {
		private final SpriteProvider spriteProvider;

		public CrimsonSporeFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			Random random = clientWorld.random;
			double j = random.nextGaussian() * 1.0E-6F;
			double k = random.nextGaussian() * 1.0E-4F;
			double l = random.nextGaussian() * 1.0E-6F;
			WaterSuspendParticle waterSuspendParticle = new WaterSuspendParticle(clientWorld, this.spriteProvider, d, e, f, j, k, l);
			waterSuspendParticle.setColor(0.9F, 0.4F, 0.5F);
			return waterSuspendParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class SporeBlossomAirFactory implements ParticleFactory<SimpleParticleType> {
		private final SpriteProvider spriteProvider;

		public SporeBlossomAirFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			WaterSuspendParticle waterSuspendParticle = new WaterSuspendParticle(clientWorld, this.spriteProvider, d, e, f, 0.0, -0.8F, 0.0) {
				@Override
				public Optional<ParticleGroup> getGroup() {
					return Optional.of(ParticleGroup.SPORE_BLOSSOM_AIR);
				}
			};
			waterSuspendParticle.maxAge = MathHelper.nextBetween(clientWorld.random, 500, 1000);
			waterSuspendParticle.gravityStrength = 0.01F;
			waterSuspendParticle.setColor(0.32F, 0.5F, 0.22F);
			return waterSuspendParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class UnderwaterFactory implements ParticleFactory<SimpleParticleType> {
		private final SpriteProvider spriteProvider;

		public UnderwaterFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			WaterSuspendParticle waterSuspendParticle = new WaterSuspendParticle(clientWorld, this.spriteProvider, d, e, f);
			waterSuspendParticle.setColor(0.4F, 0.4F, 0.7F);
			return waterSuspendParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class WarpedSporeFactory implements ParticleFactory<SimpleParticleType> {
		private final SpriteProvider spriteProvider;

		public WarpedSporeFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			double j = (double)clientWorld.random.nextFloat() * -1.9 * (double)clientWorld.random.nextFloat() * 0.1;
			WaterSuspendParticle waterSuspendParticle = new WaterSuspendParticle(clientWorld, this.spriteProvider, d, e, f, 0.0, j, 0.0);
			waterSuspendParticle.setColor(0.1F, 0.1F, 0.3F);
			waterSuspendParticle.setBoundingBoxSpacing(0.001F, 0.001F);
			return waterSuspendParticle;
		}
	}
}
