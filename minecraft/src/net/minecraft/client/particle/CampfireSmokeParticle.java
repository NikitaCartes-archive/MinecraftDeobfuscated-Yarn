package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class CampfireSmokeParticle extends SpriteBillboardParticle {
	private CampfireSmokeParticle(World world, double d, double e, double f, double g, double h, double i, boolean bl) {
		super(world, d, e, f);
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
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.age++ < this.maxAge && !(this.colorAlpha <= 0.0F)) {
			this.velocityX = this.velocityX + (double)(this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean() ? 1 : -1));
			this.velocityZ = this.velocityZ + (double)(this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean() ? 1 : -1));
			this.velocityY = this.velocityY - (double)this.gravityStrength;
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			if (this.age >= this.maxAge - 60 && this.colorAlpha > 0.01F) {
				this.colorAlpha -= 0.015F;
			}
		} else {
			this.markDead();
		}
	}

	@Override
	public ParticleTextureSheet getTextureSheet() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Environment(EnvType.CLIENT)
	public static class class_3938 implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider field_18290;

		public class_3938(SpriteProvider spriteProvider) {
			this.field_18290 = spriteProvider;
		}

		public Particle method_17579(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			CampfireSmokeParticle campfireSmokeParticle = new CampfireSmokeParticle(world, d, e, f, g, h, i, false);
			campfireSmokeParticle.setColorAlpha(0.9F);
			campfireSmokeParticle.method_18140(this.field_18290);
			return campfireSmokeParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_3995 implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider field_17789;

		public class_3995(SpriteProvider spriteProvider) {
			this.field_17789 = spriteProvider;
		}

		public Particle method_18820(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			CampfireSmokeParticle campfireSmokeParticle = new CampfireSmokeParticle(world, d, e, f, g, h, i, true);
			campfireSmokeParticle.setColorAlpha(0.95F);
			campfireSmokeParticle.method_18140(this.field_17789);
			return campfireSmokeParticle;
		}
	}
}
