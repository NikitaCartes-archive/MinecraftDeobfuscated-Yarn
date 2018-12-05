package net.minecraft.client.particle;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.TexturedParticle;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class SpellParticle extends Particle {
	private static final Random RANDOM = new Random();
	private int field_3889 = 128;

	protected SpellParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, 0.5 - RANDOM.nextDouble(), h, 0.5 - RANDOM.nextDouble());
		this.velocityY *= 0.2F;
		if (g == 0.0 && i == 0.0) {
			this.velocityX *= 0.1F;
			this.velocityZ *= 0.1F;
		}

		this.size *= 0.75F;
		this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
		this.collidesWithWorld = false;
	}

	@Override
	public boolean hasAlpha() {
		return true;
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		}

		this.setSpriteIndex(this.field_3889 + 7 - this.age * 8 / this.maxAge);
		this.velocityY += 0.004;
		this.addPos(this.velocityX, this.velocityY, this.velocityZ);
		if (this.posY == this.prevPosY) {
			this.velocityX *= 1.1;
			this.velocityZ *= 1.1;
		}

		this.velocityX *= 0.96F;
		this.velocityY *= 0.96F;
		this.velocityZ *= 0.96F;
		if (this.onGround) {
			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
		}
	}

	public void method_3095(int i) {
		this.field_3889 = i;
	}

	@Environment(EnvType.CLIENT)
	public static class FactoryInstant implements FactoryParticle<TexturedParticle> {
		public Particle createParticle(TexturedParticle texturedParticle, World world, double d, double e, double f, double g, double h, double i) {
			Particle particle = new SpellParticle(world, d, e, f, g, h, i);
			((SpellParticle)particle).method_3095(144);
			return particle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class FactoryMob implements FactoryParticle<TexturedParticle> {
		public Particle createParticle(TexturedParticle texturedParticle, World world, double d, double e, double f, double g, double h, double i) {
			Particle particle = new SpellParticle(world, d, e, f, g, h, i);
			particle.setColor((float)g, (float)h, (float)i);
			return particle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class FactoryMobAmbient implements FactoryParticle<TexturedParticle> {
		public Particle createParticle(TexturedParticle texturedParticle, World world, double d, double e, double f, double g, double h, double i) {
			Particle particle = new SpellParticle(world, d, e, f, g, h, i);
			particle.setColorAlpha(0.15F);
			particle.setColor((float)g, (float)h, (float)i);
			return particle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class FactoryWitch implements FactoryParticle<TexturedParticle> {
		public Particle createParticle(TexturedParticle texturedParticle, World world, double d, double e, double f, double g, double h, double i) {
			Particle particle = new SpellParticle(world, d, e, f, g, h, i);
			((SpellParticle)particle).method_3095(144);
			float j = world.random.nextFloat() * 0.5F + 0.35F;
			particle.setColor(1.0F * j, 0.0F * j, 1.0F * j);
			return particle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_715 implements FactoryParticle<TexturedParticle> {
		public Particle createParticle(TexturedParticle texturedParticle, World world, double d, double e, double f, double g, double h, double i) {
			return new SpellParticle(world, d, e, f, g, h, i);
		}
	}
}
