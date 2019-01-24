package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class SquidInkParticle extends AnimatedParticle {
	protected SquidInkParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, 0, 8, 0.0F);
		this.size = 5.0F;
		this.setColorAlpha(1.0F);
		this.setColor(0.0F, 0.0F, 0.0F);
		this.setSpriteIndex(0);
		this.maxAge = (int)((double)(this.size * 12.0F) / (Math.random() * 0.8F + 0.2F));
		this.collidesWithWorld = false;
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
		this.method_3091(0.0F);
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		}

		if (this.age > this.maxAge / 2) {
			this.setColorAlpha(1.0F - ((float)this.age - (float)(this.maxAge / 2)) / (float)this.maxAge);
		}

		this.setSpriteIndex(this.textureId + this.frameCount - 1 - this.age * this.frameCount / this.maxAge);
		this.move(this.velocityX, this.velocityY, this.velocityZ);
		if (this.world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ)).isAir()) {
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

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		public Particle method_3105(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new SquidInkParticle(world, d, e, f, g, h, i);
		}
	}
}
