package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class class_3937 extends class_3940 {
	private class_3937(World world, double d, double e, double f, double g, double h, double i, boolean bl) {
		super(world, d, e, f, g, h, i);
		this.colorRed = 1.0F;
		this.colorGreen = 1.0F;
		this.colorBlue = 1.0F;
		this.method_3087(3.0F);
		this.setBoundingBoxSpacing(0.25F, 0.25F);
		if (bl) {
			this.maxAge = this.random.nextInt(50) + 280;
		} else {
			this.maxAge = this.random.nextInt(50) + 80;
		}

		this.gravityStrength = 3.0E-6F;
		this.velocityX = g;
		this.velocityY = h + (double)(this.random.nextFloat() / 500.0F);
		this.velocityZ = i;
		this.setSpriteIndex(304 + this.random.nextInt(10));
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.velocityX = this.velocityX + (double)(this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean() ? 1 : -1));
		this.velocityZ = this.velocityZ + (double)(this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean() ? 1 : -1));
		this.velocityY = this.velocityY - (double)this.gravityStrength;
		this.move(this.velocityX, this.velocityY, this.velocityZ);
		if (this.age++ < this.maxAge && !(this.colorAlpha <= 0.0F)) {
			if (this.age >= this.maxAge - 60 && this.colorAlpha > 0.01F) {
				this.colorAlpha -= 0.015F;
			}
		} else {
			this.markDead();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_3938 implements ParticleFactory<DefaultParticleType> {
		public Particle method_17579(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			Particle particle = new class_3937(world, d, e, f, g, h, i, false);
			particle.setColorAlpha(0.9F);
			return particle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_3939 implements ParticleFactory<DefaultParticleType> {
		public Particle method_17580(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			Particle particle = new class_3937(world, d, e, f, g, h, i, true);
			particle.setColorAlpha(0.95F);
			return particle;
		}
	}
}
