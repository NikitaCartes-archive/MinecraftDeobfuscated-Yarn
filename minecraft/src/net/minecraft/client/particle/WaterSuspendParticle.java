package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class WaterSuspendParticle extends SpriteBillboardParticle {
	private WaterSuspendParticle(World world, double x, double y, double z) {
		super(world, x, y - 0.125, z);
		this.colorRed = 0.4F;
		this.colorGreen = 0.4F;
		this.colorBlue = 0.7F;
		this.setBoundingBoxSpacing(0.01F, 0.01F);
		this.scale = this.scale * (this.random.nextFloat() * 0.6F + 0.2F);
		this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
		this.collidesWithWorld = false;
	}

	private WaterSuspendParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e - 0.125, f, g, h, i);
		this.setBoundingBoxSpacing(0.01F, 0.01F);
		this.scale = this.scale * (this.random.nextFloat() * 0.6F + 0.6F);
		this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public void tick() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.maxAge-- <= 0) {
			this.markDead();
		} else {
			this.move(this.velocityX, this.velocityY, this.velocityZ);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class CrimsonSporeFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public CrimsonSporeFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			WaterSuspendParticle waterSuspendParticle = new WaterSuspendParticle(world, d, e, f, g, h, i);
			waterSuspendParticle.setSprite(this.spriteProvider);
			waterSuspendParticle.setColor(0.9F, 0.4F, 0.5F);
			return waterSuspendParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class UnderwaterFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public UnderwaterFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			WaterSuspendParticle waterSuspendParticle = new WaterSuspendParticle(world, d, e, f);
			waterSuspendParticle.setSprite(this.spriteProvider);
			return waterSuspendParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class WarpedSporeFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public WarpedSporeFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			WaterSuspendParticle waterSuspendParticle = new WaterSuspendParticle(world, d, e, f, g, h, i);
			waterSuspendParticle.setSprite(this.spriteProvider);
			waterSuspendParticle.setColor(0.1F, 0.1F, 0.3F);
			waterSuspendParticle.setBoundingBoxSpacing(0.001F, 0.001F);
			return waterSuspendParticle;
		}
	}
}
