package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class LavaEmberParticle extends Particle {
	private final float field_3827;

	protected LavaEmberParticle(World world, double d, double e, double f) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.velocityX *= 0.8F;
		this.velocityY *= 0.8F;
		this.velocityZ *= 0.8F;
		this.velocityY = (double)(this.random.nextFloat() * 0.4F + 0.05F);
		this.colorRed = 1.0F;
		this.colorGreen = 1.0F;
		this.colorBlue = 1.0F;
		this.size = this.size * (this.random.nextFloat() * 2.0F + 0.2F);
		this.field_3827 = this.size;
		this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
		this.setSpriteIndex(49);
	}

	@Override
	public int getColorMultiplier(float f) {
		int i = super.getColorMultiplier(f);
		int j = 240;
		int k = i >> 16 & 0xFF;
		return 240 | k << 16;
	}

	@Override
	public void buildGeometry(BufferBuilder bufferBuilder, Entity entity, float f, float g, float h, float i, float j, float k) {
		float l = ((float)this.age + f) / (float)this.maxAge;
		this.size = this.field_3827 * (1.0F - l * l);
		super.buildGeometry(bufferBuilder, entity, f, g, h, i, j, k);
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		}

		float f = (float)this.age / (float)this.maxAge;
		if (this.random.nextFloat() > f) {
			this.world.method_8406(ParticleTypes.field_11251, this.posX, this.posY, this.posZ, this.velocityX, this.velocityY, this.velocityZ);
		}

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

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		public Particle method_3039(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new LavaEmberParticle(world, d, e, f);
		}
	}
}
