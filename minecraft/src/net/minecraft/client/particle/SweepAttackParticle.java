package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(EnvType.CLIENT)
public class SweepAttackParticle extends SpriteBillboardParticle {
	private final SpriteProvider spriteProvider;

	private SweepAttackParticle(ClientWorld world, double x, double y, double z, double scaleMultiplier, SpriteProvider spriteProvider) {
		super(world, x, y, z, 0.0, 0.0, 0.0);
		this.spriteProvider = spriteProvider;
		this.maxAge = 4;
		float f = this.random.nextFloat() * 0.6F + 0.4F;
		this.colorRed = f;
		this.colorGreen = f;
		this.colorBlue = f;
		this.scale = 1.0F - (float)scaleMultiplier * 0.5F;
		this.setSpriteForAge(spriteProvider);
	}

	@Override
	public int getColorMultiplier(float tint) {
		return 15728880;
	}

	@Override
	public void tick() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.setSpriteForAge(this.spriteProvider);
		}
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_LIT;
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle method_3006(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			return new SweepAttackParticle(clientWorld, d, e, f, g, this.spriteProvider);
		}
	}
}
