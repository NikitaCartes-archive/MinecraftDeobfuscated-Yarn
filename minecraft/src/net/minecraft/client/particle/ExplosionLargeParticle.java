package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ExplosionLargeParticle extends SpriteBillboardParticle {
	private final SpriteProvider field_17815;

	private ExplosionLargeParticle(World world, double d, double e, double f, double g, SpriteProvider spriteProvider) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.maxAge = 6 + this.random.nextInt(4);
		float h = this.random.nextFloat() * 0.6F + 0.4F;
		this.colorRed = h;
		this.colorGreen = h;
		this.colorBlue = h;
		this.scale = 2.0F * (1.0F - (float)g * 0.5F);
		this.field_17815 = spriteProvider;
		this.method_18142(spriteProvider);
	}

	@Override
	public int getColorMultiplier(float f) {
		return 15728880;
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.method_18142(this.field_17815);
		}
	}

	@Override
	public ParticleTextureSheet getTextureSheet() {
		return ParticleTextureSheet.PARTICLE_SHEET_LIT;
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider field_17816;

		public Factory(SpriteProvider spriteProvider) {
			this.field_17816 = spriteProvider;
		}

		public Particle method_3038(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new ExplosionLargeParticle(world, d, e, f, g, this.field_17816);
		}
	}
}
