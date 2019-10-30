package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class SquidInkParticle extends AnimatedParticle {
	private SquidInkParticle(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
		super(world, x, y, z, spriteProvider, 0.0F);
		this.scale = 0.5F;
		this.setColorAlpha(1.0F);
		this.setColor(0.0F, 0.0F, 0.0F);
		this.maxAge = (int)((double)(this.scale * 12.0F) / (Math.random() * 0.8F + 0.2F));
		this.setSpriteForAge(spriteProvider);
		this.collidesWithWorld = false;
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.velocityZ = velocityZ;
		this.setResistance(0.0F);
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
			if (this.age > this.maxAge / 2) {
				this.setColorAlpha(1.0F - ((float)this.age - (float)(this.maxAge / 2)) / (float)this.maxAge);
			}

			this.move(this.velocityX, this.velocityY, this.velocityZ);
			if (this.world.getBlockState(new BlockPos(this.x, this.y, this.z)).isAir()) {
				this.velocityY -= 0.008F;
			}

			this.velocityX *= 0.92F;
			this.velocityY *= 0.92F;
			this.velocityZ *= 0.92F;
			if (this.onGround) {
				this.velocityX *= 0.7F;
				this.velocityZ *= 0.7F;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle method_3105(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new SquidInkParticle(world, d, e, f, g, h, i, this.spriteProvider);
		}
	}
}
