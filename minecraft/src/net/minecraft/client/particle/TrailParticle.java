package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.TrailParticleEffect;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class TrailParticle extends SpriteBillboardParticle {
	private final Vec3d target;

	TrailParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Vec3d target, int color) {
		super(world, x, y, z, velocityX, velocityY, velocityZ);
		color = ColorHelper.scaleRgb(
			color, 0.875F + this.random.nextFloat() * 0.25F, 0.875F + this.random.nextFloat() * 0.25F, 0.875F + this.random.nextFloat() * 0.25F
		);
		this.red = (float)ColorHelper.getRed(color) / 255.0F;
		this.green = (float)ColorHelper.getGreen(color) / 255.0F;
		this.blue = (float)ColorHelper.getBlue(color) / 255.0F;
		this.scale = 0.26F;
		this.target = target;
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
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			int i = this.maxAge - this.age;
			double d = 1.0 / (double)i;
			this.x = MathHelper.lerp(d, this.x, this.target.getX());
			this.y = MathHelper.lerp(d, this.y, this.target.getY());
			this.z = MathHelper.lerp(d, this.z, this.target.getZ());
		}
	}

	@Override
	public int getBrightness(float tint) {
		return 15728880;
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<TrailParticleEffect> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(TrailParticleEffect trailParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			TrailParticle trailParticle = new TrailParticle(clientWorld, d, e, f, g, h, i, trailParticleEffect.target(), trailParticleEffect.color());
			trailParticle.setSprite(this.spriteProvider);
			trailParticle.setMaxAge(clientWorld.random.nextInt(40) + 10);
			return trailParticle;
		}
	}
}
