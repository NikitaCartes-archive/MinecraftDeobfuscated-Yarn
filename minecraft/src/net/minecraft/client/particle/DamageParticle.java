package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4000;
import net.minecraft.class_4001;
import net.minecraft.class_4002;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class DamageParticle extends SpriteBillboardParticle {
	private DamageParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.velocityX *= 0.1F;
		this.velocityY *= 0.1F;
		this.velocityZ *= 0.1F;
		this.velocityX += g * 0.4;
		this.velocityY += h * 0.4;
		this.velocityZ += i * 0.4;
		float j = (float)(Math.random() * 0.3F + 0.6F);
		this.colorRed = j;
		this.colorGreen = j;
		this.colorBlue = j;
		this.field_17867 *= 0.75F;
		this.maxAge = Math.max((int)(6.0 / (Math.random() * 0.8 + 0.6)), 1);
		this.collidesWithWorld = false;
		this.update();
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
			this.colorGreen = (float)((double)this.colorGreen * 0.96);
			this.colorBlue = (float)((double)this.colorBlue * 0.9);
			this.velocityX *= 0.7F;
			this.velocityY *= 0.7F;
			this.velocityZ *= 0.7F;
			this.velocityY -= 0.02F;
			if (this.onGround) {
				this.velocityX *= 0.7F;
				this.velocityZ *= 0.7F;
			}
		}
	}

	@Override
	public ParticleTextureSheet getTextureSheet() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Environment(EnvType.CLIENT)
	public static class CritFactory implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17792;

		public CritFactory(class_4001 arg) {
			this.field_17792 = arg.method_18137(class_4000.field_17841);
		}

		public Particle method_3015(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			DamageParticle damageParticle = new DamageParticle(world, d, e, f, g, h, i);
			damageParticle.method_18140(this.field_17792);
			return damageParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class DefaultFactory implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17790;

		public DefaultFactory(class_4001 arg) {
			this.field_17790 = arg.method_18137(class_4000.field_17843);
		}

		public Particle method_3013(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			DamageParticle damageParticle = new DamageParticle(world, d, e, f, g, h + 1.0, i);
			damageParticle.setMaxAge(20);
			damageParticle.method_18140(this.field_17790);
			return damageParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class EnchantedHitFactory implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17791;

		public EnchantedHitFactory(class_4001 arg) {
			this.field_17791 = arg.method_18137(class_4000.field_17842);
		}

		public Particle method_3014(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			DamageParticle damageParticle = new DamageParticle(world, d, e, f, g, h, i);
			damageParticle.colorRed *= 0.3F;
			damageParticle.colorGreen *= 0.8F;
			damageParticle.method_18140(this.field_17791);
			return damageParticle;
		}
	}
}
