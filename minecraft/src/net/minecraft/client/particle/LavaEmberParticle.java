package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4000;
import net.minecraft.class_4001;
import net.minecraft.class_4002;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class LavaEmberParticle extends SpriteBillboardParticle {
	private LavaEmberParticle(World world, double d, double e, double f) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.velocityX *= 0.8F;
		this.velocityY *= 0.8F;
		this.velocityZ *= 0.8F;
		this.velocityY = (double)(this.random.nextFloat() * 0.4F + 0.05F);
		this.field_17867 = this.field_17867 * (this.random.nextFloat() * 2.0F + 0.2F);
		this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
	}

	@Override
	public ParticleTextureSheet getTextureSheet() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public int getColorMultiplier(float f) {
		int i = super.getColorMultiplier(f);
		int j = 240;
		int k = i >> 16 & 0xFF;
		return 240 | k << 16;
	}

	@Override
	public float method_18132(float f) {
		float g = ((float)this.age + f) / (float)this.maxAge;
		return this.field_17867 * (1.0F - g * g);
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		float f = (float)this.age / (float)this.maxAge;
		if (this.random.nextFloat() > f) {
			this.world.addParticle(ParticleTypes.field_11251, this.posX, this.posY, this.posZ, this.velocityX, this.velocityY, this.velocityZ);
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
		private final class_4002 field_17818;

		public Factory(class_4001 arg) {
			this.field_17818 = arg.method_18137(class_4000.field_17856);
		}

		public Particle method_3039(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			LavaEmberParticle lavaEmberParticle = new LavaEmberParticle(world, d, e, f);
			lavaEmberParticle.method_18140(this.field_17818);
			return lavaEmberParticle;
		}
	}
}
