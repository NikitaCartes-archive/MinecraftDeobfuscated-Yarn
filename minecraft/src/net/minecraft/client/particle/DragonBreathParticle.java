package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class DragonBreathParticle extends SpriteBillboardParticle {
	private static final int field_32654 = 11993298;
	private static final int field_32655 = 14614777;
	private static final float field_32648 = 0.7176471F;
	private static final float field_32649 = 0.0F;
	private static final float field_32650 = 0.8235294F;
	private static final float field_32651 = 0.8745098F;
	private static final float field_32652 = 0.0F;
	private static final float field_32653 = 0.9764706F;
	private boolean reachedGround;
	private final SpriteProvider spriteProvider;

	DragonBreathParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
		super(world, x, y, z);
		this.velocityMultiplier = 0.96F;
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.velocityZ = velocityZ;
		this.colorRed = MathHelper.nextFloat(this.random, 0.7176471F, 0.8745098F);
		this.colorGreen = MathHelper.nextFloat(this.random, 0.0F, 0.0F);
		this.colorBlue = MathHelper.nextFloat(this.random, 0.8235294F, 0.9764706F);
		this.scale *= 0.75F;
		this.maxAge = (int)(20.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
		this.reachedGround = false;
		this.collidesWithWorld = false;
		this.spriteProvider = spriteProvider;
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
			this.setSpriteForAge(this.spriteProvider);
			if (this.onGround) {
				this.velocityY = 0.0;
				this.reachedGround = true;
			}

			if (this.reachedGround) {
				this.velocityY += 0.002;
			}

			this.move(this.velocityX, this.velocityY, this.velocityZ);
			if (this.y == this.prevPosY) {
				this.velocityX *= 1.1;
				this.velocityZ *= 1.1;
			}

			this.velocityX = this.velocityX * (double)this.velocityMultiplier;
			this.velocityZ = this.velocityZ * (double)this.velocityMultiplier;
			if (this.reachedGround) {
				this.velocityY = this.velocityY * (double)this.velocityMultiplier;
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
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			return new DragonBreathParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
		}
	}
}
