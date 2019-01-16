package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class EmotionParticle extends Particle {
	private final float field_3811;

	protected EmotionParticle(World world, double d, double e, double f, double g, double h, double i) {
		this(world, d, e, f, g, h, i, 2.0F);
	}

	protected EmotionParticle(World world, double d, double e, double f, double g, double h, double i, float j) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.velocityX *= 0.01F;
		this.velocityY *= 0.01F;
		this.velocityZ *= 0.01F;
		this.velocityY += 0.1;
		this.size *= 0.75F;
		this.size *= j;
		this.field_3811 = this.size;
		this.maxAge = 16;
		this.setSpriteIndex(80);
		this.collidesWithWorld = false;
	}

	@Override
	public void buildGeometry(BufferBuilder bufferBuilder, Entity entity, float f, float g, float h, float i, float j, float k) {
		float l = ((float)this.age + f) / (float)this.maxAge * 32.0F;
		l = MathHelper.clamp(l, 0.0F, 1.0F);
		this.size = this.field_3811 * l;
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

		this.move(this.velocityX, this.velocityY, this.velocityZ);
		if (this.posY == this.prevPosY) {
			this.velocityX *= 1.1;
			this.velocityZ *= 1.1;
		}

		this.velocityX *= 0.86F;
		this.velocityY *= 0.86F;
		this.velocityZ *= 0.86F;
		if (this.onGround) {
			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class AngryVillagerFactory implements ParticleFactory<DefaultParticleType> {
		public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			Particle particle = new EmotionParticle(world, d, e + 0.5, f, g, h, i);
			particle.setSpriteIndex(81);
			particle.setColor(1.0F, 1.0F, 1.0F);
			return particle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class HeartFactory implements ParticleFactory<DefaultParticleType> {
		public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new EmotionParticle(world, d, e, f, g, h, i);
		}
	}
}
