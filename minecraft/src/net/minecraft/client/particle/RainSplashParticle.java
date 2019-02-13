package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3999;
import net.minecraft.class_4000;
import net.minecraft.class_4001;
import net.minecraft.class_4002;
import net.minecraft.class_4003;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class RainSplashParticle extends class_4003 {
	protected RainSplashParticle(World world, double d, double e, double f) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.velocityX *= 0.3F;
		this.velocityY = Math.random() * 0.2F + 0.1F;
		this.velocityZ *= 0.3F;
		this.setBoundingBoxSpacing(0.01F, 0.01F);
		this.gravityStrength = 0.06F;
		this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
	}

	@Override
	public class_3999 method_18122() {
		return class_3999.field_17828;
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.maxAge-- <= 0) {
			this.markDead();
		} else {
			this.velocityY = this.velocityY - (double)this.gravityStrength;
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= 0.98F;
			this.velocityY *= 0.98F;
			this.velocityZ *= 0.98F;
			if (this.onGround) {
				if (Math.random() < 0.5) {
					this.markDead();
				}

				this.velocityX *= 0.7F;
				this.velocityZ *= 0.7F;
			}

			BlockPos blockPos = new BlockPos(this.posX, this.posY, this.posZ);
			double d = Math.max(
				this.world
					.getBlockState(blockPos)
					.getCollisionShape(this.world, blockPos)
					.method_1102(Direction.Axis.Y, this.posX - (double)blockPos.getX(), this.posZ - (double)blockPos.getZ()),
				(double)this.world.getFluidState(blockPos).getHeight(this.world, blockPos)
			);
			if (d > 0.0 && this.posY < (double)blockPos.getY() + d) {
				this.markDead();
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17891;

		public Factory(class_4001 arg) {
			this.field_17891 = arg.register(class_4000.field_17834);
		}

		public Particle method_3116(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			RainSplashParticle rainSplashParticle = new RainSplashParticle(world, d, e, f);
			rainSplashParticle.method_18140(this.field_17891);
			return rainSplashParticle;
		}
	}
}
