package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DustParticleParameters;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class RedDustParticle extends SpriteBillboardParticle {
	private final SpriteProvider field_17801;

	private RedDustParticle(
		World world, double d, double e, double f, double g, double h, double i, DustParticleParameters dustParticleParameters, SpriteProvider spriteProvider
	) {
		super(world, d, e, f, g, h, i);
		this.field_17801 = spriteProvider;
		this.velocityX *= 0.1F;
		this.velocityY *= 0.1F;
		this.velocityZ *= 0.1F;
		float j = (float)Math.random() * 0.4F + 0.6F;
		this.colorRed = ((float)(Math.random() * 0.2F) + 0.8F) * dustParticleParameters.getRed() * j;
		this.colorGreen = ((float)(Math.random() * 0.2F) + 0.8F) * dustParticleParameters.getGreen() * j;
		this.colorBlue = ((float)(Math.random() * 0.2F) + 0.8F) * dustParticleParameters.getBlue() * j;
		this.scale = this.scale * 0.75F * dustParticleParameters.getAlpha();
		int k = (int)(8.0 / (Math.random() * 0.8 + 0.2));
		this.maxAge = (int)Math.max((float)k * dustParticleParameters.getAlpha(), 1.0F);
		this.setSpriteForAge(spriteProvider);
	}

	@Override
	public ParticleTextureSheet getTextureSheet() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public float getSize(float f) {
		return this.scale * MathHelper.clamp(((float)this.age + f) / (float)this.maxAge * 32.0F, 0.0F, 1.0F);
	}

	@Override
	public void update() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.setSpriteForAge(this.field_17801);
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			if (this.y == this.prevPosY) {
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
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DustParticleParameters> {
		private final SpriteProvider field_17802;

		public Factory(SpriteProvider spriteProvider) {
			this.field_17802 = spriteProvider;
		}

		public Particle method_3022(DustParticleParameters dustParticleParameters, World world, double d, double e, double f, double g, double h, double i) {
			return new RedDustParticle(world, d, e, f, g, h, i, dustParticleParameters, this.field_17802);
		}
	}
}
