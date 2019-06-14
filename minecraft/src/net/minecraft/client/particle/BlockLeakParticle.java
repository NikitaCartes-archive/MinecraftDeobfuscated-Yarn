package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BlockLeakParticle extends SpriteBillboardParticle {
	private final Fluid fluid;

	private BlockLeakParticle(World world, double d, double e, double f, Fluid fluid) {
		super(world, d, e, f);
		this.setBoundingBoxSpacing(0.01F, 0.01F);
		this.gravityStrength = 0.06F;
		this.fluid = fluid;
	}

	@Override
	public ParticleTextureSheet method_18122() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public int getColorMultiplier(float f) {
		return this.fluid.matches(FluidTags.field_15518) ? 240 : super.getColorMultiplier(f);
	}

	@Override
	public void tick() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		this.updateAge();
		if (!this.dead) {
			this.velocityY = this.velocityY - (double)this.gravityStrength;
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			this.updateVelocity();
			if (!this.dead) {
				this.velocityX *= 0.98F;
				this.velocityY *= 0.98F;
				this.velocityZ *= 0.98F;
				BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
				FluidState fluidState = this.world.method_8316(blockPos);
				if (fluidState.getFluid() == this.fluid && this.y < (double)((float)blockPos.getY() + fluidState.getHeight(this.world, blockPos))) {
					this.markDead();
				}
			}
		}
	}

	protected void updateAge() {
		if (this.maxAge-- <= 0) {
			this.markDead();
		}
	}

	protected void updateVelocity() {
	}

	@Environment(EnvType.CLIENT)
	public static class DrippingLavaFactory implements ParticleFactory<DefaultParticleType> {
		protected final SpriteProvider field_18295;

		public DrippingLavaFactory(SpriteProvider spriteProvider) {
			this.field_18295 = spriteProvider;
		}

		public Particle method_3017(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle.DrippingLavaParticle drippingLavaParticle = new BlockLeakParticle.DrippingLavaParticle(
				world, d, e, f, Fluids.LAVA, ParticleTypes.field_18304
			);
			drippingLavaParticle.setSprite(this.field_18295);
			return drippingLavaParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	static class DrippingLavaParticle extends BlockLeakParticle.DrippingParticle {
		private DrippingLavaParticle(World world, double d, double e, double f, Fluid fluid, ParticleEffect particleEffect) {
			super(world, d, e, f, fluid, particleEffect);
		}

		@Override
		protected void updateAge() {
			this.colorRed = 1.0F;
			this.colorGreen = 16.0F / (float)(40 - this.maxAge + 16);
			this.colorBlue = 4.0F / (float)(40 - this.maxAge + 8);
			super.updateAge();
		}
	}

	@Environment(EnvType.CLIENT)
	static class DrippingParticle extends BlockLeakParticle {
		private final ParticleEffect nextParticle;

		private DrippingParticle(World world, double d, double e, double f, Fluid fluid, ParticleEffect particleEffect) {
			super(world, d, e, f, fluid);
			this.nextParticle = particleEffect;
			this.gravityStrength *= 0.02F;
			this.maxAge = 40;
		}

		@Override
		protected void updateAge() {
			if (this.maxAge-- <= 0) {
				this.markDead();
				this.world.addParticle(this.nextParticle, this.x, this.y, this.z, this.velocityX, this.velocityY, this.velocityZ);
			}
		}

		@Override
		protected void updateVelocity() {
			this.velocityX *= 0.02;
			this.velocityY *= 0.02;
			this.velocityZ *= 0.02;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class DrippingWaterFactory implements ParticleFactory<DefaultParticleType> {
		protected final SpriteProvider field_18298;

		public DrippingWaterFactory(SpriteProvider spriteProvider) {
			this.field_18298 = spriteProvider;
		}

		public Particle method_18825(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle blockLeakParticle = new BlockLeakParticle.DrippingParticle(world, d, e, f, Fluids.WATER, ParticleTypes.field_18306);
			blockLeakParticle.setColor(0.2F, 0.3F, 1.0F);
			blockLeakParticle.setSprite(this.field_18298);
			return blockLeakParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class FallingLavaFactory implements ParticleFactory<DefaultParticleType> {
		protected final SpriteProvider field_18294;

		public FallingLavaFactory(SpriteProvider spriteProvider) {
			this.field_18294 = spriteProvider;
		}

		public Particle method_18823(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle blockLeakParticle = new BlockLeakParticle.FallingParticle(world, d, e, f, Fluids.LAVA, ParticleTypes.field_18305);
			blockLeakParticle.setColor(1.0F, 0.2857143F, 0.083333336F);
			blockLeakParticle.setSprite(this.field_18294);
			return blockLeakParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	static class FallingParticle extends BlockLeakParticle {
		private final ParticleEffect nextParticle;

		private FallingParticle(World world, double d, double e, double f, Fluid fluid, ParticleEffect particleEffect) {
			super(world, d, e, f, fluid);
			this.nextParticle = particleEffect;
			this.maxAge = (int)(64.0 / (Math.random() * 0.8 + 0.2));
		}

		@Override
		protected void updateVelocity() {
			if (this.onGround) {
				this.markDead();
				this.world.addParticle(this.nextParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class FallingWaterFactory implements ParticleFactory<DefaultParticleType> {
		protected final SpriteProvider field_18297;

		public FallingWaterFactory(SpriteProvider spriteProvider) {
			this.field_18297 = spriteProvider;
		}

		public Particle method_3018(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle blockLeakParticle = new BlockLeakParticle.FallingParticle(world, d, e, f, Fluids.WATER, ParticleTypes.field_11202);
			blockLeakParticle.setColor(0.2F, 0.3F, 1.0F);
			blockLeakParticle.setSprite(this.field_18297);
			return blockLeakParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class LandingLavaFactory implements ParticleFactory<DefaultParticleType> {
		protected final SpriteProvider field_18296;

		public LandingLavaFactory(SpriteProvider spriteProvider) {
			this.field_18296 = spriteProvider;
		}

		public Particle method_18824(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle blockLeakParticle = new BlockLeakParticle.LandingParticle(world, d, e, f, Fluids.LAVA);
			blockLeakParticle.setColor(1.0F, 0.2857143F, 0.083333336F);
			blockLeakParticle.setSprite(this.field_18296);
			return blockLeakParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	static class LandingParticle extends BlockLeakParticle {
		private LandingParticle(World world, double d, double e, double f, Fluid fluid) {
			super(world, d, e, f, fluid);
			this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
		}
	}
}
