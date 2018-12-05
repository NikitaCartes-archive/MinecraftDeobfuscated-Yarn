package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.TexturedParticle;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class EnchantGlyphParticle extends Particle {
	private final double startX;
	private final double startY;
	private final double startZ;

	protected EnchantGlyphParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
		this.startX = d;
		this.startY = e;
		this.startZ = f;
		this.prevPosX = d + g;
		this.prevPosY = e + h;
		this.prevPosZ = f + i;
		this.posX = this.prevPosX;
		this.posY = this.prevPosY;
		this.posZ = this.prevPosZ;
		float j = this.random.nextFloat() * 0.6F + 0.4F;
		this.size = this.random.nextFloat() * 0.5F + 0.2F;
		this.colorRed = 0.9F * j;
		this.colorGreen = 0.9F * j;
		this.colorBlue = j;
		this.collidesWithWorld = false;
		this.maxAge = (int)(Math.random() * 10.0) + 30;
		this.setSpriteIndex((int)(Math.random() * 26.0 + 1.0 + 224.0));
	}

	@Override
	public void addPos(double d, double e, double f) {
		this.setBoundingBox(this.getBoundingBox().offset(d, e, f));
		this.repositionFromBoundingBox();
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
		f = 1.0F - f;
		float g = 1.0F - f;
		g *= g;
		g *= g;
		this.posX = this.startX + this.velocityX * (double)f;
		this.posY = this.startY + this.velocityY * (double)f - (double)(g * 1.2F);
		this.posZ = this.startZ + this.velocityZ * (double)f;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_669 implements FactoryParticle<TexturedParticle> {
		public Particle createParticle(TexturedParticle texturedParticle, World world, double d, double e, double f, double g, double h, double i) {
			EnchantGlyphParticle enchantGlyphParticle = new EnchantGlyphParticle(world, d, e, f, g, h, i);
			enchantGlyphParticle.setSpriteIndex(208);
			return enchantGlyphParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_670 implements FactoryParticle<TexturedParticle> {
		public Particle createParticle(TexturedParticle texturedParticle, World world, double d, double e, double f, double g, double h, double i) {
			return new EnchantGlyphParticle(world, d, e, f, g, h, i);
		}
	}
}
