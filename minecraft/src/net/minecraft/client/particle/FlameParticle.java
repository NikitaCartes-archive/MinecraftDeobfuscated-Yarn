package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class FlameParticle extends SpriteBillboardParticle {
	private FlameParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
		this.velocityX = this.velocityX * 0.01F + g;
		this.velocityY = this.velocityY * 0.01F + h;
		this.velocityZ = this.velocityZ * 0.01F + i;
		this.x = this.x + (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
		this.y = this.y + (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
		this.z = this.z + (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
		this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2)) + 4;
	}

	@Override
	public ParticleTextureSheet getTextureSheet() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public void move(double d, double e, double f) {
		this.setBoundingBox(this.getBoundingBox().offset(d, e, f));
		this.repositionFromBoundingBox();
	}

	@Override
	public float getSize(float f) {
		float g = ((float)this.age + f) / (float)this.maxAge;
		return this.scale * (1.0F - g * g * 0.5F);
	}

	@Override
	public int getColorMultiplier(float f) {
		float g = ((float)this.age + f) / (float)this.maxAge;
		g = MathHelper.clamp(g, 0.0F, 1.0F);
		int i = super.getColorMultiplier(f);
		int j = i & 0xFF;
		int k = i >> 16 & 0xFF;
		j += (int)(g * 15.0F * 16.0F);
		if (j > 240) {
			j = 240;
		}

		return j | k << 16;
	}

	@Override
	public void update() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.move(this.velocityX, this.velocityY, this.velocityZ);
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
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider field_17812;

		public Factory(SpriteProvider spriteProvider) {
			this.field_17812 = spriteProvider;
		}

		public Particle method_3036(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			FlameParticle flameParticle = new FlameParticle(world, d, e, f, g, h, i);
			flameParticle.setSprite(this.field_17812);
			return flameParticle;
		}
	}
}
