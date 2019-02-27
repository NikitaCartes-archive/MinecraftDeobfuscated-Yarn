package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4002;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BlockLeakParticle extends SpriteBillboardParticle {
	private final Fluid field_3789;

	private BlockLeakParticle(World world, double d, double e, double f, Fluid fluid) {
		super(world, d, e, f);
		this.setBoundingBoxSpacing(0.01F, 0.01F);
		this.gravityStrength = 0.06F;
		this.field_3789 = fluid;
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
		this.method_18821();
		if (!this.dead) {
			this.velocityY = this.velocityY - (double)this.gravityStrength;
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			this.method_18822();
			if (!this.dead) {
				this.velocityX *= 0.98F;
				this.velocityY *= 0.98F;
				this.velocityZ *= 0.98F;
				BlockPos blockPos = new BlockPos(this.posX, this.posY, this.posZ);
				FluidState fluidState = this.world.getFluidState(blockPos);
				if (fluidState.getFluid() == this.field_3789 && this.posY < (double)((float)blockPos.getY() + fluidState.getHeight(this.world, blockPos))) {
					this.markDead();
				}
			}
		}
	}

	protected void method_18821() {
		if (this.maxAge-- <= 0) {
			this.markDead();
		}
	}

	protected void method_18822() {
	}

	@Environment(EnvType.CLIENT)
	public static class LavaFactory implements ParticleFactory<DefaultParticleType> {
		protected final class_4002 field_18295;

		public LavaFactory(class_4002 arg) {
			this.field_18295 = arg;
		}

		public Particle method_3017(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle.class_4082 lv = new BlockLeakParticle.class_4082(world, d, e, f, Fluids.LAVA, ParticleTypes.field_18304);
			lv.method_18140(this.field_18295);
			return lv;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class WaterFactory implements ParticleFactory<DefaultParticleType> {
		protected final class_4002 field_18297;

		public WaterFactory(class_4002 arg) {
			this.field_18297 = arg;
		}

		public Particle method_3018(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle blockLeakParticle = new BlockLeakParticle.class_4083(world, d, e, f, Fluids.WATER, ParticleTypes.field_11202);
			blockLeakParticle.setColor(0.2F, 0.3F, 1.0F);
			blockLeakParticle.method_18140(this.field_18297);
			return blockLeakParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_4082 extends BlockLeakParticle.class_4084 {
		private class_4082(World world, double d, double e, double f, Fluid fluid, ParticleParameters particleParameters) {
			super(world, d, e, f, fluid, particleParameters);
		}

		@Override
		protected void method_18821() {
			this.colorRed = 1.0F;
			this.colorGreen = 16.0F / (float)(40 - this.maxAge + 16);
			this.colorBlue = 4.0F / (float)(40 - this.maxAge + 8);
			super.method_18821();
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_4083 extends BlockLeakParticle {
		private final ParticleParameters field_18292;

		private class_4083(World world, double d, double e, double f, Fluid fluid, ParticleParameters particleParameters) {
			super(world, d, e, f, fluid);
			this.field_18292 = particleParameters;
			this.maxAge = (int)(64.0 / (Math.random() * 0.8 + 0.2));
		}

		@Override
		protected void method_18822() {
			if (this.onGround) {
				this.markDead();
				this.world.addParticle(this.field_18292, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_4084 extends BlockLeakParticle {
		private final ParticleParameters field_18293;

		private class_4084(World world, double d, double e, double f, Fluid fluid, ParticleParameters particleParameters) {
			super(world, d, e, f, fluid);
			this.field_18293 = particleParameters;
			this.gravityStrength *= 0.02F;
			this.maxAge = 40;
		}

		@Override
		protected void method_18821() {
			if (this.maxAge-- <= 0) {
				this.markDead();
				this.world.addParticle(this.field_18293, this.posX, this.posY, this.posZ, this.velocityX, this.velocityY, this.velocityZ);
			}
		}

		@Override
		protected void method_18822() {
			this.velocityX *= 0.02;
			this.velocityY *= 0.02;
			this.velocityZ *= 0.02;
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_4085 extends BlockLeakParticle {
		private class_4085(World world, double d, double e, double f, Fluid fluid) {
			super(world, d, e, f, fluid);
			this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4086 implements ParticleFactory<DefaultParticleType> {
		protected final class_4002 field_18294;

		public class_4086(class_4002 arg) {
			this.field_18294 = arg;
		}

		public Particle method_18823(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle blockLeakParticle = new BlockLeakParticle.class_4083(world, d, e, f, Fluids.LAVA, ParticleTypes.field_18305);
			blockLeakParticle.setColor(1.0F, 0.2857143F, 0.083333336F);
			blockLeakParticle.method_18140(this.field_18294);
			return blockLeakParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4087 implements ParticleFactory<DefaultParticleType> {
		protected final class_4002 field_18296;

		public class_4087(class_4002 arg) {
			this.field_18296 = arg;
		}

		public Particle method_18824(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle blockLeakParticle = new BlockLeakParticle.class_4085(world, d, e, f, Fluids.LAVA);
			blockLeakParticle.setColor(1.0F, 0.2857143F, 0.083333336F);
			blockLeakParticle.method_18140(this.field_18296);
			return blockLeakParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4088 implements ParticleFactory<DefaultParticleType> {
		protected final class_4002 field_18298;

		public class_4088(class_4002 arg) {
			this.field_18298 = arg;
		}

		public Particle method_18825(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle blockLeakParticle = new BlockLeakParticle.class_4084(world, d, e, f, Fluids.WATER, ParticleTypes.field_18306);
			blockLeakParticle.setColor(0.2F, 0.3F, 1.0F);
			blockLeakParticle.method_18140(this.field_18298);
			return blockLeakParticle;
		}
	}
}
