package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3999;
import net.minecraft.class_4000;
import net.minecraft.class_4001;
import net.minecraft.class_4002;
import net.minecraft.class_4003;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class EmotionParticle extends class_4003 {
	private EmotionParticle(World world, double d, double e, double f) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.velocityX *= 0.01F;
		this.velocityY *= 0.01F;
		this.velocityZ *= 0.01F;
		this.velocityY += 0.1;
		this.field_17867 *= 1.5F;
		this.maxAge = 16;
		this.collidesWithWorld = false;
	}

	@Override
	public class_3999 method_18122() {
		return class_3999.field_17828;
	}

	@Override
	public float method_18132(float f) {
		return this.field_17867 * MathHelper.clamp(((float)this.age + f) / (float)this.maxAge * 32.0F, 0.0F, 1.0F);
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
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
	}

	@Environment(EnvType.CLIENT)
	public static class AngryVillagerFactory implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17813;

		public AngryVillagerFactory(class_4001 arg) {
			this.field_17813 = arg.method_18137(class_4000.field_17836);
		}

		public Particle method_3034(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			EmotionParticle emotionParticle = new EmotionParticle(world, d, e + 0.5, f);
			emotionParticle.method_18140(this.field_17813);
			emotionParticle.setColor(1.0F, 1.0F, 1.0F);
			return emotionParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class HeartFactory implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17814;

		public HeartFactory(class_4001 arg) {
			this.field_17814 = arg.method_18137(class_4000.field_17855);
		}

		public Particle method_3035(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			EmotionParticle emotionParticle = new EmotionParticle(world, d, e, f);
			emotionParticle.method_18140(this.field_17814);
			return emotionParticle;
		}
	}
}
