package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public int getColorMultiplier(float f) {
		return this.fluid.matches(FluidTags.LAVA) ? 240 : super.getColorMultiplier(f);
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
				FluidState fluidState = this.world.getFluidState(blockPos);
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
		protected final SpriteProvider spriteProvider;

		public DrippingLavaFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle method_3017(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle.DrippingLavaParticle drippingLavaParticle = new BlockLeakParticle.DrippingLavaParticle(
				world, d, e, f, Fluids.LAVA, ParticleTypes.FALLING_LAVA
			);
			drippingLavaParticle.setSprite(this.spriteProvider);
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
		protected final SpriteProvider spriteProvider;

		public DrippingWaterFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle method_18825(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle blockLeakParticle = new BlockLeakParticle.DrippingParticle(world, d, e, f, Fluids.WATER, ParticleTypes.FALLING_WATER);
			blockLeakParticle.setColor(0.2F, 0.3F, 1.0F);
			blockLeakParticle.setSprite(this.spriteProvider);
			return blockLeakParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class FallingLavaFactory implements ParticleFactory<DefaultParticleType> {
		protected final SpriteProvider spriteProvider;

		public FallingLavaFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle method_18823(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle blockLeakParticle = new BlockLeakParticle.FallingParticle(world, d, e, f, Fluids.LAVA, ParticleTypes.LANDING_LAVA);
			blockLeakParticle.setColor(1.0F, 0.2857143F, 0.083333336F);
			blockLeakParticle.setSprite(this.spriteProvider);
			return blockLeakParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	static class FallingParticle extends BlockLeakParticle.class_4497 {
		protected final ParticleEffect nextParticle;

		private FallingParticle(World world, double d, double e, double f, Fluid fluid, ParticleEffect particleEffect) {
			super(world, d, e, f, fluid);
			this.nextParticle = particleEffect;
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
		protected final SpriteProvider spriteProvider;

		public FallingWaterFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle method_3018(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle blockLeakParticle = new BlockLeakParticle.FallingParticle(world, d, e, f, Fluids.WATER, ParticleTypes.SPLASH);
			blockLeakParticle.setColor(0.2F, 0.3F, 1.0F);
			blockLeakParticle.setSprite(this.spriteProvider);
			return blockLeakParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class LandingLavaFactory implements ParticleFactory<DefaultParticleType> {
		protected final SpriteProvider spriteProvider;

		public LandingLavaFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle method_18824(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle blockLeakParticle = new BlockLeakParticle.LandingParticle(world, d, e, f, Fluids.LAVA);
			blockLeakParticle.setColor(1.0F, 0.2857143F, 0.083333336F);
			blockLeakParticle.setSprite(this.spriteProvider);
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

	@Environment(EnvType.CLIENT)
	static class class_4497 extends BlockLeakParticle {
		private class_4497(World world, double d, double e, double f, Fluid fluid) {
			super(world, d, e, f, fluid);
			this.maxAge = (int)(64.0 / (Math.random() * 0.8 + 0.2));
		}

		@Override
		protected void updateVelocity() {
			if (this.onGround) {
				this.markDead();
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_4498 extends BlockLeakParticle.FallingParticle {
		private class_4498(World world, double d, double e, double f, Fluid fluid, ParticleEffect particleEffect) {
			super(world, d, e, f, fluid, particleEffect);
		}

		@Override
		protected void updateVelocity() {
			if (this.onGround) {
				this.markDead();
				this.world.addParticle(this.nextParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
				if (this.world.random.nextFloat() < 0.3F) {
					this.world.playSound(this.x + 0.5, this.y, this.z + 0.5, SoundEvents.BLOCK_BEEHIVE_DRIP, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4499 implements ParticleFactory<DefaultParticleType> {
		protected final SpriteProvider field_20515;

		public class_4499(SpriteProvider spriteProvider) {
			this.field_20515 = spriteProvider;
		}

		public Particle method_22115(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle blockLeakParticle = new BlockLeakParticle.class_4498(world, d, e, f, Fluids.EMPTY, ParticleTypes.LANDING_HONEY);
			blockLeakParticle.gravityStrength = 0.01F;
			blockLeakParticle.setColor(0.582F, 0.448F, 0.082F);
			blockLeakParticle.setSprite(this.field_20515);
			return blockLeakParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4500 implements ParticleFactory<DefaultParticleType> {
		protected final SpriteProvider field_20516;

		public class_4500(SpriteProvider spriteProvider) {
			this.field_20516 = spriteProvider;
		}

		public Particle method_22116(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle.DrippingParticle drippingParticle = new BlockLeakParticle.DrippingParticle(world, d, e, f, Fluids.EMPTY, ParticleTypes.FALLING_HONEY);
			drippingParticle.gravityStrength *= 0.01F;
			drippingParticle.maxAge = 100;
			drippingParticle.setColor(0.622F, 0.508F, 0.082F);
			drippingParticle.setSprite(this.field_20516);
			return drippingParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4501 implements ParticleFactory<DefaultParticleType> {
		protected final SpriteProvider field_20517;

		public class_4501(SpriteProvider spriteProvider) {
			this.field_20517 = spriteProvider;
		}

		public Particle method_22117(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle blockLeakParticle = new BlockLeakParticle.LandingParticle(world, d, e, f, Fluids.EMPTY);
			blockLeakParticle.maxAge = (int)(128.0 / (Math.random() * 0.8 + 0.2));
			blockLeakParticle.setColor(0.522F, 0.408F, 0.082F);
			blockLeakParticle.setSprite(this.field_20517);
			return blockLeakParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4502 implements ParticleFactory<DefaultParticleType> {
		protected final SpriteProvider field_20518;

		public class_4502(SpriteProvider spriteProvider) {
			this.field_20518 = spriteProvider;
		}

		public Particle method_22118(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle blockLeakParticle = new BlockLeakParticle.class_4497(world, d, e, f, Fluids.EMPTY);
			blockLeakParticle.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
			blockLeakParticle.gravityStrength = 0.007F;
			blockLeakParticle.setColor(0.92F, 0.782F, 0.72F);
			blockLeakParticle.setSprite(this.field_20518);
			return blockLeakParticle;
		}
	}
}
