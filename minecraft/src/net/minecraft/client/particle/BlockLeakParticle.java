package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class BlockLeakParticle extends SpriteBillboardParticle {
	private final Fluid fluid;
	protected boolean obsidianTear;

	BlockLeakParticle(ClientWorld world, double x, double y, double z, Fluid fluid) {
		super(world, x, y, z);
		this.setBoundingBoxSpacing(0.01F, 0.01F);
		this.gravityStrength = 0.06F;
		this.fluid = fluid;
	}

	protected Fluid getFluid() {
		return this.fluid;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public int getBrightness(float tint) {
		return this.obsidianTear ? 240 : super.getBrightness(tint);
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
	static class ContinuousFalling extends BlockLeakParticle.Falling {
		protected final ParticleEffect nextParticle;

		ContinuousFalling(ClientWorld world, double x, double y, double z, Fluid fluid, ParticleEffect nextParticle) {
			super(world, x, y, z, fluid);
			this.nextParticle = nextParticle;
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
	static class Dripping extends BlockLeakParticle {
		private final ParticleEffect nextParticle;

		Dripping(ClientWorld world, double x, double y, double z, Fluid fluid, ParticleEffect nextParticle) {
			super(world, x, y, z, fluid);
			this.nextParticle = nextParticle;
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
	public static class DrippingHoneyFactory implements ParticleFactory<DefaultParticleType> {
		protected final SpriteProvider spriteProvider;

		public DrippingHoneyFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle.Dripping dripping = new BlockLeakParticle.Dripping(clientWorld, d, e, f, Fluids.EMPTY, ParticleTypes.FALLING_HONEY);
			dripping.gravityStrength *= 0.01F;
			dripping.maxAge = 100;
			dripping.setColor(0.622F, 0.508F, 0.082F);
			dripping.setSprite(this.spriteProvider);
			return dripping;
		}
	}

	@Environment(EnvType.CLIENT)
	static class DrippingLava extends BlockLeakParticle.Dripping {
		DrippingLava(ClientWorld clientWorld, double d, double e, double f, Fluid fluid, ParticleEffect particleEffect) {
			super(clientWorld, d, e, f, fluid, particleEffect);
		}

		@Override
		protected void updateAge() {
			this.red = 1.0F;
			this.green = 16.0F / (float)(40 - this.maxAge + 16);
			this.blue = 4.0F / (float)(40 - this.maxAge + 8);
			super.updateAge();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class DrippingLavaFactory implements ParticleFactory<DefaultParticleType> {
		protected final SpriteProvider spriteProvider;

		public DrippingLavaFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle.DrippingLava drippingLava = new BlockLeakParticle.DrippingLava(clientWorld, d, e, f, Fluids.LAVA, ParticleTypes.FALLING_LAVA);
			drippingLava.setSprite(this.spriteProvider);
			return drippingLava;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class DrippingObsidianTearFactory implements ParticleFactory<DefaultParticleType> {
		protected final SpriteProvider spriteProvider;

		public DrippingObsidianTearFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle.Dripping dripping = new BlockLeakParticle.Dripping(clientWorld, d, e, f, Fluids.EMPTY, ParticleTypes.FALLING_OBSIDIAN_TEAR);
			dripping.obsidianTear = true;
			dripping.gravityStrength *= 0.01F;
			dripping.maxAge = 100;
			dripping.setColor(0.51171875F, 0.03125F, 0.890625F);
			dripping.setSprite(this.spriteProvider);
			return dripping;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class DrippingWaterFactory implements ParticleFactory<DefaultParticleType> {
		protected final SpriteProvider spriteProvider;

		public DrippingWaterFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle blockLeakParticle = new BlockLeakParticle.Dripping(clientWorld, d, e, f, Fluids.WATER, ParticleTypes.FALLING_WATER);
			blockLeakParticle.setColor(0.2F, 0.3F, 1.0F);
			blockLeakParticle.setSprite(this.spriteProvider);
			return blockLeakParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	static class DripstoneLavaDrip extends BlockLeakParticle.ContinuousFalling {
		DripstoneLavaDrip(ClientWorld clientWorld, double d, double e, double f, Fluid fluid, ParticleEffect particleEffect) {
			super(clientWorld, d, e, f, fluid, particleEffect);
		}

		@Override
		protected void updateVelocity() {
			if (this.onGround) {
				this.markDead();
				this.world.addParticle(this.nextParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
				SoundEvent soundEvent = this.getFluid() == Fluids.LAVA ? SoundEvents.BLOCK_POINTED_DRIPSTONE_DRIP_LAVA : SoundEvents.BLOCK_POINTED_DRIPSTONE_DRIP_WATER;
				float f = MathHelper.nextBetween(this.random, 0.3F, 1.0F);
				this.world.playSound(this.x, this.y, this.z, soundEvent, SoundCategory.BLOCKS, f, 1.0F, false);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class DripstoneLavaSplashFactory implements ParticleFactory<DefaultParticleType> {
		protected final SpriteProvider spriteProvider;

		public DripstoneLavaSplashFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle blockLeakParticle = new BlockLeakParticle.DripstoneLavaDrip(clientWorld, d, e, f, Fluids.WATER, ParticleTypes.SPLASH);
			blockLeakParticle.setColor(0.2F, 0.3F, 1.0F);
			blockLeakParticle.setSprite(this.spriteProvider);
			return blockLeakParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	static class Falling extends BlockLeakParticle {
		Falling(ClientWorld clientWorld, double d, double e, double f, Fluid fluid) {
			this(clientWorld, d, e, f, fluid, (int)(64.0 / (Math.random() * 0.8 + 0.2)));
		}

		Falling(ClientWorld world, double x, double y, double z, Fluid fluid, int maxAge) {
			super(world, x, y, z, fluid);
			this.maxAge = maxAge;
		}

		@Override
		protected void updateVelocity() {
			if (this.onGround) {
				this.markDead();
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class FallingDripstoneLavaFactory implements ParticleFactory<DefaultParticleType> {
		protected final SpriteProvider spriteProvider;

		public FallingDripstoneLavaFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle blockLeakParticle = new BlockLeakParticle.DrippingLava(clientWorld, d, e, f, Fluids.LAVA, ParticleTypes.FALLING_DRIPSTONE_LAVA);
			blockLeakParticle.setSprite(this.spriteProvider);
			return blockLeakParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class FallingDripstoneWaterFactory implements ParticleFactory<DefaultParticleType> {
		protected final SpriteProvider spriteProvider;

		public FallingDripstoneWaterFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle blockLeakParticle = new BlockLeakParticle.Dripping(clientWorld, d, e, f, Fluids.WATER, ParticleTypes.FALLING_DRIPSTONE_WATER);
			blockLeakParticle.setColor(0.2F, 0.3F, 1.0F);
			blockLeakParticle.setSprite(this.spriteProvider);
			return blockLeakParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	static class FallingHoney extends BlockLeakParticle.ContinuousFalling {
		FallingHoney(ClientWorld clientWorld, double d, double e, double f, Fluid fluid, ParticleEffect particleEffect) {
			super(clientWorld, d, e, f, fluid, particleEffect);
		}

		@Override
		protected void updateVelocity() {
			if (this.onGround) {
				this.markDead();
				this.world.addParticle(this.nextParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
				float f = MathHelper.nextBetween(this.random, 0.3F, 1.0F);
				this.world.playSound(this.x, this.y, this.z, SoundEvents.BLOCK_BEEHIVE_DRIP, SoundCategory.BLOCKS, f, 1.0F, false);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class FallingHoneyFactory implements ParticleFactory<DefaultParticleType> {
		protected final SpriteProvider spriteProvider;

		public FallingHoneyFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle blockLeakParticle = new BlockLeakParticle.FallingHoney(clientWorld, d, e, f, Fluids.EMPTY, ParticleTypes.LANDING_HONEY);
			blockLeakParticle.gravityStrength = 0.01F;
			blockLeakParticle.setColor(0.582F, 0.448F, 0.082F);
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

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle blockLeakParticle = new BlockLeakParticle.ContinuousFalling(clientWorld, d, e, f, Fluids.LAVA, ParticleTypes.LANDING_LAVA);
			blockLeakParticle.setColor(1.0F, 0.2857143F, 0.083333336F);
			blockLeakParticle.setSprite(this.spriteProvider);
			return blockLeakParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class FallingNectarFactory implements ParticleFactory<DefaultParticleType> {
		protected final SpriteProvider spriteProvider;

		public FallingNectarFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle blockLeakParticle = new BlockLeakParticle.Falling(clientWorld, d, e, f, Fluids.EMPTY);
			blockLeakParticle.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
			blockLeakParticle.gravityStrength = 0.007F;
			blockLeakParticle.setColor(0.92F, 0.782F, 0.72F);
			blockLeakParticle.setSprite(this.spriteProvider);
			return blockLeakParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class FallingObsidianTearFactory implements ParticleFactory<DefaultParticleType> {
		protected final SpriteProvider spriteProvider;

		public FallingObsidianTearFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle blockLeakParticle = new BlockLeakParticle.ContinuousFalling(clientWorld, d, e, f, Fluids.EMPTY, ParticleTypes.LANDING_OBSIDIAN_TEAR);
			blockLeakParticle.obsidianTear = true;
			blockLeakParticle.gravityStrength = 0.01F;
			blockLeakParticle.setColor(0.51171875F, 0.03125F, 0.890625F);
			blockLeakParticle.setSprite(this.spriteProvider);
			return blockLeakParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class FallingSporeBlossomFactory implements ParticleFactory<DefaultParticleType> {
		protected final SpriteProvider spriteProvider;
		private final Random random;

		public FallingSporeBlossomFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
			this.random = Random.create();
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			int j = (int)(64.0F / MathHelper.nextBetween(this.random, 0.1F, 0.9F));
			BlockLeakParticle blockLeakParticle = new BlockLeakParticle.Falling(clientWorld, d, e, f, Fluids.EMPTY, j);
			blockLeakParticle.gravityStrength = 0.005F;
			blockLeakParticle.setColor(0.32F, 0.5F, 0.22F);
			blockLeakParticle.setSprite(this.spriteProvider);
			return blockLeakParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class FallingWaterFactory implements ParticleFactory<DefaultParticleType> {
		protected final SpriteProvider spriteProvider;

		public FallingWaterFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle blockLeakParticle = new BlockLeakParticle.ContinuousFalling(clientWorld, d, e, f, Fluids.WATER, ParticleTypes.SPLASH);
			blockLeakParticle.setColor(0.2F, 0.3F, 1.0F);
			blockLeakParticle.setSprite(this.spriteProvider);
			return blockLeakParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	static class Landing extends BlockLeakParticle {
		Landing(ClientWorld clientWorld, double d, double e, double f, Fluid fluid) {
			super(clientWorld, d, e, f, fluid);
			this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
		}
	}

	@Environment(EnvType.CLIENT)
	public static class LandingDripstoneLavaFactory implements ParticleFactory<DefaultParticleType> {
		protected final SpriteProvider spriteProvider;

		public LandingDripstoneLavaFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle blockLeakParticle = new BlockLeakParticle.DripstoneLavaDrip(clientWorld, d, e, f, Fluids.LAVA, ParticleTypes.LANDING_LAVA);
			blockLeakParticle.setColor(1.0F, 0.2857143F, 0.083333336F);
			blockLeakParticle.setSprite(this.spriteProvider);
			return blockLeakParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class LandingHoneyFactory implements ParticleFactory<DefaultParticleType> {
		protected final SpriteProvider spriteProvider;

		public LandingHoneyFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle blockLeakParticle = new BlockLeakParticle.Landing(clientWorld, d, e, f, Fluids.EMPTY);
			blockLeakParticle.maxAge = (int)(128.0 / (Math.random() * 0.8 + 0.2));
			blockLeakParticle.setColor(0.522F, 0.408F, 0.082F);
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

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle blockLeakParticle = new BlockLeakParticle.Landing(clientWorld, d, e, f, Fluids.LAVA);
			blockLeakParticle.setColor(1.0F, 0.2857143F, 0.083333336F);
			blockLeakParticle.setSprite(this.spriteProvider);
			return blockLeakParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class LandingObsidianTearFactory implements ParticleFactory<DefaultParticleType> {
		protected final SpriteProvider spriteProvider;

		public LandingObsidianTearFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			BlockLeakParticle blockLeakParticle = new BlockLeakParticle.Landing(clientWorld, d, e, f, Fluids.EMPTY);
			blockLeakParticle.obsidianTear = true;
			blockLeakParticle.maxAge = (int)(28.0 / (Math.random() * 0.8 + 0.2));
			blockLeakParticle.setColor(0.51171875F, 0.03125F, 0.890625F);
			blockLeakParticle.setSprite(this.spriteProvider);
			return blockLeakParticle;
		}
	}
}
