package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
		this.scale *= 0.75F;
		this.maxAge = Math.max((int)(6.0 / (Math.random() * 0.8 + 0.6)), 1);
		this.collidesWithWorld = false;
		this.tick();
	}

	@Override
	public float getSize(float f) {
		return this.scale * MathHelper.clamp(((float)this.age + f) / (float)this.maxAge * 32.0F, 0.0F, 1.0F);
	}

	@Override
	public void tick() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
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
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Environment(EnvType.CLIENT)
	public static class DefaultFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider field_17790;

		public DefaultFactory(SpriteProvider spriteProvider) {
			this.field_17790 = spriteProvider;
		}

		public Particle method_3013(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			DamageParticle damageParticle = new DamageParticle(world, d, e, f, g, h + 1.0, i);
			damageParticle.setMaxAge(20);
			damageParticle.setSprite(this.field_17790);
			return damageParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class EnchantedHitFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider field_17791;

		public EnchantedHitFactory(SpriteProvider spriteProvider) {
			this.field_17791 = spriteProvider;
		}

		public Particle method_3014(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			DamageParticle damageParticle = new DamageParticle(world, d, e, f, g, h, i);
			damageParticle.colorRed *= 0.3F;
			damageParticle.colorGreen *= 0.8F;
			damageParticle.setSprite(this.field_17791);
			return damageParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_3939 implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider field_18291;

		public class_3939(SpriteProvider spriteProvider) {
			this.field_18291 = spriteProvider;
		}

		public Particle method_17580(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			DamageParticle damageParticle = new DamageParticle(world, d, e, f, g, h, i);
			damageParticle.setSprite(this.field_18291);
			return damageParticle;
		}
	}
}
