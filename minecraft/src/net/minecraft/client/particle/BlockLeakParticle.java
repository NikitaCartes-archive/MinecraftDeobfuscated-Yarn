package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4000;
import net.minecraft.class_4001;
import net.minecraft.class_4002;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BlockLeakParticle extends SpriteBillboardParticle {
	private final Fluid field_3789;
	private int slowedTimer;
	private final class_4002 field_17795;
	private final class_4002 field_17796;
	private final class_4002 field_17797;

	private BlockLeakParticle(World world, double d, double e, double f, Fluid fluid, class_4002 arg, class_4002 arg2, class_4002 arg3) {
		super(world, d, e, f);
		this.field_17795 = arg;
		this.field_17796 = arg2;
		this.field_17797 = arg3;
		if (fluid.matches(FluidTags.field_15517)) {
			this.colorRed = 0.0F;
			this.colorGreen = 0.0F;
			this.colorBlue = 1.0F;
		} else {
			this.colorRed = 1.0F;
			this.colorGreen = 0.0F;
			this.colorBlue = 0.0F;
		}

		this.setSprite(arg2.getSprite(this.random));
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
	public ParticleTextureSheet getTextureSheet() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public int getColorMultiplier(float f) {
		return this.field_3789.matches(FluidTags.field_15518) ? 240 : super.getColorMultiplier(f);
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.maxAge-- <= 0) {
			this.markDead();
		} else {
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
				this.setSprite(this.field_17796.getSprite(this.random));
			} else {
				this.setSprite(this.field_17795.getSprite(this.random));
			}

			this.move(this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= 0.98F;
			this.velocityY *= 0.98F;
			this.velocityZ *= 0.98F;
			if (this.onGround) {
				if (this.field_3789.matches(FluidTags.field_15517)) {
					this.markDead();
					this.world.addParticle(ParticleTypes.field_11202, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
				} else {
					this.setSprite(this.field_17797.getSprite(this.random));
				}

				this.velocityX *= 0.7F;
				this.velocityZ *= 0.7F;
			}

			BlockPos blockPos = new BlockPos(this.posX, this.posY, this.posZ);
			FluidState fluidState = this.world.getFluidState(blockPos);
			if (fluidState.getFluid() == this.field_3789 && this.posY < (double)((float)blockPos.getY() + fluidState.getHeight(this.world, blockPos))) {
				this.markDead();
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class LavaFactory extends BlockLeakParticle.class_3996 {
		public LavaFactory(class_4001 arg) {
			super(arg);
		}

		public Particle method_3017(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new BlockLeakParticle(world, d, e, f, Fluids.LAVA, this.field_17798, this.field_17799, this.field_17800);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class WaterFactory extends BlockLeakParticle.class_3996 {
		public WaterFactory(class_4001 arg) {
			super(arg);
		}

		public Particle method_3018(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new BlockLeakParticle(world, d, e, f, Fluids.WATER, this.field_17798, this.field_17799, this.field_17800);
		}
	}

	@Environment(EnvType.CLIENT)
	abstract static class class_3996 implements ParticleFactory<DefaultParticleType> {
		protected final class_4002 field_17798;
		protected final class_4002 field_17799;
		protected final class_4002 field_17800;

		public class_3996(class_4001 arg) {
			this.field_17798 = arg.method_18137(class_4000.field_17845);
			this.field_17799 = arg.method_18137(class_4000.field_17846);
			this.field_17800 = arg.method_18137(class_4000.field_17847);
		}
	}
}
