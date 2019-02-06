package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3999;
import net.minecraft.class_4000;
import net.minecraft.class_4001;
import net.minecraft.class_4002;
import net.minecraft.class_4003;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class PortalParticle extends class_4003 {
	private final double startX;
	private final double startY;
	private final double startZ;

	private PortalParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f);
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
		this.posX = d;
		this.posY = e;
		this.posZ = f;
		this.startX = this.posX;
		this.startY = this.posY;
		this.startZ = this.posZ;
		this.field_17867 = 0.1F * (this.random.nextFloat() * 0.2F + 0.5F);
		float j = this.random.nextFloat() * 0.6F + 0.4F;
		this.colorRed = j * 0.9F;
		this.colorGreen = j * 0.3F;
		this.colorBlue = j;
		this.maxAge = (int)(Math.random() * 10.0) + 40;
	}

	@Override
	public class_3999 method_18122() {
		return class_3999.field_17828;
	}

	@Override
	public void move(double d, double e, double f) {
		this.setBoundingBox(this.getBoundingBox().offset(d, e, f));
		this.repositionFromBoundingBox();
	}

	@Override
	public float method_18132(float f) {
		float g = ((float)this.age + f) / (float)this.maxAge;
		g = 1.0F - g;
		g *= g;
		g = 1.0F - g;
		return this.field_17867 * g;
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
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			float f = (float)this.age / (float)this.maxAge;
			float var3 = -f + f * f * 2.0F;
			float var4 = 1.0F - var3;
			this.posX = this.startX + this.velocityX * (double)var4;
			this.posY = this.startY + this.velocityY * (double)var4 + (double)(1.0F - f);
			this.posZ = this.startZ + this.velocityZ * (double)var4;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17865;

		public Factory(class_4001 arg) {
			this.field_17865 = arg.register(class_4000.field_17850);
		}

		public Particle method_3094(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			PortalParticle portalParticle = new PortalParticle(world, d, e, f, g, h, i);
			portalParticle.method_18140(this.field_17865);
			return portalParticle;
		}
	}
}
