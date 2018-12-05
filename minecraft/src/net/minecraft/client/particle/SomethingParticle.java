package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.TexturedParticle;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class SomethingParticle extends Particle {
	protected SomethingParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
		float j = this.random.nextFloat() * 0.1F + 0.2F;
		this.colorRed = j;
		this.colorGreen = j;
		this.colorBlue = j;
		this.setSpriteIndex(0);
		this.setBoundingBoxSpacing(0.02F, 0.02F);
		this.size = this.size * (this.random.nextFloat() * 0.6F + 0.5F);
		this.velocityX *= 0.02F;
		this.velocityY *= 0.02F;
		this.velocityZ *= 0.02F;
		this.maxAge = (int)(20.0 / (Math.random() * 0.8 + 0.2));
	}

	@Override
	public void addPos(double d, double e, double f) {
		this.setBoundingBox(this.getBoundingBox().offset(d, e, f));
		this.repositionFromBoundingBox();
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.addPos(this.velocityX, this.velocityY, this.velocityZ);
		this.velocityX *= 0.99;
		this.velocityY *= 0.99;
		this.velocityZ *= 0.99;
		if (this.maxAge-- <= 0) {
			this.markDead();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class FactoryHappy implements FactoryParticle<TexturedParticle> {
		public Particle createParticle(TexturedParticle texturedParticle, World world, double d, double e, double f, double g, double h, double i) {
			Particle particle = new SomethingParticle(world, d, e, f, g, h, i);
			particle.setSpriteIndex(82);
			particle.setColor(1.0F, 1.0F, 1.0F);
			return particle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_730 implements FactoryParticle<TexturedParticle> {
		public Particle createParticle(TexturedParticle texturedParticle, World world, double d, double e, double f, double g, double h, double i) {
			Particle particle = new SomethingParticle(world, d, e, f, g, h, i);
			particle.setColor(0.3F, 0.5F, 1.0F);
			particle.setColorAlpha(1.0F - world.random.nextFloat() * 0.7F);
			particle.setMaxAge(particle.method_3082() / 2);
			return particle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_732 implements FactoryParticle<TexturedParticle> {
		public Particle createParticle(TexturedParticle texturedParticle, World world, double d, double e, double f, double g, double h, double i) {
			return new SomethingParticle(world, d, e, f, g, h, i);
		}
	}
}
