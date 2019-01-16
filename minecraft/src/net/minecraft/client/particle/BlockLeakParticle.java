package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BlockLeakParticle extends Particle {
	private final Fluid field_3789;
	private int slowedTimer;

	protected BlockLeakParticle(World world, double d, double e, double f, Fluid fluid) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.velocityX = 0.0;
		this.velocityY = 0.0;
		this.velocityZ = 0.0;
		if (fluid.matches(FluidTags.field_15517)) {
			this.colorRed = 0.0F;
			this.colorGreen = 0.0F;
			this.colorBlue = 1.0F;
		} else {
			this.colorRed = 1.0F;
			this.colorGreen = 0.0F;
			this.colorBlue = 0.0F;
		}

		this.setSpriteIndex(113);
		this.setBoundingBoxSpacing(0.01F, 0.01F);
		this.gravityStrength = 0.06F;
		this.field_3789 = fluid;
		this.slowedTimer = 40;
		this.maxAge = (int)(64.0 / (Math.random() * 0.8 + 0.2));
		this.velocityX = 0.0;
		this.velocityY = 0.0;
		this.velocityZ = 0.0;
	}

	@Override
	public int getColorMultiplier(float f) {
		return this.field_3789.matches(FluidTags.field_15517) ? super.getColorMultiplier(f) : 257;
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.field_3789.matches(FluidTags.field_15517)) {
			this.colorRed = 0.2F;
			this.colorGreen = 0.3F;
			this.colorBlue = 1.0F;
		} else {
			this.colorRed = 1.0F;
			this.colorGreen = 16.0F / (float)(40 - this.slowedTimer + 16);
			this.colorBlue = 4.0F / (float)(40 - this.slowedTimer + 8);
		}

		this.velocityY = this.velocityY - (double)this.gravityStrength;
		if (this.slowedTimer-- > 0) {
			this.velocityX *= 0.02;
			this.velocityY *= 0.02;
			this.velocityZ *= 0.02;
			this.setSpriteIndex(113);
		} else {
			this.setSpriteIndex(112);
		}

		this.move(this.velocityX, this.velocityY, this.velocityZ);
		this.velocityX *= 0.98F;
		this.velocityY *= 0.98F;
		this.velocityZ *= 0.98F;
		if (this.maxAge-- <= 0) {
			this.markDead();
		}

		if (this.onGround) {
			if (this.field_3789.matches(FluidTags.field_15517)) {
				this.markDead();
				this.world.addParticle(ParticleTypes.field_11202, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
			} else {
				this.setSpriteIndex(114);
			}

			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
		}

		BlockPos blockPos = new BlockPos(this.posX, this.posY, this.posZ);
		FluidState fluidState = this.world.getFluidState(blockPos);
		if (fluidState.getFluid() == this.field_3789 && this.posY < (double)((float)blockPos.getY() + fluidState.method_15763(this.world, blockPos))) {
			this.markDead();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class LavaFactory implements ParticleFactory<DefaultParticleType> {
		public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new BlockLeakParticle(world, d, e, f, Fluids.LAVA);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class WaterFactory implements ParticleFactory<DefaultParticleType> {
		public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new BlockLeakParticle(world, d, e, f, Fluids.WATER);
		}
	}
}
