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
public class FlameParticle extends SpriteBillboardParticle {
	private FlameParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
		this.velocityX = this.velocityX * 0.01F + g;
		this.velocityY = this.velocityY * 0.01F + h;
		this.velocityZ = this.velocityZ * 0.01F + i;
		this.posX = this.posX + (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
		this.posY = this.posY + (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
		this.posZ = this.posZ + (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
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
	public float method_18132(float f) {
		float g = ((float)this.age + f) / (float)this.maxAge;
		return this.field_17867 * (1.0F - g * g * 0.5F);
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
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
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
		private final class_4002 field_17812;

		public Factory(class_4001 arg) {
			this.field_17812 = arg.method_18137(class_4000.field_17851);
		}

		public Particle method_3036(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			FlameParticle flameParticle = new FlameParticle(world, d, e, f, g, h, i);
			flameParticle.method_18140(this.field_17812);
			return flameParticle;
		}
	}
}
