package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class SweepAttackParticle extends SpriteBillboardParticle {
	private final SpriteProvider field_17781;

	private SweepAttackParticle(World world, double d, double e, double f, double g, SpriteProvider spriteProvider) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.field_17781 = spriteProvider;
		this.maxAge = 4;
		float h = this.random.nextFloat() * 0.6F + 0.4F;
		this.colorRed = h;
		this.colorGreen = h;
		this.colorBlue = h;
		this.scale = 1.0F - (float)g * 0.5F;
		this.setSpriteForAge(spriteProvider);
	}

	@Override
	public int getColorMultiplier(float f) {
		return 15728880;
	}

	@Override
	public void update() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.setSpriteForAge(this.field_17781);
		}
	}

	@Override
	public ParticleTextureSheet getTextureSheet() {
		return ParticleTextureSheet.PARTICLE_SHEET_LIT;
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider field_17782;

		public Factory(SpriteProvider spriteProvider) {
			this.field_17782 = spriteProvider;
		}

		public Particle method_3006(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new SweepAttackParticle(world, d, e, f, g, this.field_17782);
		}
	}
}
