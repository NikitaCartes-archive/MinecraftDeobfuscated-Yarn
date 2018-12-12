package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class FlameParticle extends Particle {
	private final float field_3812;

	protected FlameParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
		this.velocityX = this.velocityX * 0.01F + g;
		this.velocityY = this.velocityY * 0.01F + h;
		this.velocityZ = this.velocityZ * 0.01F + i;
		this.posX = this.posX + (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
		this.posY = this.posY + (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
		this.posZ = this.posZ + (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
		this.field_3812 = this.size;
		this.colorRed = 1.0F;
		this.colorGreen = 1.0F;
		this.colorBlue = 1.0F;
		this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2)) + 4;
		this.setSpriteIndex(48);
	}

	@Override
	public void move(double d, double e, double f) {
		this.setBoundingBox(this.getBoundingBox().offset(d, e, f));
		this.repositionFromBoundingBox();
	}

	@Override
	public void buildGeometry(BufferBuilder bufferBuilder, Entity entity, float f, float g, float h, float i, float j, float k) {
		float l = ((float)this.age + f) / (float)this.maxAge;
		this.size = this.field_3812 * (1.0F - l * l * 0.5F);
		super.buildGeometry(bufferBuilder, entity, f, g, h, i, j, k);
	}

	@Override
	public int getColorMultiplier(float f) {
		float g = ((float)this.age + f) / (float)this.maxAge;
		g = MathHelper.clamp(g, 0.0F, 1.0F);
		int i = super.getColorMultiplier(f);
		int j = i & 0xFF;
		int k = i >> 16 & 0xFF;
		j += (int)(g * 15.0F * 16.0F);
		if (j > 240) {
			j = 240;
		}

		return j | k << 16;
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		}

		this.move(this.velocityX, this.velocityY, this.velocityZ);
		this.velocityX *= 0.96F;
		this.velocityY *= 0.96F;
		this.velocityZ *= 0.96F;
		if (this.onGround) {
			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		public Particle method_3036(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new FlameParticle(world, d, e, f, g, h, i);
		}
	}
}
