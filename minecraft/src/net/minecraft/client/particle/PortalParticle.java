package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class PortalParticle extends Particle {
	private final float field_3887;
	private final double startX;
	private final double startY;
	private final double startZ;

	protected PortalParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
		this.posX = d;
		this.posY = e;
		this.posZ = f;
		this.startX = this.posX;
		this.startY = this.posY;
		this.startZ = this.posZ;
		float j = this.random.nextFloat() * 0.6F + 0.4F;
		this.size = this.random.nextFloat() * 0.2F + 0.5F;
		this.field_3887 = this.size;
		this.colorRed = j * 0.9F;
		this.colorGreen = j * 0.3F;
		this.colorBlue = j;
		this.maxAge = (int)(Math.random() * 10.0) + 40;
		this.setSpriteIndex((int)(Math.random() * 8.0));
	}

	@Override
	public void move(double d, double e, double f) {
		this.setBoundingBox(this.getBoundingBox().offset(d, e, f));
		this.repositionFromBoundingBox();
	}

	@Override
	public void buildGeometry(BufferBuilder bufferBuilder, Entity entity, float f, float g, float h, float i, float j, float k) {
		float l = ((float)this.age + f) / (float)this.maxAge;
		l = 1.0F - l;
		l *= l;
		l = 1.0F - l;
		this.size = this.field_3887 * l;
		super.buildGeometry(bufferBuilder, entity, f, g, h, i, j, k);
	}

	@Override
	public int getColorMultiplier(float f) {
		int i = super.getColorMultiplier(f);
		float g = (float)this.age / (float)this.maxAge;
		g *= g;
		g *= g;
		int j = i & 0xFF;
		int k = i >> 16 & 0xFF;
		k += (int)(g * 15.0F * 16.0F);
		if (k > 240) {
			k = 240;
		}

		return j | k << 16;
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		float f = (float)this.age / (float)this.maxAge;
		float var3 = -f + f * f * 2.0F;
		float var4 = 1.0F - var3;
		this.posX = this.startX + this.velocityX * (double)var4;
		this.posY = this.startY + this.velocityY * (double)var4 + (double)(1.0F - f);
		this.posZ = this.startZ + this.velocityZ * (double)var4;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new PortalParticle(world, d, e, f, g, h, i);
		}
	}
}
