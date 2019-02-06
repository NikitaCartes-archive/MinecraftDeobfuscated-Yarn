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
public class EnchantGlyphParticle extends class_4003 {
	private final double startX;
	private final double startY;
	private final double startZ;

	private EnchantGlyphParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f);
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
		this.field_17867 = 0.1F * (this.random.nextFloat() * 0.5F + 0.2F);
		float j = this.random.nextFloat() * 0.6F + 0.4F;
		this.colorRed = 0.9F * j;
		this.colorGreen = 0.9F * j;
		this.colorBlue = j;
		this.collidesWithWorld = false;
		this.maxAge = (int)(Math.random() * 10.0) + 30;
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
			f = 1.0F - f;
			float g = 1.0F - f;
			g *= g;
			g *= g;
			this.posX = this.startX + this.velocityX * (double)f;
			this.posY = this.startY + this.velocityY * (double)f - (double)(g * 1.2F);
			this.posZ = this.startZ + this.velocityZ * (double)f;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class EnchantFactory implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17803;

		public EnchantFactory(class_4001 arg) {
			this.field_17803 = arg.method_18137(class_4000.field_17857);
		}

		public Particle method_3021(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			EnchantGlyphParticle enchantGlyphParticle = new EnchantGlyphParticle(world, d, e, f, g, h, i);
			enchantGlyphParticle.method_18140(this.field_17803);
			return enchantGlyphParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class NautilusFactory implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17804;

		public NautilusFactory(class_4001 arg) {
			this.field_17804 = arg.register(class_4000.field_17859);
		}

		public Particle method_3020(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			EnchantGlyphParticle enchantGlyphParticle = new EnchantGlyphParticle(world, d, e, f, g, h, i);
			enchantGlyphParticle.method_18140(this.field_17804);
			return enchantGlyphParticle;
		}
	}
}
