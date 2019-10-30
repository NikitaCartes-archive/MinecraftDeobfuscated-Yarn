package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class LavaEmberParticle extends SpriteBillboardParticle {
	private LavaEmberParticle(World world, double x, double y, double z) {
		super(world, x, y, z, 0.0, 0.0, 0.0);
		this.velocityX *= 0.8F;
		this.velocityY *= 0.8F;
		this.velocityZ *= 0.8F;
		this.velocityY = (double)(this.random.nextFloat() * 0.4F + 0.05F);
		this.scale = this.scale * (this.random.nextFloat() * 2.0F + 0.2F);
		this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public int getColorMultiplier(float tint) {
		int i = super.getColorMultiplier(tint);
		int j = 240;
		int k = i >> 16 & 0xFF;
		return 240 | k << 16;
	}

	@Override
	public float getSize(float tickDelta) {
		float f = ((float)this.age + tickDelta) / (float)this.maxAge;
		return this.scale * (1.0F - f * f);
	}

	@Override
	public void tick() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		float f = (float)this.age / (float)this.maxAge;
		if (this.random.nextFloat() > f) {
			this.world.addParticle(ParticleTypes.SMOKE, this.x, this.y, this.z, this.velocityX, this.velocityY, this.velocityZ);
		}

		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.velocityY -= 0.03;
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= 0.999F;
			this.velocityY *= 0.999F;
			this.velocityZ *= 0.999F;
			if (this.onGround) {
				this.velocityX *= 0.7F;
				this.velocityZ *= 0.7F;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider field_17818;

		public Factory(SpriteProvider spriteProvider) {
			this.field_17818 = spriteProvider;
		}

		public Particle method_3039(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			LavaEmberParticle lavaEmberParticle = new LavaEmberParticle(world, d, e, f);
			lavaEmberParticle.setSprite(this.field_17818);
			return lavaEmberParticle;
		}
	}
}
