package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ExplosionSmokeParticle extends SpriteBillboardParticle {
	private final SpriteProvider field_17806;

	protected ExplosionSmokeParticle(World world, double d, double e, double f, double g, double h, double i, SpriteProvider spriteProvider) {
		super(world, d, e, f);
		this.field_17806 = spriteProvider;
		this.velocityX = g + (Math.random() * 2.0 - 1.0) * 0.05F;
		this.velocityY = h + (Math.random() * 2.0 - 1.0) * 0.05F;
		this.velocityZ = i + (Math.random() * 2.0 - 1.0) * 0.05F;
		float j = this.random.nextFloat() * 0.3F + 0.7F;
		this.colorRed = j;
		this.colorGreen = j;
		this.colorBlue = j;
		this.scale = 0.1F * (this.random.nextFloat() * this.random.nextFloat() * 6.0F + 1.0F);
		this.maxAge = (int)(16.0 / ((double)this.random.nextFloat() * 0.8 + 0.2)) + 2;
		this.method_18142(spriteProvider);
	}

	@Override
	public ParticleTextureSheet getTextureSheet() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.method_18142(this.field_17806);
			this.velocityY += 0.004;
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= 0.9F;
			this.velocityY *= 0.9F;
			this.velocityZ *= 0.9F;
			if (this.onGround) {
				this.velocityX *= 0.7F;
				this.velocityZ *= 0.7F;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider field_17807;

		public Factory(SpriteProvider spriteProvider) {
			this.field_17807 = spriteProvider;
		}

		public Particle method_3023(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new ExplosionSmokeParticle(world, d, e, f, g, h, i, this.field_17807);
		}
	}
}
