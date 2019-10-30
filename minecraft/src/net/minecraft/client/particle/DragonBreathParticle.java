package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class DragonBreathParticle extends SpriteBillboardParticle {
	private boolean field_3792;
	private final SpriteProvider field_17793;

	private DragonBreathParticle(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
		super(world, x, y, z);
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.velocityZ = velocityZ;
		this.colorRed = MathHelper.nextFloat(this.random, 0.7176471F, 0.8745098F);
		this.colorGreen = MathHelper.nextFloat(this.random, 0.0F, 0.0F);
		this.colorBlue = MathHelper.nextFloat(this.random, 0.8235294F, 0.9764706F);
		this.scale *= 0.75F;
		this.maxAge = (int)(20.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
		this.field_3792 = false;
		this.collidesWithWorld = false;
		this.field_17793 = spriteProvider;
		this.setSpriteForAge(spriteProvider);
	}

	@Override
	public void tick() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.setSpriteForAge(this.field_17793);
			if (this.onGround) {
				this.velocityY = 0.0;
				this.field_3792 = true;
			}

			if (this.field_3792) {
				this.velocityY += 0.002;
			}

			this.move(this.velocityX, this.velocityY, this.velocityZ);
			if (this.y == this.prevPosY) {
				this.velocityX *= 1.1;
				this.velocityZ *= 1.1;
			}

			this.velocityX *= 0.96F;
			this.velocityZ *= 0.96F;
			if (this.field_3792) {
				this.velocityY *= 0.96F;
			}
		}
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public float getSize(float tickDelta) {
		return this.scale * MathHelper.clamp(((float)this.age + tickDelta) / (float)this.maxAge * 32.0F, 0.0F, 1.0F);
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider field_17794;

		public Factory(SpriteProvider spriteProvider) {
			this.field_17794 = spriteProvider;
		}

		public Particle method_3019(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new DragonBreathParticle(world, d, e, f, g, h, i, this.field_17794);
		}
	}
}
